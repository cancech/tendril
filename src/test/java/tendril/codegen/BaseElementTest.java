package tendril.codegen;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;

import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.codegen.field.JValue;
import tendril.dom.type.core.ClassType;
import test.AbstractUnitTest;

/**
 * Test case for {@link BaseElement}
 */
public class BaseElementTest extends AbstractUnitTest {
	// Annotation without any parameters to use for testing
	private static @interface MockAnnotation1 {
	}

	// Annotation with a single value parameter to use for testing
	private static @interface MockAnnotation2 {
		String value();
	}

	// Annotation with multiple values to use for testing
	private static @interface MockAnnotation3 {
		String val1();

		int val2();
	}

	/**
	 * Concrete implementation of the {@link BaseElement} to allow for testing {@link BaseElement}
	 */
	private static class TestBaseElement extends BaseElement {
		// Counter for how many times generateSelf has been called
		private int timesGenerateSelfCalled = 0;

		/**
		 * CTOR
		 * 
		 * @param name {@link String}
		 */
		protected TestBaseElement(String name) {
			super(name);
		}

		/**
		 * Does nothing other than count how many times it has been called
		 */
		@Override
		protected void generateSelf(CodeBuilder builder, Set<ClassType> classImports) {
			timesGenerateSelfCalled++;
		}

		/**
		 * Verify that the generateSelf method has been called the expected number of times
		 * 
		 * @param expected int the number of times the method is expected to have been called
		 */
		public void verifyTimesGenerateSelfCalled(int expected) {
			Assertions.assertEquals(expected, timesGenerateSelfCalled);
		}

	}

	// Mocks to use for testing
	@Mock
	private CodeBuilder mockCodeBuilder;
	@Mock
	private Set<ClassType> mockSet;
	@Mock
	private JValue<String> mockAnnotation2Value;
	@Mock
	private JValue<String> mockAnnotation3Val1;
	@Mock
	private JValue<Integer> mockAnnotation3Val2;

	// Instance to use for testing
	private TestBaseElement element;

	/**
	 * @see test.AbstractUnitTest#prepareTest()
	 */
	@Override
	protected void prepareTest() {
		lenient().when(mockAnnotation2Value.generate(mockSet)).thenReturn("mockAnnotation2Value");
		lenient().when(mockAnnotation3Val1.generate(mockSet)).thenReturn("mockAnnotation3Val1");
		lenient().when(mockAnnotation3Val2.generate(mockSet)).thenReturn("mockAnnotation3Val2");
		element = new TestBaseElement("MyElementName");
		Assertions.assertEquals("MyElementName", element.getName());
	}

	/**
	 * Verify that without any annotation the code generation takes place as expected
	 */
	@Test
	public void testGenerateNoAnnotation() {
		element.generate(mockCodeBuilder, mockSet);
		element.verifyTimesGenerateSelfCalled(1);
	}
	
	/**
	 * Verify that the correct code is generated with a single parameterless annotation
	 */
	@Test
	public void testSingleAnnotationWithoutParameters() {
		element.annotate(MockAnnotation1.class);
		element.generate(mockCodeBuilder, mockSet);
		verify(mockSet).add(new ClassType(MockAnnotation1.class));
		verify(mockCodeBuilder).append("@MockAnnotation1");
		element.verifyTimesGenerateSelfCalled(1);
	}
	
	/**
	 * Verify that the correct code is generated with a single annotation that takes a default value
	 */
	@Test
	public void testSingleAnnotationWithDefaultValue() {
		element.annotate(MockAnnotation2.class, mockAnnotation2Value);
		element.generate(mockCodeBuilder, mockSet);
		verify(mockSet).add(new ClassType(MockAnnotation2.class));
		verify(mockAnnotation2Value).generate(mockSet);
		verify(mockCodeBuilder).append("@MockAnnotation2(mockAnnotation2Value)");
		element.verifyTimesGenerateSelfCalled(1);
	}
	
	/**
	 * Verify that the correct code is generated with a single annotation that takes a default value
	 */
	@Test
	public void testSingleAnnotationWithMultipleValues() {
		element.annotate(MockAnnotation3.class, Map.of("val1", mockAnnotation3Val1, "val2", mockAnnotation3Val2));
		element.generate(mockCodeBuilder, mockSet);
		verify(mockSet).add(new ClassType(MockAnnotation3.class));
		verify(mockAnnotation3Val1).generate(mockSet);
		verify(mockAnnotation3Val2).generate(mockSet);
		verify(mockCodeBuilder).append("@MockAnnotation3(val1 = mockAnnotation3Val1, val2 = mockAnnotation3Val2)");
		element.verifyTimesGenerateSelfCalled(1);
	}
	
	/**
	 * Verify that the correct code is generates with multiple annotations applied
	 */
	@Test
	public void testMultipleAnnotations() {
		
	}
}
