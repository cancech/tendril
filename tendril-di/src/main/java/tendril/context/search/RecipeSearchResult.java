package tendril.context.search;

import java.util.List;

import tendril.bean.Fallback;
import tendril.bean.Primary;
import tendril.bean.recipe.AbstractRecipe;

/**
 * Wrapper class for the bean recipes that are found when searching for one or more beans. The matching recipes will be all stored within this result, grouped by their respective type:
 * <ul>
 * 		<li>{@link Primary}</li>
 * 		<li>{@link Fallback}</li>
 * 		<li>Basic (no explicit type)</li>
 * </ul>
 * 
 * @param <BEAN_TYPE> indicating the type of bean that the recipe is to create
 */
public class RecipeSearchResult<BEAN_TYPE> {

	private final String type;
	private final List<AbstractRecipe<BEAN_TYPE>> result;
	
	public RecipeSearchResult(String type, List<AbstractRecipe<BEAN_TYPE>> result) {
		this.type = type;
		this.result = result;
	}
	
	public String getType() {
		return type;
	}
	
	public List<AbstractRecipe<BEAN_TYPE>> getRecipes() {
		return result;
	}
}
