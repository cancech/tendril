package tendril.context;

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
	private RecipeSearchResult<Object> result;

	@Override
	protected void prepareTest() {
		result = new RecipeSearchResult<>();
	}

	/**
	 * Verify that the result is empty by default
	 */
	@Test
	public void testEmptyResult() {
		Assertions.assertFalse(result.hasPrimaryRecipes());
		CollectionAssert.assertEmpty(result.getPrimaryRecipes());
		Assertions.assertFalse(result.hasBasicRecipes());
		CollectionAssert.assertEmpty(result.getBasicRecipes());
		Assertions.assertFalse(result.hasFallbackRecipes());
		CollectionAssert.assertEmpty(result.getFallbackRecipes());
	}

	/**
	 * Verify that a single result can be added to each category
	 */
	@Test
	public void testSingleResult() {
		testEmptyResult();
		
		// Add primary
		result.addPrimaryRecipe(mockPrimary1);
		Assertions.assertTrue(result.hasPrimaryRecipes());
		CollectionAssert.assertEquivalent(Collections.singletonList(mockPrimary1), result.getPrimaryRecipes());
		Assertions.assertFalse(result.hasBasicRecipes());
		CollectionAssert.assertEmpty(result.getBasicRecipes());
		Assertions.assertFalse(result.hasFallbackRecipes());
		CollectionAssert.assertEmpty(result.getFallbackRecipes());
		
		// Add basic
		result.addBasicRecipe(mockBasic1);
		Assertions.assertTrue(result.hasPrimaryRecipes());
		CollectionAssert.assertEquivalent(Collections.singletonList(mockPrimary1), result.getPrimaryRecipes());
		Assertions.assertTrue(result.hasBasicRecipes());
		CollectionAssert.assertEquivalent(Collections.singletonList(mockBasic1), result.getBasicRecipes());
		Assertions.assertFalse(result.hasFallbackRecipes());
		CollectionAssert.assertEmpty(result.getFallbackRecipes());
		
		// Add fallback
		result.addFallbackRecipe(mockFallback1);
		Assertions.assertTrue(result.hasPrimaryRecipes());
		CollectionAssert.assertEquivalent(Collections.singletonList(mockPrimary1), result.getPrimaryRecipes());
		Assertions.assertTrue(result.hasBasicRecipes());
		CollectionAssert.assertEquivalent(Collections.singletonList(mockBasic1), result.getBasicRecipes());
		Assertions.assertTrue(result.hasFallbackRecipes());
		CollectionAssert.assertEquivalent(Collections.singletonList(mockFallback1), result.getFallbackRecipes());
	}

	/**
	 * Verify that a multiple results can be added to each category
	 */
	@Test
	public void testMultipleResults() {
		testEmptyResult();
		
		// Add basic
		result.addBasicRecipe(mockBasic1);
		result.addBasicRecipe(mockBasic2);
		result.addBasicRecipe(mockBasic3);
		Assertions.assertFalse(result.hasPrimaryRecipes());
		CollectionAssert.assertEmpty(result.getPrimaryRecipes());
		Assertions.assertTrue(result.hasBasicRecipes());
		CollectionAssert.assertEquivalent(Arrays.asList(mockBasic1, mockBasic2, mockBasic3), result.getBasicRecipes());
		Assertions.assertFalse(result.hasFallbackRecipes());
		CollectionAssert.assertEmpty(result.getFallbackRecipes());
		
		// Add fallback
		result.addFallbackRecipe(mockFallback1);
		result.addFallbackRecipe(mockFallback2);
		result.addFallbackRecipe(mockFallback3);
		Assertions.assertFalse(result.hasPrimaryRecipes());
		CollectionAssert.assertEmpty(result.getPrimaryRecipes());
		Assertions.assertTrue(result.hasBasicRecipes());
		CollectionAssert.assertEquivalent(Arrays.asList(mockBasic1, mockBasic2, mockBasic3), result.getBasicRecipes());
		Assertions.assertTrue(result.hasFallbackRecipes());
		CollectionAssert.assertEquivalent(Arrays.asList(mockFallback1, mockFallback2, mockFallback3), result.getFallbackRecipes());
		
		// Add primary
		result.addPrimaryRecipe(mockPrimary1);
		result.addPrimaryRecipe(mockPrimary2);
		result.addPrimaryRecipe(mockPrimary3);
		Assertions.assertTrue(result.hasPrimaryRecipes());
		CollectionAssert.assertEquivalent(Arrays.asList(mockPrimary1, mockPrimary2, mockPrimary3), result.getPrimaryRecipes());
		Assertions.assertTrue(result.hasBasicRecipes());
		CollectionAssert.assertEquivalent(Arrays.asList(mockBasic1, mockBasic2, mockBasic3), result.getBasicRecipes());
		Assertions.assertTrue(result.hasFallbackRecipes());
		CollectionAssert.assertEquivalent(Arrays.asList(mockFallback1, mockFallback2, mockFallback3), result.getFallbackRecipes());
	}
}
