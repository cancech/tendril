package tendril.context.search;

import tendril.bean.Fallback;
import tendril.bean.Primary;

/**
 * Handles the search for recipes when a single matching recipe is desired.
 * 
 * @param <BEAN_TYPE> of the recipe which is sought
 */
public class SingleRecipeSearchHandler<BEAN_TYPE> extends RecipeSearchHandler<BEAN_TYPE> {
	
	/**
	 * CTOR
	 */
	public SingleRecipeSearchHandler() {
	}
	
	/**
	 * @see tendril.context.search.RecipeSearchHandler#processResults()
	 */
	@Override
	public RecipeSearchResult<BEAN_TYPE> processResults() {
		if (hasPrimaryRecipes())
			return new RecipeSearchResult<BEAN_TYPE>(Primary.class.getSimpleName(), primaryRecipes);
		if (hasBasicRecipes())
			return new RecipeSearchResult<BEAN_TYPE>("basic", basicRecipes);
		return new RecipeSearchResult<BEAN_TYPE>(Fallback.class.getSimpleName(), fallbackRecipes);
	}

}
