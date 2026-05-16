package tendril.context.search;

import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.bean.recipe.AbstractRecipe;
import tendril.test.AbstractUnitTest;
import tendril.test.assertions.CollectionAssert;

/**
 * Test case for the {@link SingleRecipeSearchHandler}
 */
public class SingleRecipeSearchHandlerTest extends AbstractUnitTest {
	
	// Mocks to use for testing
	@Mock
	private AbstractRecipe<Object> mockRecipe1;
	@Mock
	private AbstractRecipe<Object> mockRecipe2;
	@Mock
	private AbstractRecipe<Object> mockRecipe3;
	@Mock
	private AbstractRecipe<Object> mockRecipe4;
	@Mock
	private AbstractRecipe<Object> mockRecipe5;
	@Mock
	private AbstractRecipe<Object> mockRecipe6;
	@Mock
	private AbstractRecipe<Object> mockRecipe7;
	@Mock
	private AbstractRecipe<Object> mockRecipe8;
	@Mock
	private AbstractRecipe<Object> mockRecipe9;
	
	// Instance to test
	private SingleRecipeSearchHandler<Object> handler;
	
	/**
	 * @see tendril.test.AbstractUnitTest#prepareTest()
	 */
	@Override
	protected void prepareTest() {
		handler = new SingleRecipeSearchHandler<>();
	}

	/**
	 * Verify result when no search results at all
	 */
	@Test
	public void testNoResults() {
		RecipeSearchResult<Object> result = handler.processResults();
		Assertions.assertEquals("Fallback", result.getType());
		CollectionAssert.assertEmpty(result.getRecipes());
	}

	/**
	 * Verify result with primary
	 */
	@Test
	public void testPrimaryResults() {
		handler.addPrimaryRecipe(mockRecipe1);
		handler.addPrimaryRecipe(mockRecipe2);
		handler.addPrimaryRecipe(mockRecipe3);
		
		RecipeSearchResult<Object> result = handler.processResults();
		
		Assertions.assertEquals("Primary", result.getType());
		CollectionAssert.assertEquals(Arrays.asList(mockRecipe1, mockRecipe2, mockRecipe3), result.getRecipes());
	}

	/**
	 * Verify result with primary and basic
	 */
	@Test
	public void testPrimaryandBasicResults() {
		handler.addPrimaryRecipe(mockRecipe1);
		handler.addPrimaryRecipe(mockRecipe2);
		handler.addPrimaryRecipe(mockRecipe3);
		handler.addBasicRecipe(mockRecipe4);
		handler.addBasicRecipe(mockRecipe5);
		handler.addBasicRecipe(mockRecipe6);
		
		RecipeSearchResult<Object> result = handler.processResults();
		
		Assertions.assertEquals("Primary", result.getType());
		CollectionAssert.assertEquals(Arrays.asList(mockRecipe1, mockRecipe2, mockRecipe3), result.getRecipes());
	}

	/**
	 * Verify result with primary, basic, and fallback
	 */
	@Test
	public void testPrimaryBasicFallbackResults() {
		handler.addPrimaryRecipe(mockRecipe1);
		handler.addPrimaryRecipe(mockRecipe2);
		handler.addPrimaryRecipe(mockRecipe3);
		handler.addBasicRecipe(mockRecipe4);
		handler.addBasicRecipe(mockRecipe5);
		handler.addBasicRecipe(mockRecipe6);
		handler.addFallbackRecipe(mockRecipe7);
		handler.addFallbackRecipe(mockRecipe8);
		handler.addFallbackRecipe(mockRecipe9);
		
		RecipeSearchResult<Object> result = handler.processResults();
		
		Assertions.assertEquals("Primary", result.getType());
		CollectionAssert.assertEquals(Arrays.asList(mockRecipe1, mockRecipe2, mockRecipe3), result.getRecipes());
	}

	/**
	 * Verify result with basic, and fallback
	 */
	@Test
	public void testBasicFallbackResults() {
		handler.addBasicRecipe(mockRecipe4);
		handler.addBasicRecipe(mockRecipe5);
		handler.addBasicRecipe(mockRecipe6);
		handler.addFallbackRecipe(mockRecipe7);
		handler.addFallbackRecipe(mockRecipe8);
		handler.addFallbackRecipe(mockRecipe9);
		
		RecipeSearchResult<Object> result = handler.processResults();
		
		Assertions.assertEquals("basic", result.getType());
		CollectionAssert.assertEquals(Arrays.asList(mockRecipe4, mockRecipe5, mockRecipe6), result.getRecipes());
	}

	/**
	 * Verify result with fallback
	 */
	@Test
	public void testFallbackResults() {
		handler.addFallbackRecipe(mockRecipe7);
		handler.addFallbackRecipe(mockRecipe8);
		handler.addFallbackRecipe(mockRecipe9);
		
		RecipeSearchResult<Object> result = handler.processResults();
		
		Assertions.assertEquals("Fallback", result.getType());
		CollectionAssert.assertEquals(Arrays.asList(mockRecipe7, mockRecipe8, mockRecipe9), result.getRecipes());
	}
}
