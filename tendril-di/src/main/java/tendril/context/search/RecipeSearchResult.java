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
	// The type of result
	private final String type;
	// The list of resulting recipes
	private final List<AbstractRecipe<BEAN_TYPE>> result;
	
	/**
	 * CTOR
	 * 
	 * @param type {@link String} representing the type of result retrieved
	 * @param result {@link List} of {@link AbstractRecipe}s which were found in the search
	 */
	public RecipeSearchResult(String type, List<AbstractRecipe<BEAN_TYPE>> result) {
		this.type = type;
		this.result = result;
	}
	
	/**
	 * Get the type of result
	 * 
	 * @return {@link String} indicating the type
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * Get the recipes found in the search
	 * 
	 * @return {@link List} of {@link AbstractRecipe}s
	 */
	public List<AbstractRecipe<BEAN_TYPE>> getRecipes() {
		return result;
	}
}
