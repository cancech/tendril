package tendril.context.search;

import java.util.ArrayList;
import java.util.List;

import tendril.bean.Fallback;
import tendril.bean.Primary;
import tendril.bean.recipe.AbstractRecipe;

/**
 * Abstract handler for performing recipe searches.
 * 
 * @param <BEAN_TYPE> of the recipes which are being sought
 */
public abstract class RecipeSearchHandler<BEAN_TYPE> {
	/** List of the primary recipes that were found during the search */
	protected final List<AbstractRecipe<BEAN_TYPE, BEAN_TYPE>> primaryRecipes = new ArrayList<>();
	/** List of the basic (no explicit type) recipes that were found during the search */
	protected final List<AbstractRecipe<BEAN_TYPE, BEAN_TYPE>> basicRecipes = new ArrayList<>();
	/** List of the fallback recipes that were found during the search */
	protected final List<AbstractRecipe<BEAN_TYPE, BEAN_TYPE>> fallbackRecipes = new ArrayList<>();
	
	/**
	 * CTOR
	 */
	RecipeSearchHandler() {
	}

	/**
	 * Add a recipe for a {@link Primary} bean that matches the requested search
	 * 
	 * @param recipe {@link AbstractRecipe} for building the {@link Primary} bean
	 */
	public void addPrimaryRecipe(AbstractRecipe<BEAN_TYPE, BEAN_TYPE> recipe) {
		primaryRecipes.add(recipe);
	}

	/**
	 * Add a recipe for a basic (no explicit type) bean that matches the requested search
	 * 
	 * @param recipe {@link AbstractRecipe} for building the basic bean
	 */
	public void addBasicRecipe(AbstractRecipe<BEAN_TYPE, BEAN_TYPE> recipe) {
		basicRecipes.add(recipe);
	}

	/**
	 * Add a recipe for a {@link Fallback} bean that matches the requested search
	 * 
	 * @param recipe {@link AbstractRecipe} for building the {@link Fallback} bean
	 */
	public void addFallbackRecipe(AbstractRecipe<BEAN_TYPE, BEAN_TYPE> recipe) {
		fallbackRecipes.add(recipe);
	}
	
	/**
	 * Check whether the result contains any {@link Primary} bean recipes.
	 * 
	 * @return true if it does
	 */
	public boolean hasPrimaryRecipes() {
		return !primaryRecipes.isEmpty();
	}
	
	/**
	 * Get the full list of {@link Primary} beans that were found during the search
	 * 
	 * @return {@link List} of {@link AbstractRecipe} instances which can be used to build the {@link Primary} bean
	 */
	public List<AbstractRecipe<BEAN_TYPE, BEAN_TYPE>> getPrimaryRecipes() {
		return primaryRecipes;
	}
	
	/**
	 * Check whether the result contains any basic(no explicit type) bean recipes.
	 * 
	 * @return true if it does
	 */
	public boolean hasBasicRecipes() {
		return !basicRecipes.isEmpty();
	}

	/**
	 * Get the full list of basic (no explicit type) beans that were found during the search
	 * 
	 * @return {@link List} of {@link AbstractRecipe} instances which can be used to build the basic bean
	 */
	public List<AbstractRecipe<BEAN_TYPE, BEAN_TYPE>> getBasicRecipes() {
		return basicRecipes;
	}
	
	/**
	 * Check whether the result contains any {@link Fallback} bean recipes.
	 * 
	 * @return true if it does
	 */
	public boolean hasFallbackRecipes() {
		return !fallbackRecipes.isEmpty();
	}

	/**
	 * Get the full list of {@link Fallback} beans that were found during the search
	 * 
	 * @return {@link List} of {@link AbstractRecipe} instances which can be used to build the {@link Fallback} bean
	 */
	public List<AbstractRecipe<BEAN_TYPE, BEAN_TYPE>> getFallbackRecipes() {
		return fallbackRecipes;
	}
	
	/**
	 * Process the search results to find the final resulting options
	 * 
	 * @return {@link RecipeSearchResult} containing the search results
	 */
	public abstract RecipeSearchResult<BEAN_TYPE> processResults();
}
