package tendril.context.search;

import java.util.ArrayList;
import java.util.List;

import tendril.bean.recipe.AbstractRecipe;

/**
 * Handles the search for recipes when all matching recipes are desired.
 * 
 * @param <BEAN_TYPE> of the recipes which are being sought
 */
public class AllRecipeSearchHandler<BEAN_TYPE> extends RecipeSearchHandler<BEAN_TYPE> {

	/**
	 * CTOR
	 */
	public AllRecipeSearchHandler() {
	}

	/**
	 * @see tendril.context.search.RecipeSearchHandler#processResults()
	 */
	@Override
	public RecipeSearchResult<BEAN_TYPE> processResults() {
		List<AbstractRecipe<BEAN_TYPE, BEAN_TYPE>> recipes = new ArrayList<>();
		// First and foremost primary and basic recipes
		recipes.addAll(primaryRecipes);
		recipes.addAll(basicRecipes);
		
		// Only if none so far, then fallbacks should be included
		if (recipes.isEmpty())
			recipes.addAll(fallbackRecipes);
		
		return new RecipeSearchResult<BEAN_TYPE>("All", recipes);
	}

}
