package tendril.context.search;

import tendril.bean.Fallback;
import tendril.bean.Primary;

public class SingleRecipeSearchHandler<BEAN_TYPE> extends RecipeSearchHandler<BEAN_TYPE> {

	@Override
	public RecipeSearchResult<BEAN_TYPE> processResults() {
		if (hasPrimaryRecipes())
			return new RecipeSearchResult<BEAN_TYPE>(Primary.class.getSimpleName(), primaryRecipes);
		if (hasBasicRecipes())
			return new RecipeSearchResult<BEAN_TYPE>("basic", basicRecipes);
		return new RecipeSearchResult<BEAN_TYPE>(Fallback.class.getSimpleName(), fallbackRecipes);
	}

}
