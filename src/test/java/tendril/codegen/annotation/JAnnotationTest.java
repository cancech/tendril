package tendril.codegen.annotation;

import static org.mockito.Mockito.verify;

import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import tendril.codegen.CodeBuilder;
import tendril.dom.type.core.ClassType;

/**
 * Test case for {@link JAnnotation}
 */
public class JAnnotationTest extends AbstractJAnnotationTest {

	/**
	 * Concrete implementation for {@link JAnnotation} to use for testing
	 */
	private static class TestJAnnotation extends JAnnotation {
		/** Counter for the number of times that generateSelf() has been called */
		private int timesGenerateSelfCalled = 0;

		/**
		 * CTOR
		 */
		public TestJAnnotation() {
			super(TestMarkerAnnotation.class);
		}

		/**
		 * Does nothing other than count number of times it has been called
		 */
		@Override
		protected void generateSelf(CodeBuilder builder, Set<ClassType> classImports) {
			timesGenerateSelfCalled++;
		}
		
		/**
		 * Verify that generateSelf() has been called the expected number of times
		 * 
		 * @param expected int number of times that generateSelf() should have been called
		 */
		public void verifyTimesGenerateCalled(int expected) {
			Assertions.assertEquals(expected, timesGenerateSelfCalled);
		}
	}
	
	// Instance to test
	private TestJAnnotation annotation;

	/**
	 * @see test.AbstractUnitTest#prepareTest()
	 */
	@Override
	protected void prepareTest() {
		annotation = new TestJAnnotation();
		Assertions.assertEquals("@TestMarkerAnnotation", annotation.getName());
	}
	
	/**
	 * Verify that generating does what is expected
	 */
	@Test
	public void testGenenrate() {
		annotation.generate(mockBuilder, mockImportSet);
		verify(mockImportSet).add(new ClassType(TestMarkerAnnotation.class));
		annotation.verifyTimesGenerateCalled(1);
	}
}
