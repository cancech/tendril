package tendril.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tendril.TendrilStartupException;
import tendril.bean.duplicate.Blueprint;
import tendril.bean.qualifier.Descriptor;
import tendril.bean.recipe.AbstractRecipe;
import tendril.context.search.AllRecipeSearchHandler;
import tendril.context.search.RecipeSearchHandler;
import tendril.context.search.RecipeSearchResult;
import tendril.context.search.SearchType;
import tendril.context.search.SingleRecipeSearchHandler;

/**
 * Manager for available recipes
 */
class RecipeManager implements BeanDebugger {
	/** All recipes that have been registered */
	private final List<AbstractRecipe<?, ?>> allRecipes = new ArrayList<>();
	/** All recipes that were attempted to be registered but whose requirements were not met */
	private final List<AbstractRecipe<?, ?>> blockedReciped = new ArrayList<>();

	/** Registered recipes organized by name */
	private final Map<String, List<AbstractRecipe<?, ?>>> namedRecipes = new HashMap<>();
	/** Registered recipes organized by enum */
	private final Map<Enum<?>, List<AbstractRecipe<?, ?>>> enumRecipes = new HashMap<>();
	/** Registered recipes organized by qualifier */
	private final Map<Class<?>, List<AbstractRecipe<?, ?>>> qualifierRecipes = new HashMap<>();
	/** Registered recipes organized by blueprint */
	private final Map<Blueprint, List<AbstractRecipe<?, ?>>> blueprintRecipes = new HashMap<>();
	
	/** Flag for whether the manager is started and initialized */
	private boolean isStarted = false;
	
	/**
	 * Get the total number of beans registered and available for use.
	 * 
	 * @return int total number of beans
	 */
	int getBeanCount() {
		return allRecipes.size();
	}

	/**
	 * Register a new recipe with the manager
	 * 
	 * @param recipe {@link AbstractRecipe} to register
	 */
	void register(AbstractRecipe<?, ?> recipe) {
		allRecipes.add(recipe);
		
		if (isStarted)
			organizeRecipe(recipe);
	}
	
	/**
	 * Replace an existing recipe with a new one.
	 * 
	 * @param original {@link AbstractRecipe} that is to be replaced
	 * @param replacement {@link AbstractRecipe} that it is to be replaced with
	 */
	void replace(AbstractRecipe<?, ?> original, AbstractRecipe<?, ?> replacement) {
		if (isStarted)
			throw new TendrilStartupException("Recipe replacement is only possible before the manager is initialized");
			
		allRecipes.remove(original);
		replacement.updatePriorities(original);
		register(replacement);
	}
	
	/**
	 * Add a recipe that is blocked from being used. Note that this is purely for debug purposes.
	 * 
	 * @param recipe {@link AbstractRecipe} that is blocked from use.
	 */
	void addBlockedRecipe(AbstractRecipe<?, ?> recipe) {
		blockedReciped.add(recipe);
	}

	/**
	 * Initialize the manager after all recipes have been registered
	 */
	void init() {
		// Organize recipes for easier searching later
		for (AbstractRecipe<?, ?> r: allRecipes)
			organizeRecipe(r);
		isStarted = true;
		
		// Handle auto-create beans
		for (AbstractRecipe<?, ?> r: allRecipes) {
			if (r.isAutoCreate())
				r.get();
		}
	}
	
	/**
	 * Organize recipes to speed up searches and access.
	 * 
	 * @param recipe {@link AbstractRecipe} to organize
	 */
	private void organizeRecipe(AbstractRecipe<?, ?> recipe) {
		Descriptor<?> description = recipe.getDescription();
		namedRecipes.computeIfAbsent(description.getName(), (k) -> new ArrayList<>()).add(recipe);
		for (Enum<?> e: description.getEnumQualifiers())
			enumRecipes.computeIfAbsent(e, (k) -> new ArrayList<>()).add(recipe);
		for (Class<?> c: description.getQualifiers())
			qualifierRecipes.computeIfAbsent(c, (k) -> new ArrayList<>()).add(recipe);
		
		Blueprint blueprint = description.getBlueprint();
		if (blueprint != null)
			blueprintRecipes.computeIfAbsent(blueprint, (k) -> new ArrayList<>()).add(recipe);
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
	<BEAN_TYPE> RecipeSearchResult<BEAN_TYPE> findRecipes(Descriptor<BEAN_TYPE> descriptor, SearchType type) {
		RecipeSearchHandler<BEAN_TYPE> foundRecipes = type == SearchType.SINGLE_BEAN ? new SingleRecipeSearchHandler<>() : new AllRecipeSearchHandler<>();
		getMatches(descriptor).forEach((r) -> {
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
	
	/**
	 * Get recipes that match the specified metadata
	 * 
	 * @param descriptor {@link Descriptor} containing the metadata
	 * @return {@link List} of matching {@link AbstractRecipe}s
	 */
	private List<AbstractRecipe<?, ?>> getMatches(Descriptor<?> descriptor) {
		List<AbstractRecipe<?, ?>> matches = getByName(descriptor.getName());
		retainByBlueprint(matches, descriptor.getBlueprint());
		retainByEnum(matches, descriptor.getEnumQualifiers());
		retainByQualifier(matches, descriptor.getQualifiers());
		return matches;
	}
	
	/**
	 * Get list of all recipes which match the name indicated. Note that this will return a mutable list (i.e.: this can be manipulated without affecting
	 * the original recipe list source.
	 * 
	 * @param name {@link String} name of the recipe that is desired
	 * @return {@link List} of {@link AbstractRecipe} matching recipes
	 */
	private List<AbstractRecipe<?, ?>> getByName(String name) {
		// If no name is specified, then that means that "any" name will match
		if (name.isBlank())
			return new ArrayList<>(allRecipes);
		
		return new ArrayList<>(namedRecipes.getOrDefault(name, Collections.emptyList()));
	}
	
	/**
	 * Purge matching recipes that do not match the blueprint. Note that this will update the specified match list
	 * 
	 * @param matches {@link List} of {@link AbstractRecipe} matching recipes to update
	 * @param blueprint {@link Blueprint} to search for
	 */
	private void retainByBlueprint(List<AbstractRecipe<?, ?>> matches, Blueprint blueprint) {
		// If no blueprint is specified in the search, then ignore blueprints
		if (blueprint == null)
			return;
		
		List<AbstractRecipe<?, ?>> eMatches = blueprintRecipes.get(blueprint);
		if (eMatches == null) {
			matches.clear();
			return;
		} else {
			matches.retainAll(eMatches);
		}
	}
	
	/**
	 * Purge matching recipes that do not match the collection of marking enums. Note that this will update the specified match list.
	 * 
	 * @param matches {@link List} of {@link AbstractRecipe} matching recipes to update
	 * @param enums {@link Collection} of {@link Enum}s that are desired by the match
	 */
	private void retainByEnum(List<AbstractRecipe<?, ?>> matches, Collection<Enum<?>> enums) {
		for (Enum<?> e: enums) {
			List<AbstractRecipe<?, ?>> eMatches = enumRecipes.get(e);
			if (eMatches == null) {
				matches.clear();
				return;
			} else {
				matches.retainAll(eMatches);
			}
		}
	}
	
	/**
	 * Purge matching recipes that do not match the collection of qualifier annotations. Note that this will update the specified match list.
	 * 
	 * @param matches {@link List} of {@link AbstractRecipe} matching recipes to update
	 * @param enums {@link Collection} of {@link Class}es that are to identify the desired recipe
	 */
	private void retainByQualifier(List<AbstractRecipe<?, ?>> matches, Collection<Class<?>> qualifiers) {
		for (Class<?> e: qualifiers) {
			List<AbstractRecipe<?, ?>> qMatches = qualifierRecipes.get(e);
			if (qMatches == null) {
				matches.clear();
				return;
			} else {
				matches.retainAll(qMatches);
			}
		}
	}
	
	/**
	 * Get all original recipes that can be replaced by the descriptor
	 *   
	 * @param <BEAN_TYPE> indicating the type of the beans that are to be replaced
	 * @param descriptor  {@link Descriptor} containing the description of the beans that are to be replaced
	 * @param type        {@link SearchType} indicating the type of recipe search that is to be performed
	 * @return {@link RecipeSearchResult} containing all of the matching recipes
	 */
	@SuppressWarnings("unchecked")
	<BEAN_TYPE> RecipeSearchResult<BEAN_TYPE> findOriginalRecipes(Descriptor<BEAN_TYPE> descriptor, SearchType type) {
		RecipeSearchHandler<BEAN_TYPE> foundRecipes = type == SearchType.SINGLE_BEAN ? new SingleRecipeSearchHandler<>() : new AllRecipeSearchHandler<>();
		allRecipes.forEach((r) -> {
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
	 * @see tendril.context.BeanDebugger#printBeansNotCreated()
	 */
	@Override
	public void printBeansNotCreated() {
		List<AbstractRecipe<?,?>> notConstructed = new ArrayList<>();
		for (AbstractRecipe<?, ?> r: allRecipes) {
			if (r.isConstructed())
				continue;
			
			notConstructed.add(r);
		}
		
		System.err.println("*************************************************");
		System.err.println("***************** TENDRIL DEBUG *****************");
		System.err.println("*************************************************");
		System.err.println("Beans with unfulfilled requirements:");
		for (AbstractRecipe<?, ?> r: blockedReciped)
			System.err.println(" *** " + r.getDescription());
		System.err.println("-------------------------------------------------");
		System.err.println("Beans not constructed / referenced:");
		for (AbstractRecipe<?, ?> r: notConstructed)
			System.err.println(" *** " + r.getDescription());
	}
}
