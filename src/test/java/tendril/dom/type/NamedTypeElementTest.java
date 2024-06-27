package tendril.dom.type;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.codegen.field.type.TypeData;
import test.AbstractUnitTest;

/**
 * Test case for {@link NamedTypeElement}
 */
public class NamedTypeElementTest extends AbstractUnitTest {

    // Mocks required for testing
    @Mock
    private TypeData<Type> mockTypeData;

    // The instance to test
    private NamedTypeElement<Type> element;

    /**
     * @see test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        element = new NamedTypeElement<Type>(mockTypeData, "MyName");
    }

    /**
     * Ensure that the getters are doing what is expected of them
     */
    @Test
    public void testGetters() {
        Assertions.assertEquals(mockTypeData, element.getType());
        Assertions.assertEquals("MyName", element.getName());
    }

}
