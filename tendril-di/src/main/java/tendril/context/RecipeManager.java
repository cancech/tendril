package tendril.context;

import java.util.ArrayList;
import java.util.List;

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
	}
	
	/**
	 * Replace an existing recipe with a new one.
	 * 
	 * @param original {@link AbstractRecipe} that is to be replaced
	 * @param replacement {@link AbstractRecipe} that it is to be replaced with
	 */
	void replace(AbstractRecipe<?, ?> original, AbstractRecipe<?, ?> replacement) {
		allRecipes.remove(original);
		replacement.updatePriorities(original);
		allRecipes.add(replacement);
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
		// Handle auto-create beans
		for (AbstractRecipe<?, ?> r: allRecipes) {
			if (r.isAutoCreate())
				r.get();
		}
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
		// TODO speed this up
		RecipeSearchHandler<BEAN_TYPE> foundRecipes = type == SearchType.SINGLE_BEAN ? new SingleRecipeSearchHandler<>() : new AllRecipeSearchHandler<>();
		allRecipes.forEach((r) -> {
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
