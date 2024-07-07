package tendril.codegen.annotation;

import java.util.Set;

import org.mockito.Mock;

import tendril.codegen.CodeBuilder;
import tendril.dom.type.core.ClassType;
import test.AbstractUnitTest;

/**
 * Helper which contains the common JAnnotation test capabilities
 */
public abstract class AbstractJAnnotationTest extends AbstractUnitTest {

	/** Test annotation which takes no parameters */
	protected static @interface TestMarkerAnnotation {
	}

	/** Test annotation which takes a default parameter */
	protected static @interface TestDefaultParamAnnotation {
		String value();
	}

	/** Test annotation which takes a multiple parameters */
	protected static @interface TestMultiParamAnnotation {
		String valStr();

		int valInt();
	}
	
	// Mocks to use for testing
	@Mock
	protected CodeBuilder mockBuilder;
	@Mock
	protected Set<ClassType> mockImportSet;
}
