package tendril.codegen.annotation;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.codegen.field.JValue;
import test.assertions.ClassAssert;

/**
 * Test case for {@link JAnnotationFactory}
 */
public class JAnnotationFactoryTest extends AbstractJAnnotationTest{

	// Mocks to use for testing
	@Mock
	private JValue<String> mockValue;
	
	/**
	 * @see test.AbstractUnitTest#prepareTest()
	 */
	@Override
	protected void prepareTest() {
		// Nothing to do...
	}

	/**
	 * Verify that the factory produces the expected instances
	 */
	@Test
	public void testFactoryMethods() {
		ClassAssert.assertInstance(JAnnotationMarker.class, JAnnotationFactory.create(TestMarkerAnnotation.class));
		ClassAssert.assertInstance(JAnnotationDefaultValue.class, JAnnotationFactory.create(TestDefaultParamAnnotation.class, mockValue));
		ClassAssert.assertInstance(JAnnotationFull.class, JAnnotationFactory.create(TestMultiParamAnnotation.class, Map.of()));
	}
}
