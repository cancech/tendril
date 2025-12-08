package tendril.processor.recipe;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.annotation.processing.Messager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.annotationprocessor.exception.ProcessingException;
import tendril.annotationprocessor.exception.TendrilException;
import tendril.bean.Fallback;
import tendril.bean.Primary;
import tendril.codegen.JBase;
import tendril.codegen.classes.ClassBuilder;
import tendril.codegen.field.type.ClassType;
import tendril.test.AbstractUnitTest;

/**
 * Integration test for verifying that the {@link AbstractRecipeGenerator} produces the proper results.
 * 
 * Note this test case is only checking for failures, as the successes are far more easily tested in the test-app
 */
public class AbstractRecipeGeneratorTest extends AbstractUnitTest {
	
	// Mocks to use for testing
	@Mock
	private ClassType mockClassType;
	@Mock
	private JBase mockCreator;
	@Mock
	private Messager mockMessager;

	// Concrete instance to use for testing
	private class TestAbstractRecipeGenerator extends AbstractRecipeGenerator<JBase> {

		TestAbstractRecipeGenerator() {
			super(mockClassType, mockCreator, mockMessager);
		}

		@Override
		protected void validateCreator() throws TendrilException {
		}

		@Override
		protected void populateBuilder(ClassBuilder builder) throws TendrilException {
		}
		
	}
	
	/**
	 * @see tendril.test.AbstractUnitTest#prepareTest()
	 */
	@Override
	protected void prepareTest() {
		// Not required
	}

	/**
	 * Verify that the flags for the recipe are properly handled
	 */
	@Test
	public void testPrimaryFallbackFlags() {
		when(mockClassType.getFullyQualifiedName()).thenReturn("MockClass");
		
		// Both not applied, should work fine
		when(mockCreator.hasAnnotation(Primary.class)).thenReturn(false);
		when(mockCreator.hasAnnotation(Fallback.class)).thenReturn(false);
		Assertions.assertDoesNotThrow(() -> new TestAbstractRecipeGenerator());
		verify(mockCreator, times(1)).hasAnnotation(Primary.class);
		verify(mockCreator, times(1)).hasAnnotation(Fallback.class);
		verify(mockClassType, never()).getFullyQualifiedName();

		// Only Primary applied, should work fine
		when(mockCreator.hasAnnotation(Primary.class)).thenReturn(true);
		when(mockCreator.hasAnnotation(Fallback.class)).thenReturn(false);
		Assertions.assertDoesNotThrow(() -> new TestAbstractRecipeGenerator());
		verify(mockCreator, times(2)).hasAnnotation(Primary.class);
		verify(mockCreator, times(2)).hasAnnotation(Fallback.class);
		verify(mockClassType, never()).getFullyQualifiedName();

		// Only Fallback applied, should work fine
		when(mockCreator.hasAnnotation(Primary.class)).thenReturn(false);
		when(mockCreator.hasAnnotation(Fallback.class)).thenReturn(true);
		Assertions.assertDoesNotThrow(() -> new TestAbstractRecipeGenerator());
		verify(mockCreator, times(3)).hasAnnotation(Primary.class);
		verify(mockCreator, times(3)).hasAnnotation(Fallback.class);
		verify(mockClassType, never()).getFullyQualifiedName();

		// Both applied, should throw an exception
		when(mockCreator.hasAnnotation(Primary.class)).thenReturn(true);
		when(mockCreator.hasAnnotation(Fallback.class)).thenReturn(true);
		Assertions.assertThrows(ProcessingException.class, () -> new TestAbstractRecipeGenerator());
		verify(mockCreator, times(4)).hasAnnotation(Primary.class);
		verify(mockCreator, times(4)).hasAnnotation(Fallback.class);
		verify(mockClassType, times(1)).getFullyQualifiedName();
	}
}
