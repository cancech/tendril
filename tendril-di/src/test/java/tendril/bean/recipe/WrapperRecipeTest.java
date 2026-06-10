package tendril.bean.recipe;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.bean.qualifier.Descriptor;
import tendril.bean.requirement.Requirement;
import tendril.context.Engine;
import tendril.test.AbstractUnitTest;

/**
 * Test case for {@link WrapperRecipe}
 */
public class WrapperRecipeTest extends AbstractUnitTest {
	
	// Mocks to use for testing
	@Mock
	private Engine mockEngine;
	@Mock
	private Object mockBean;
	@Mock
	private Descriptor<Object> mockDescriptor;
	@Mock
	private Requirement mockRequirement;

	// Instance to test
	private WrapperRecipe<Object> recipe;
	
	/**
	 * @see tendril.test.AbstractUnitTest#prepareTest()
	 */
	@Override
	protected void prepareTest() {
		when(mockDescriptor.getBeanClass()).thenReturn(Object.class);
		recipe = new WrapperRecipe<>(mockEngine, mockBean, mockDescriptor);
		verifyNoInteractions(mockEngine, mockBean);
	}
	
	/**
	 * Verify that the details of the bean are properly retrieved
	 */
	@Test
	public void verifyDetails() {
		Assertions.assertEquals(mockBean, recipe.get());
		Assertions.assertEquals(mockDescriptor, recipe.getDescription());
		verifyNoInteractions(mockEngine, mockBean);
	}
	
	/**
	 * Verify that the not-required methods do not have any impact on anything
	 */
	@Test
	public void testNotRequiredMethods() {
		recipe.setupDescriptor(mockDescriptor);
		recipe.setupPropertyRequirement(mockRequirement);
		recipe.setupEnvironmentRequirement(mockRequirement);

		Assertions.assertEquals(mockBean, recipe.get());
		Assertions.assertEquals(mockDescriptor, recipe.getDescription());
		verifyNoInteractions(mockEngine, mockBean, mockRequirement);
	}

}
