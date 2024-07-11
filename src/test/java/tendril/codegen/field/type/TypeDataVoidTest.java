/**
 * 
 */
package tendril.codegen.field.type;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.dom.type.core.VoidType;

/**
 * Test case for {@link TypeDataVoid}
 */
public class TypeDataVoidTest extends SharedTypeDataTest<TypeDataVoid> {

    // Mocks to use for testing
    @Mock
    private Object mockObject;
    
    /**
     * @see test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        data = new TypeDataVoid();
        verifyDataState(VoidType.INSTANCE, "void", true);
    }

    /**
     * Verify that attempting to generate an asValue throws an exception
     */
    @Test
    public void testAsValueThrowsException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> data.asValue(mockObject));
    }
}
