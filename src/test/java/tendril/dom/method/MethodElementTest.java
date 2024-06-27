package tendril.dom.method;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.codegen.field.type.TypeData;
import tendril.dom.type.NamedTypeElement;
import tendril.dom.type.Type;
import tendril.dom.type.core.ClassType;
import tendril.dom.type.core.PoDType;
import tendril.dom.type.core.VoidType;
import test.AbstractUnitTest;
import test.assertions.CollectionAssert;

/**
 * Test case for {@link MethodElement}
 */
public class MethodElementTest extends AbstractUnitTest {

    // Mocks to use for testing
    @Mock
    private TypeData<Type> mockReturnType;
    @Mock
    private NamedTypeElement<ClassType> mockClassParam;
    @Mock
    private NamedTypeElement<PoDType> mockPodParam;
    @Mock
    private NamedTypeElement<VoidType> mockVoidParam;
    
    // Instance to test
    private MethodElement<Type> method;
    
    @Override
    protected void prepareTest() {
        method = new MethodElement<Type>(mockReturnType, "methodName");
    }
    
    /**
     * Verify that no parameters are stored by default
     */
    @Test
    public void testNoParams() {
        CollectionAssert.assertEmpty(method.getParameters());
    }
    
    /**
     * Verify that a single parameter can be stored and retrieved
     */
    @Test
    public void testSingleParam() {
        method.addParameter(mockClassParam);
        Assertions.assertIterableEquals(Collections.singletonList(mockClassParam), method.getParameters());
    }
    
    /**
     * Verify that a multiple parameters can be stored and retrieved
     */
    @Test
    public void testMultipleParams() {
        method.addParameter(mockPodParam);
        method.addParameter(mockClassParam);
        method.addParameter(mockVoidParam);
        Assertions.assertIterableEquals(Arrays.asList(mockPodParam, mockClassParam, mockVoidParam), method.getParameters());
    }

}
