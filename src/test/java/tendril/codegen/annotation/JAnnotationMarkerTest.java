package tendril.codegen.annotation;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link JAnnotationMarker}
 */
public class JAnnotationMarkerTest extends AbstractJAnnotationTest {

	// Instance to test
	private JAnnotationMarker annotation;

	/**
	 * @see test.AbstractUnitTest#prepareTest()
	 */
	@Override
	protected void prepareTest() {
		annotation = new JAnnotationMarker(TestMarkerAnnotation.class);
	}

	/**
	 * Verify that the code is properly generated
	 */
	@Test
	public void testCodeGeneration() {
		annotation.generateSelf(mockBuilder, mockImportSet);
		verify(mockBuilder).append("@TestMarkerAnnotation");
	}

	/**
	 * Verify that an error is generated if an annotation that is not merely a marker is employed
	 */
	@Test
	public void testInvalidAnnotations() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> new JAnnotationMarker(TestDefaultParamAnnotation.class));
		Assertions.assertThrows(IllegalArgumentException.class, () -> new JAnnotationMarker(TestMultiParamAnnotation.class));
	}
}
