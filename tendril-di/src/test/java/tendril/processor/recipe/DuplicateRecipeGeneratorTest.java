package tendril.processor.recipe;

import static org.mockito.Mockito.verify;

import javax.annotation.processing.Messager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.annotationprocessor.exception.InvalidConfigurationException;
import tendril.bean.Fallback;
import tendril.bean.Primary;
import tendril.bean.recipe.ConfigurationRecipe;
import tendril.codegen.classes.JClass;
import tendril.codegen.field.type.ClassType;
import tendril.test.AbstractUnitTest;

public class DuplicateRecipeGeneratorTest extends AbstractUnitTest {
	
	// Mocks to use for testing
	@Mock
	private ClassType mockBlueprint;
	@Mock
	private ClassType mockBeanType;
	@Mock
	private JClass mockCreator;
	@Mock
	private Messager mockMessager;

	private DuplicateRecipeGenerator generator;
	
	/**
	 * @see tendril.test.AbstractUnitTest#prepareTest()
	 */
	@Override
	protected void prepareTest() {
		generator = new DuplicateRecipeGenerator(mockBlueprint, mockBeanType, mockCreator, mockMessager);
		verify(mockCreator).hasAnnotation(Primary.class);
		verify(mockCreator).hasAnnotation(Fallback.class);
	}

	/**
	 * Verify the recipe class provided
	 * @throws InvalidConfigurationException 
	 */
	@Test
	public void testGetRecipeClass() throws InvalidConfigurationException {
		Assertions.assertEquals(ConfigurationRecipe.class, generator.getRecipeClass());
	}
}
