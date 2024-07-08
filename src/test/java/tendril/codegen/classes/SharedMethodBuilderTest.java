/**
 * 
 */
package tendril.codegen.classes;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.mockito.Mock;

import tendril.codegen.VisibilityType;
import tendril.codegen.classes.method.JMethod;
import tendril.codegen.field.type.TypeData;
import tendril.dom.method.MethodElement;
import tendril.dom.type.Type;
import test.AbstractUnitTest;
import test.assertions.ClassAssert;

/**
 * Class which contains the shared elements of the specific method builders
 */
public abstract class SharedMethodBuilderTest<T extends MethodBuilder<Type>> extends AbstractUnitTest {

    // Mocks to use for testing
    @Mock
    protected JClass mockClass;
    @Mock
    protected TypeData<Type> mockReturnType;
    @Mock
    protected VisibilityType mockVisibilityType;
    @Mock
    protected MethodElement<Type> mockMethodElement;

    // Instance to test
    protected MethodBuilder<Type> builder;

    /**
     * Apply the visibility and ensure that no exception is thrown
     * 
     * @param visibility {@link VisibilityType} to apply
     */
    protected void verifyValidateDoesNotThrow(VisibilityType visibility) {
        builder.setVisibility(visibility);
        Assertions.assertDoesNotThrow(() -> builder.validateData());
    }

    /**
     * Apply the visibility and ensure that the exception is thrown
     * 
     * @param visibility {@link VisibilityType} to apply
     */
    protected void verifyValidateDoesThrow(VisibilityType visibility) {
        builder.setVisibility(visibility);
        Assertions.assertThrows(IllegalArgumentException.class, () -> builder.validateData());
    }

    /**
     * Verify that the buildMethod() produces the appropriate {@link JMethod} type
     * 
     * @param expectedClass {@link Class} extending {@link JMethod} that is expected to be built
     */
    protected void verifyBuildMethodType(@SuppressWarnings("rawtypes") Class<? extends JMethod> expectedClass) {
        when(mockMethodElement.getName()).thenReturn("mockMethod");
        ClassAssert.assertInstance(expectedClass, builder.buildMethod(mockMethodElement));
        verify(mockMethodElement).getName();
    }
}
