package tendril.processor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.annotationprocessor.exception.InvalidTypeException;
import tendril.annotationprocessor.exception.TendrilException;
import tendril.bean.duplicate.BlueprintDriver;
import tendril.test.AbstractUnitTest;

/**
 * Test case for the {@link BlueprintProcessor}
 */
public class BlueprintProcessorTest extends AbstractUnitTest {
	
	private class BlueprintProcessorForTest extends BlueprintProcessor {
		
		private BlueprintProcessorForTest() {
			super();
			this.processingEnv = mockProcessingEnv;
		}
		
	}
	
	// Mocks to use for testing
    @Mock
    private ProcessingEnvironment mockProcessingEnv;
    @Mock
    private Elements mockElementUtils;
    @Mock
    private TypeElement mockClassTypeElement;
    @Mock
    private TypeMirror mockClassTypeMirror;
    @Mock
    private Types mockTypeUtils;
	@Mock
	private TypeElement mockTypeElement;

	// Instance to use for testing
	private BlueprintProcessor processor;
	
	/**
	 * @see tendril.test.AbstractUnitTest#prepareTest()
	 */
	@Override
	protected void prepareTest() {
		processor = new BlueprintProcessorForTest();
	}
	
	private void configMocks(ElementKind typeKind, boolean isAssignable) {
		when(mockTypeElement.getKind()).thenReturn(typeKind);
		
		when(mockProcessingEnv.getElementUtils()).thenReturn(mockElementUtils);
		when(mockElementUtils.getTypeElement(BlueprintDriver.class.getName())).thenReturn(mockClassTypeElement);
		when(mockClassTypeElement.asType()).thenReturn(mockClassTypeMirror);
		
		when(mockProcessingEnv.getTypeUtils()).thenReturn(mockTypeUtils);
		when(mockTypeUtils.isAssignable(any(), eq(mockClassTypeMirror))).thenReturn(isAssignable);
	}

	/**
	 * Verify that the validation is performed properly
	 * @throws TendrilException 
	 */
	@Test
	public void testValidateTypeEnum() throws TendrilException {
		// When not assignable, everything works as expected
		configMocks(ElementKind.ENUM, false);
		processor.validateType(mockTypeElement);
		verify(mockTypeElement).asType();
		
		// When is assignable, an exception is thrown
		configMocks(ElementKind.ENUM, true);
		Assertions.assertThrows(InvalidTypeException.class, () -> processor.validateType(mockTypeElement));
		verify(mockTypeElement, times(2)).asType();
	}

	/**
	 * Verify that the validation is performed properly
	 * @throws TendrilException 
	 */
	@Test
	public void testValidateTypeClass() throws TendrilException {
		// When not assignable, an exception is thrown
		configMocks(ElementKind.CLASS, false);
		Assertions.assertThrows(InvalidTypeException.class, () -> processor.validateType(mockTypeElement));
		verify(mockTypeElement).asType();
		
		// When is assignable, everything works as expected
		configMocks(ElementKind.CLASS, true);
		processor.validateType(mockTypeElement);
		verify(mockTypeElement, times(2)).asType();
	}
}
