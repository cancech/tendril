/*
 * Copyright 2024 Jaroslav Bosak
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://opensource.org/license/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tendril.context;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import tendril.BeanReplacementException;
import tendril.BeanRetrievalException;
import tendril.TendrilStartupException;
import tendril.bean.duplicate.Blueprint;
import tendril.bean.qualifier.Descriptor;
import tendril.bean.recipe.AbstractRecipe;
import tendril.bean.recipe.ConfigurationRecipe;
import tendril.bean.recipe.WrapperRecipe;
import tendril.bean.requirement.Requirement;
import tendril.context.launch.TendrilRunner;
import tendril.context.search.AllRecipeSearchHandler;
import tendril.context.search.RecipeSearchHandler;
import tendril.context.search.RecipeSearchResult;
import tendril.context.search.SearchType;
import tendril.context.search.SingleRecipeSearchHandler;
import tendril.processor.registration.RegistryFile;
import tendril.processor.registration.ReplacementRegistryFile;
import tendril.processor.registration.RunnerFile;
import tendril.util.TendrilStringUtil;
import tendril.util.TendrilUtil;

/**
 * The core element within the {@link ApplicationContext} which is responsible for the bulk of the Dependency Injection capability. This tracks all beans (via their recipes) and allows for their
 * access. It is not expected that client code will ever touch or manipulate the engine directly, with the expected interactions being via recipes and their beans.
 */
public class Engine implements ApplicationContext {

	/** Logger for creating log messages when running */
	private static Logger LOGGER = Logger.getLogger(Engine.class.getSimpleName());

	/** List of all blueprints which have been added */
	private final List<Blueprint> blueprints = new ArrayList<>();
	/** Cache of all blueprints which have been added for a given class type */
	private final Map<Class<? extends Blueprint>, List<Blueprint>> blueprintsForClass = new HashMap<>();
	/** All recipes that have been registered */
	private final List<AbstractRecipe<?, ?>> recipes = new ArrayList<>();
	/** All replacement recipes that are defined in a configuration */
	private final List<Map<String, AbstractRecipe<?, ?>>> configReplacements = new ArrayList<>();
	/** List of environments that are applied to the context */
	private List<String> environments = new ArrayList<>();
	/** Flag for whether or not the engine has been started */
	private boolean isStarted = false;

	/**
	 * CTOR
	 */
	public Engine() {
		String cliEnvs = System.getProperty("environments");
		if (cliEnvs != null && !cliEnvs.isBlank())
			addEnvironments(cliEnvs.split(","));
	}

	/**
	 * Set the environments for the context
	 * 
	 * @param envs {@link String}... that are applied
	 */
	void addEnvironments(String... envs) {
		// Environments can only be set before starting the engine
		if (isStarted)
			throw new RuntimeException("Environments can only be set before starting the context");

		environments.addAll(Arrays.asList(envs));
	}

	/**
	 * Get the current list of environments for the context
	 * 
	 * @return {@link List} of applied {@link String} environment names
	 */
	List<String> getEnvironments() {
		return environments;
	}

	/**
	 * Initialize the engine by reading the list of all known recipes and registering them with the engine. Each recipe object is created, though the bean contained within is not created until it
	 * becomes necessary to do so (i.e.: accessed by a Consumer).
	 */
	void init() {
		isStarted = true;

		LOGGER.fine("Initializing with environments [" + TendrilStringUtil.join(environments) + "]");
		try {
			// First load all "original" recipes
			processRegistry(RegistryFile.read(), (recipe, instance) -> {
				if (!tryAddConfiguration(recipe, instance))
					tryAddRecipe(recipe, instance);
			});

			// Replace those which have available replacements
			processRegistry(ReplacementRegistryFile.read(), (recipe, instance) -> tryReplaceRecipe(recipe, instance));

			// Finally once everything else has been loaded, load the various replacement recipes from config files
			processConfigReplacements();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Inject the ApplicationContext
		recipes.add(new WrapperRecipe<>(this, this, new Descriptor<>(ApplicationContext.class)));
	}

	/**
	 * Functional interface to allow different recipe loading methods to be employed when processing the registry. This is explicitly tied to the processRegistry method.
	 */
	private interface RecipeLoader {
		/**
		 * Load the recipe into the engine.
		 * 
		 * @param recipeName {@link String} the name or identifier of the recipe
		 * @param recipe {@link Object} instance
		 */
		void load(String recipeName, Object recipe);
	}

	/**
	 * Process recipe classes that appear in a registry
	 * 
	 * @param recipes {@link Set} of {@link String} recipe class names to load
	 * @param loader  {@link RecipeLoader} which is to load the recipes
	 */
	private void processRegistry(Set<String> recipes, RecipeLoader loader) {
		for (String recipe : recipes) {
			try {
				// Add the recipe from the registry file
				loader.load(recipe, Class.forName(recipe).getDeclaredConstructor(Engine.class).newInstance(this));
			} catch (ClassCastException e) {
				LOGGER.severe(recipe + " is not a proper recipe (does not extend " + AbstractRecipe.class.getName() + ")");
			} catch (ClassNotFoundException e) {
				LOGGER.severe("Unable to find class " + recipe);
			} catch (NoSuchMethodException | InstantiationException | IllegalArgumentException | InvocationTargetException e) {
				LOGGER.severe("Unable to create " + recipe);
			} catch (IllegalAccessException | SecurityException e) {
				LOGGER.severe("Unable to access " + recipe);
			}
		}
	}

	/**
	 * Process the replacement recipes that have been delayed during configuration processing
	 */
	private void processConfigReplacements() {
		for (Map<String, AbstractRecipe<?, ?>> m : configReplacements) {
			m.forEach((name, r) -> tryReplaceRecipe(name, r));
		}

		configReplacements.clear();
	}

	/**
	 * Try to add the recipe as though it were a configuration
	 * 
	 * @param recipe {@link String} the fully qualified name of the recipe
	 * @param object {@link Object} recipe instance
	 * @return boolean true if the recipe was for a configuration and it was processed (false if not configuration and not processed)
	 */
	private boolean tryAddConfiguration(String recipe, Object object) {
		if (!(object instanceof ConfigurationRecipe))
			return false;

		ConfigurationRecipe<?> config = (ConfigurationRecipe<?>) object;
		if (requirementsMet(config)) {
			LOGGER.fine("Loading configuration " + recipe);
			// Regular beans can be processed immediately
			config.getNestedRecipes().forEach((name, r) -> tryAddRecipe(recipe + "::" + name, r));
			// Replacements must be delayed until later
			Map<String, AbstractRecipe<?, ?>> delayedReplacements = new HashMap<>();
			config.getNestedReplacementRecipes().forEach((name, r) -> {
				delayedReplacements.put(recipe + "::" + name, r);
			});
			configReplacements.add(delayedReplacements);
		} else {
			LOGGER.fine("Configuration requirements not met " + recipe);
		}

		return true;
	}

	/**
	 * Try to add the recipe as though it were a recipe for an individual bean
	 * 
	 * @param name   {@link String} the fully qualified name of the recipe
	 * @param object {@link Object} recipe instance
	 */
	private void tryAddRecipe(String name, Object object) {
		AbstractRecipe<?, ?> recipe = (AbstractRecipe<?, ?>) object;

		if (requirementsMet(recipe)) {
			recipes.add(recipe);
			LOGGER.fine("Loaded recipe " + name);
		} else {
			LOGGER.fine("Bean requirements not met" + recipe);
		}
	}

	/**
	 * Try to replace an existing recipe with the indicated replacement
	 * 
	 * @param name   {@link String} the fully qualified name of the recipe
	 * @param object {@link Object} replacement recipe instance
	 */
	@SuppressWarnings("unchecked")
	private void tryReplaceRecipe(String name, Object object) {
		if (object instanceof AbstractRecipe recipe) {
			if (requirementsMet(recipe)) {
				// Find the recipe this is to replace
				Descriptor<?> description = recipe.getDescription();
				try {
					AbstractRecipe<?, ?> orig = getRecipe(description, findOriginalRecipes(description, SearchType.SINGLE_BEAN));
					recipes.remove(orig);
					recipe.updatePriorities(orig);
					description.updateFrom(orig.getDescription());
					recipes.add(recipe);
				} catch (Exception ex) {
					throw new BeanReplacementException("Failed to apply replacement bean " + name, ex);
				}
			} else {
				LOGGER.fine("Replacement bean requirements not met" + recipe);
			}
		} else {
			LOGGER.severe("Unable to load malformed recipe " + name);
		}
	}

	/**
	 * Check if the requirements for the recipe have been met
	 * 
	 * @param recipe {@link AbstractRecipe} to check
	 * @return boolean true if all requirements for the recipe have been met
	 */
	boolean requirementsMet(AbstractRecipe<?, ?> recipe) {
		return requirementsMet(recipe, recipe.getEnvironmentRequirement(), environments) && requirementsMet(recipe, recipe.getPropertyRequirement(), systemPropertyList());
	}

	/**
	 * Get a list of system properties as String (i.e.: list of the property names that have been applied)
	 * 
	 * @return {@link List} of {@link String}s
	 */
	protected List<String> systemPropertyList() {
		List<String> propNames = new ArrayList<>();
		for (Object o : System.getProperties().keySet())
			propNames.add(o.toString());
		return propNames;
	}

	/**
	 * Check if the specific requirement has been met.
	 * 
	 * @param recipe {@link AbstractRecipe} being checked
	 * @param req    {@link Requirement} to be validated
	 * @param values {@link List} of {@link String} values that are to be checked against
	 * @return boolean {@code true} if the requirement has been met
	 */
	private boolean requirementsMet(AbstractRecipe<?, ?> recipe, Requirement req, List<String> values) {
		List<String> reqEnvs = req.getRequired();
		List<String> notReqEnvs = req.getRequiredNot();

		// Make sure that all required environments are present
		if (!values.containsAll(reqEnvs)) {
			LOGGER.fine("Unable to load " + recipe + " because not all required environments [" + TendrilStringUtil.join(reqEnvs) + "] are met.");
			return false;
		}

		// Make sure that none of the not required environments are present
		if (TendrilUtil.containsAny(values, notReqEnvs)) {
			LOGGER.fine("Unable to load " + recipe + " because at least one of the not required environments [" + TendrilStringUtil.join(notReqEnvs) + "] is present");
			return false;
		}

		// Make sure that all of the "one of" environments are present
		for (List<String> tuple : req.getRequiredOneOf()) {
			if (!TendrilUtil.containsAny(values, tuple)) {
				LOGGER.fine("Unable to load " + recipe + " because at least one of the required environments [" + TendrilStringUtil.join(tuple) + "] is not present");
				return false;
			}
		}

		return true;
	}

	/**
	 * Get the number of beans that are registered with the engine
	 * 
	 * @return int the number of beans
	 */
	public int getBeanCount() {
		return recipes.size();
	}

	/**
	 * @see tendril.context.ApplicationContext#count(tendril.bean.qualifier.Descriptor)
	 */
	@Override
	public <BEAN_TYPE> int count(Descriptor<BEAN_TYPE> descriptor) {
		RecipeSearchResult<BEAN_TYPE> matchingRecipes = findRecipes(descriptor, SearchType.ALL_BEANS);
		return matchingRecipes.getRecipes().size();
	}
	
	/**
	 * @see tendril.context.ApplicationContext#registerBean(java.lang.Object, tendril.bean.qualifier.Descriptor)
	 */
	@Override
	public <BEAN_TYPE> void registerBean(BEAN_TYPE bean, Descriptor<BEAN_TYPE> descriptor) {
		recipes.add(new WrapperRecipe<>(this, bean, descriptor));
	}

	/**
	 * @see tendril.context.ApplicationContext#getBean(tendril.bean.qualifier.Descriptor)
	 */
	@Override
	public <BEAN_TYPE> BEAN_TYPE getBean(Descriptor<BEAN_TYPE> descriptor) {
		return (BEAN_TYPE) getRecipe(descriptor, findRecipes(descriptor, SearchType.SINGLE_BEAN)).get();
	}

	/**
	 * Retrieve the one recipe from the matches. Primarily performs error checking and throws a {@link BeanRetrievalException} if more than one match is present.
	 * 
	 * @param descriptor      {@link Descriptor} containing the description of the bean that is to be retrieved
	 * @param matchingRecipes {@link RecipeSearchResult} containing the matching beans
	 * @return The single matching recipe
	 * @throws BeanRetrievalException if there is an issue retrieving the desired bean
	 */
	@SuppressWarnings("unchecked")
	private <BEAN_TYPE> AbstractRecipe<BEAN_TYPE, BEAN_TYPE> getRecipe(Descriptor<BEAN_TYPE> descriptor, RecipeSearchResult<?> matchingRecipes) throws BeanReplacementException {
		List<?> matches = matchingRecipes.getRecipes();
		if (matches.isEmpty())
			throw new BeanRetrievalException(descriptor);
		if (matches.size() > 1)
			throw new BeanRetrievalException(descriptor, (List<AbstractRecipe<BEAN_TYPE, BEAN_TYPE>>) matches, matchingRecipes.getType());

		return (AbstractRecipe<BEAN_TYPE, BEAN_TYPE>) matches.get(0);
	}

	/**
	 * @see tendril.context.ApplicationContext#getAllBeans(tendril.bean.qualifier.Descriptor)
	 */
	@Override
	public <BEAN_TYPE> List<BEAN_TYPE> getAllBeans(Descriptor<BEAN_TYPE> descriptor) {
		List<BEAN_TYPE> beans = new ArrayList<>();
		RecipeSearchResult<BEAN_TYPE> matchingRecipes = findRecipes(descriptor, SearchType.ALL_BEANS);
		matchingRecipes.getRecipes().forEach(r -> beans.add(r.get()));
		return beans;
	}

	/**
	 * Get all of the recipes which are available for the desired type. This includes exact matches (i.e.: recipe provides exactly the desired class) as well as classes which can be referenced as the
	 * desired type (i.e.: they are higher in the hierarchy of the desired type).
	 * 
	 * @param <BEAN_TYPE> indicating the type of the beans that are to be retrieved
	 * @param descriptor  {@link Descriptor} containing the description of the beans that are to be retrieved
	 * @param type        {@link SearchType} indicating the type of recipe search that is to be performed
	 * @return {@link RecipeSearchResult} containing all of the matching recipes
	 */
	@SuppressWarnings("unchecked")
	private <BEAN_TYPE> RecipeSearchResult<BEAN_TYPE> findRecipes(Descriptor<BEAN_TYPE> descriptor, SearchType type) {
		RecipeSearchHandler<BEAN_TYPE> foundRecipes = type == SearchType.SINGLE_BEAN ? new SingleRecipeSearchHandler<>() : new AllRecipeSearchHandler<>();
		recipes.forEach((r) -> {
			if (r.getDescription().matches(descriptor)) {
				if (r.isPrimary())
					foundRecipes.addPrimaryRecipe((AbstractRecipe<BEAN_TYPE, BEAN_TYPE>) r);
				else if (r.isFallback())
					foundRecipes.addFallbackRecipe((AbstractRecipe<BEAN_TYPE, BEAN_TYPE>) r);
				else
					foundRecipes.addBasicRecipe((AbstractRecipe<BEAN_TYPE, BEAN_TYPE>) r);
			}
		});

		return foundRecipes.processResults();
	}

	@SuppressWarnings("unchecked")
	private <BEAN_TYPE> RecipeSearchResult<BEAN_TYPE> findOriginalRecipes(Descriptor<BEAN_TYPE> descriptor, SearchType type) {
		RecipeSearchHandler<BEAN_TYPE> foundRecipes = type == SearchType.SINGLE_BEAN ? new SingleRecipeSearchHandler<>() : new AllRecipeSearchHandler<>();
		recipes.forEach((r) -> {
			if (r.getDescription().replacedBy(descriptor)) {
				if (r.isPrimary())
					foundRecipes.addPrimaryRecipe((AbstractRecipe<BEAN_TYPE, BEAN_TYPE>) r);
				else if (r.isFallback())
					foundRecipes.addFallbackRecipe((AbstractRecipe<BEAN_TYPE, BEAN_TYPE>) r);
				else
					foundRecipes.addBasicRecipe((AbstractRecipe<BEAN_TYPE, BEAN_TYPE>) r);
			}
		});

		return foundRecipes.processResults();
	}

	/**
	 * Add a blueprint to drive bean duplication
	 * 
	 * @param driver {@link Blueprint} to add
	 */
	void addBlueprint(Blueprint driver) {
		// Blueprints can only be added before starting the engine
		if (isStarted)
			throw new RuntimeException("Blueprints can only be added before starting the context");

		blueprints.add(driver);
	}

	/**
	 * Get all blueprints which have been applied for the given {@code BLUEPRINT_TYPE}. This will include all blueprints which are <i>castable</i> to the indicated {@link Class} not just those
	 * which are the exact {@link Class}.
	 * 
	 * @param <BLUEPRINT_TYPE> The {@link Blueprint} implementing class which is to be retrieved
	 * @param blueprintClass   {@link Class} representing the type which is desired
	 * @return {@link List} of dynamic blueprints which are castable to the desired type
	 */
	@SuppressWarnings("unchecked")
	public <BLUEPRINT_TYPE extends Blueprint> List<BLUEPRINT_TYPE> getBlueprints(Class<BLUEPRINT_TYPE> blueprintClass) {
		cacheBlueprintsForClass(blueprintClass);
		return (List<BLUEPRINT_TYPE>) blueprintsForClass.get(blueprintClass);
	}

	/**
	 * Update the cache so that blueprints for the indicated {@link Class} can be easily retrieved in the future.
	 * 
	 * @param blueprintClass {@link Class} of the blueprint which is to be cached
	 */
	private void cacheBlueprintsForClass(Class<? extends Blueprint> blueprintClass) {
		// Nothing to do if this had been cached previously
		if (blueprintsForClass.containsKey(blueprintClass))
			return;

		// Find all classes which can be cast to the desired blueprint class
		List<Blueprint> matches = new ArrayList<>();
		for (Blueprint b : blueprints) {
			if (blueprintClass.isInstance(b))
				matches.add(b);
		}
		// Save them for future retrieval
		blueprintsForClass.put(blueprintClass, matches);
	}
	/**
	 * @see tendril.context.ApplicationContext#start()
	 */
	@Override
    public void start() {
        try {
            List<AbstractRecipe<?, ?>> runnerRecipes = new ArrayList<>();
            for (String runnerClass: RunnerFile.read()) {
                AbstractRecipe<?, ?> runnerRecipe = (AbstractRecipe<?, ?>)Class.forName(runnerClass).getDeclaredConstructor(Engine.class).newInstance(this);
                if (requirementsMet(runnerRecipe))
                    runnerRecipes.add(runnerRecipe);
            }

            if (runnerRecipes.isEmpty())
                throw new TendrilStartupException("Exactly one runner is required to start the application, however none can be loaded.");
            else if (runnerRecipes.size() > 1)
                throw new TendrilStartupException("Exactly one runner is required to start the application, however " + runnerRecipes.size() + " can be loaded [" +
                        TendrilStringUtil.join(runnerRecipes, r -> r.getDescription().getBeanClass().getName()) + "].");
            
            TendrilRunner runner = (TendrilRunner) runnerRecipes.get(0).get();
            runner.run();
        } catch (IOException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException |
                SecurityException | ClassNotFoundException e) {
            throw new TendrilStartupException(e);
        }
	}
}
