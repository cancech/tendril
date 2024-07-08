package tendril.codegen.classes.method;

import org.junit.jupiter.api.Test;

import tendril.codegen.VisibilityType;
import tendril.codegen.classes.SharedMethodBuilderTest;
import tendril.dom.type.Type;

/**
 * Test case for {@link ConcreteMethodBuilder}
 */
public class ConcreteMethodBuilderTest extends SharedMethodBuilderTest<ConcreteMethodBuilder<Type>> {

    /**
     * @see test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        builder = new ConcreteMethodBuilder<Type>(mockClass, mockReturnType, "MethodName");
    }

    /**
     * Verify that the validation properly accounts for all cases
     */
    @Test
    public void testValidate() {
        // No code, only private fails
        verifyValidateDoesThrow(VisibilityType.PUBLIC);
        verifyValidateDoesThrow(VisibilityType.PROTECTED);
        verifyValidateDoesThrow(VisibilityType.PACKAGE_PRIVATE);
        verifyValidateDoesThrow(VisibilityType.PRIVATE);

        // With code, all pass
        builder.emptyImplementation();
        verifyValidateDoesNotThrow(VisibilityType.PUBLIC);
        verifyValidateDoesNotThrow(VisibilityType.PROTECTED);
        verifyValidateDoesNotThrow(VisibilityType.PACKAGE_PRIVATE);
        verifyValidateDoesNotThrow(VisibilityType.PRIVATE);
    }

    /**
     * Verify that the proper method is created
     */
    @Test
    public void testBuildMethod() {
        verifyBuildMethodType(JMethodDefault.class);
    }
}
