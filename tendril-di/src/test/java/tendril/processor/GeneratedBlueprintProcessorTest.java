package tendril.processor;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.lang.model.element.TypeElement;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.annotationprocessor.exception.TendrilException;
import tendril.bean.Bean;
import tendril.test.AbstractUnitTest;

public class GeneratedBlueprintProcessorTest extends AbstractUnitTest {
	
	// Mocks to use for testing
	@Mock
	private TypeElement mockAnnotation;
	@Mock
	private TypeElement mockType;
	
	// Instance to test
	private GeneratedBlueprintProcessor processor;
	
	/**
	 * Test override to force the currentAnnotation without needing extensive mocking
	 */
	private class TestGeneratedBlueprintProcessor extends GeneratedBlueprintProcessor {
		
		private TestGeneratedBlueprintProcessor() {
			currentAnnotation = mockAnnotation;
		}
	}

	/**
	 * @see tendril.test.AbstractUnitTest#prepareTest()
	 */
	@Override
	protected void prepareTest() {
		processor = new TestGeneratedBlueprintProcessor();
	}

	/**
	 * When @Bean is not applied, validation passes
	 */
	@Test
	public void testValidateNoBean() throws TendrilException {
		when(mockType.getAnnotationsByType(Bean.class)).thenReturn(new Bean[0]);
		processor.validateType(mockType);
		verify(mockType).getAnnotationsByType(Bean.class);
	}

	/**
	 * When @Bean is applied, exception is thrown
	 */
	@Test
	public void testValidateWithBean() {
		when(mockType.getAnnotationsByType(Bean.class)).thenReturn(new Bean[1]);
		Assertions.assertThrows(TendrilException.class, () -> processor.validateType(mockType));
		verify(mockType).getAnnotationsByType(Bean.class);
		verify(mockAnnotation).getSimpleName();
	}
}
