package tendril.context.search;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.bean.recipe.AbstractRecipe;
import tendril.test.AbstractUnitTest;
import tendril.test.assertions.CollectionAssert;

/**
 * Test case for the {@link RecipeSearchResult}
 */
public class RecipeSearchResultTest extends AbstractUnitTest {
	
	// Mocks to use for testing
	@Mock
	private AbstractRecipe<Object> mockPrimary1;
	@Mock
	private AbstractRecipe<Object> mockPrimary2;
	@Mock
	private AbstractRecipe<Object> mockPrimary3;
	@Mock
	private AbstractRecipe<Object> mockBasic1;
	@Mock
	private AbstractRecipe<Object> mockBasic2;
	@Mock
	private AbstractRecipe<Object> mockBasic3;
	@Mock
	private AbstractRecipe<Object> mockFallback1;
	@Mock
	private AbstractRecipe<Object> mockFallback2;
	@Mock
	private AbstractRecipe<Object> mockFallback3;
	
	// Instance to test
	private RecipeSearchHandler<Object> handler;

	@Override
	protected void prepareTest() {
		handler = new RecipeSearchHandler<>() {

			@Override
			public RecipeSearchResult<Object> processResults() {
				return null;
			}
			
		};
	}

	/**
	 * Verify that the result is empty by default
	 */
	@Test
	public void testEmptyResult() {
		Assertions.assertFalse(handler.hasPrimaryRecipes());
		CollectionAssert.assertEmpty(handler.getPrimaryRecipes());
		Assertions.assertFalse(handler.hasBasicRecipes());
		CollectionAssert.assertEmpty(handler.getBasicRecipes());
		Assertions.assertFalse(handler.hasFallbackRecipes());
		CollectionAssert.assertEmpty(handler.getFallbackRecipes());
	}

	/**
	 * Verify that a single result can be added to each category
	 */
	@Test
	public void testSingleResult() {
		testEmptyResult();
		
		// Add primary
		handler.addPrimaryRecipe(mockPrimary1);
		Assertions.assertTrue(handler.hasPrimaryRecipes());
		CollectionAssert.assertEquivalent(Collections.singletonList(mockPrimary1), handler.getPrimaryRecipes());
		Assertions.assertFalse(handler.hasBasicRecipes());
		CollectionAssert.assertEmpty(handler.getBasicRecipes());
		Assertions.assertFalse(handler.hasFallbackRecipes());
		CollectionAssert.assertEmpty(handler.getFallbackRecipes());
		
		// Add basic
		handler.addBasicRecipe(mockBasic1);
		Assertions.assertTrue(handler.hasPrimaryRecipes());
		CollectionAssert.assertEquivalent(Collections.singletonList(mockPrimary1), handler.getPrimaryRecipes());
		Assertions.assertTrue(handler.hasBasicRecipes());
		CollectionAssert.assertEquivalent(Collections.singletonList(mockBasic1), handler.getBasicRecipes());
		Assertions.assertFalse(handler.hasFallbackRecipes());
		CollectionAssert.assertEmpty(handler.getFallbackRecipes());
		
		// Add fallback
		handler.addFallbackRecipe(mockFallback1);
		Assertions.assertTrue(handler.hasPrimaryRecipes());
		CollectionAssert.assertEquivalent(Collections.singletonList(mockPrimary1), handler.getPrimaryRecipes());
		Assertions.assertTrue(handler.hasBasicRecipes());
		CollectionAssert.assertEquivalent(Collections.singletonList(mockBasic1), handler.getBasicRecipes());
		Assertions.assertTrue(handler.hasFallbackRecipes());
		CollectionAssert.assertEquivalent(Collections.singletonList(mockFallback1), handler.getFallbackRecipes());
	}

	/**
	 * Verify that a multiple results can be added to each category
	 */
	@Test
	public void testMultipleResults() {
		testEmptyResult();
		
		// Add basic
		handler.addBasicRecipe(mockBasic1);
		handler.addBasicRecipe(mockBasic2);
		handler.addBasicRecipe(mockBasic3);
		Assertions.assertFalse(handler.hasPrimaryRecipes());
		CollectionAssert.assertEmpty(handler.getPrimaryRecipes());
		Assertions.assertTrue(handler.hasBasicRecipes());
		CollectionAssert.assertEquivalent(Arrays.asList(mockBasic1, mockBasic2, mockBasic3), handler.getBasicRecipes());
		Assertions.assertFalse(handler.hasFallbackRecipes());
		CollectionAssert.assertEmpty(handler.getFallbackRecipes());
		
		// Add fallback
		handler.addFallbackRecipe(mockFallback1);
		handler.addFallbackRecipe(mockFallback2);
		handler.addFallbackRecipe(mockFallback3);
		Assertions.assertFalse(handler.hasPrimaryRecipes());
		CollectionAssert.assertEmpty(handler.getPrimaryRecipes());
		Assertions.assertTrue(handler.hasBasicRecipes());
		CollectionAssert.assertEquivalent(Arrays.asList(mockBasic1, mockBasic2, mockBasic3), handler.getBasicRecipes());
		Assertions.assertTrue(handler.hasFallbackRecipes());
		CollectionAssert.assertEquivalent(Arrays.asList(mockFallback1, mockFallback2, mockFallback3), handler.getFallbackRecipes());
		
		// Add primary
		handler.addPrimaryRecipe(mockPrimary1);
		handler.addPrimaryRecipe(mockPrimary2);
		handler.addPrimaryRecipe(mockPrimary3);
		Assertions.assertTrue(handler.hasPrimaryRecipes());
		CollectionAssert.assertEquivalent(Arrays.asList(mockPrimary1, mockPrimary2, mockPrimary3), handler.getPrimaryRecipes());
		Assertions.assertTrue(handler.hasBasicRecipes());
		CollectionAssert.assertEquivalent(Arrays.asList(mockBasic1, mockBasic2, mockBasic3), handler.getBasicRecipes());
		Assertions.assertTrue(handler.hasFallbackRecipes());
		CollectionAssert.assertEquivalent(Arrays.asList(mockFallback1, mockFallback2, mockFallback3), handler.getFallbackRecipes());
	}
}
