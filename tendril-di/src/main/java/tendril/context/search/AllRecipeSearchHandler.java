package tendril.context.search;

import java.util.ArrayList;
import java.util.List;

import tendril.bean.recipe.AbstractRecipe;

public class AllRecipeSearchHandler<BEAN_TYPE> extends RecipeSearchHandler<BEAN_TYPE> {

	@Override
	public RecipeSearchResult<BEAN_TYPE> processResults() {
		List<AbstractRecipe<BEAN_TYPE>> recipes = new ArrayList<>();
		// First and foremost primary and basic recipes
		recipes.addAll(primaryRecipes);
		recipes.addAll(basicRecipes);
		
		// Only if none so far, then fallbacks should be included
		if (recipes.isEmpty())
			recipes.addAll(fallbackRecipes);
		
		return new RecipeSearchResult<BEAN_TYPE>("All", recipes);
	}

}
