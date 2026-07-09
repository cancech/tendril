package tendril.context.search;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.bean.recipe.AbstractRecipe;
import tendril.test.AbstractUnitTest;

/**
 * Test case for the {@link RecipeSearchResult}
 */
public class RecipeSearchResultTest extends AbstractUnitTest {

	// Mocks to use for testing
	@Mock
	private List<AbstractRecipe<Object, Object>> mockResult;
	
	// Instance to test
	private RecipeSearchResult<Object> result;
	
	/**
	 * @see tendril.test.AbstractUnitTest#prepareTest()
	 */
	@Override
	protected void prepareTest() {
		result = new RecipeSearchResult<>("type", mockResult);
	}
	
	/**
	 * Verify values are set
	 */
	@Test
	public void testValues() {
		Assertions.assertEquals("type", result.getType());
		Assertions.assertEquals(mockResult, result.getRecipes());
	}
}
