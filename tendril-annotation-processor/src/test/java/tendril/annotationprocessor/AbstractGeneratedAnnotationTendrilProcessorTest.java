package tendril.annotationprocessor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import tendril.annotationprocessor.exception.MissingAnnotationException;
import tendril.annotationprocessor.exception.TendrilException;
import tendril.test.AbstractUnitTest;

/**
 * Test case for {@link AbstractGeneratedAnnotationTendrilProcessor}
 */
public class AbstractGeneratedAnnotationTendrilProcessorTest extends AbstractUnitTest {
	
	// Mocks to use for testing
    @Mock
    protected ProcessingEnvironment mockProcessingEnv;
	@Mock
	private RoundEnvironment mockRound1;
	@Mock
	private RoundEnvironment mockRound2;
	@Mock
	private RoundEnvironment mockRound3;
	@Mock
	private TypeElement mockAnnotation;
	@Mock
	private TypeElement mockGeneratedAnnotation;
	@Mock
	private TypeElement mockTypeElement;
	@Mock
	private ExecutableElement mockExecutableElement;
	
	// Concrete implementation to use for testing
	private class TestProcessor extends AbstractGeneratedAnnotationTendrilProcessor {
		
		private int timesTypeProcessed = 0;
		private int timesMethodProcessed = 0;
		
		TestProcessor() {
			this.processingEnv = mockProcessingEnv;
		}
		
		@Override
		public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
			this.roundEnv = env;
			for(TypeElement el: annotations)
				findAndProcessElements(el);
			return false;
		}
		
		@Override
		protected void processElement(TypeElement annotation, Element element) throws MissingAnnotationException, TendrilException {
			if (element instanceof TypeElement)
				timesTypeProcessed++;
			else if (element instanceof ExecutableElement)
				timesMethodProcessed++;
		}

		@Override
		protected ClassDefinition processType() throws TendrilException {
			Assertions.fail("Should not be reached as part of the test");
			return null;
		}

		@Override
		protected ClassDefinition processMethod() throws TendrilException {
			Assertions.fail("Should not be reached as part of the test");
			return null;
		}
		
		private void assertTimesProcessed(int expectedType, int expectedMethod) {
			Assertions.assertEquals(expectedType, timesTypeProcessed);
			Assertions.assertEquals(expectedMethod, timesMethodProcessed);
		}
		
	}
	
	// Instance to use for testing
	private TestProcessor processor;

	@Override
	protected void prepareTest() {
		doReturn(Set.of(mockGeneratedAnnotation)).when(mockRound3).getElementsAnnotatedWith(mockAnnotation);
		
		processor = new TestProcessor();
	}
	
	/**
	 * Verify that the first annotation generate appropriately
	 */
	@Test
	public void testFirstAnnotation() {
		doReturn(Set.of(mockTypeElement)).when(mockRound1).getElementsAnnotatedWith(mockGeneratedAnnotation);
		doReturn(Set.of(mockExecutableElement)).when(mockRound2).getElementsAnnotatedWith(mockGeneratedAnnotation);
		doReturn(Set.of(mockTypeElement, mockExecutableElement)).when(mockRound3).getElementsAnnotatedWith(mockGeneratedAnnotation);
		
		try(MockedStatic<EnvironmentCollector> envCollector = Mockito.mockStatic(EnvironmentCollector.class)) {
			envCollector.when(() -> EnvironmentCollector.getAllEnvironments(mockRound3)).thenReturn(Arrays.asList(mockRound1, mockRound2, mockRound3));
			processor.process(Set.of(mockAnnotation), mockRound3);
			envCollector.verify(()->EnvironmentCollector.getAllEnvironments(any(RoundEnvironment.class)), times(1));
			verify(mockRound1).getElementsAnnotatedWith(mockGeneratedAnnotation);
			verify(mockRound2).getElementsAnnotatedWith(mockGeneratedAnnotation);
			verify(mockRound3).getElementsAnnotatedWith(mockGeneratedAnnotation);
			processor.assertTimesProcessed(2, 2);
		}
	}

	/**
	 * Verify that the second annotation will also generate appropriately
	 */
	@Test
	public void testSecondIteration() {
		testFirstAnnotation();
		doReturn(Set.of(mockTypeElement, mockExecutableElement)).when(mockRound1).getElementsAnnotatedWith(mockGeneratedAnnotation);
		doReturn(Set.of(mockExecutableElement, mockTypeElement)).when(mockRound2).getElementsAnnotatedWith(mockGeneratedAnnotation);
		doReturn(Set.of()).when(mockRound3).getElementsAnnotatedWith(mockGeneratedAnnotation);

		try(MockedStatic<EnvironmentCollector> envCollector = Mockito.mockStatic(EnvironmentCollector.class)) {
			envCollector.when(() -> EnvironmentCollector.getAllEnvironments(mockRound3)).thenReturn(Arrays.asList(mockRound1, mockRound2, mockRound3));
			processor.process(Set.of(mockAnnotation), mockRound3);
			envCollector.verify(()->EnvironmentCollector.getAllEnvironments(any(RoundEnvironment.class)), times(1));
			verify(mockRound1, times(2)).getElementsAnnotatedWith(mockGeneratedAnnotation);
			verify(mockRound2, times(2)).getElementsAnnotatedWith(mockGeneratedAnnotation);
			verify(mockRound3, times(2)).getElementsAnnotatedWith(mockGeneratedAnnotation);
			processor.assertTimesProcessed(4, 4);
		}
	}
}
