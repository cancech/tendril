/**
 * 
 */
package tendril.codegen.field.type;

import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.mockito.Mock;

import tendril.dom.type.Type;
import tendril.dom.type.core.ClassType;
import test.AbstractUnitTest;

/**
 * Shared test features for the various {@link TypeData} tests
 */
public abstract class SharedTypeDataTest<TYPE_DATA extends TypeData<?>> extends AbstractUnitTest {

    // Mocks to use for testing
    @Mock
    protected Set<ClassType> mockImports;

    // Instance to test
    protected TYPE_DATA data;

    /**
     * Helper to verify the full details of the {@link TypeData} instance being tested.
     * 
     * @param expectedType {@link Type} of the data
     * @param expectedName {@link String} of the data
     * @param expectedVoid boolean true if the data is to be void
     */
    protected void verifyDataState(Type expectedType, String expectedName, boolean expectedVoid) {
        Assertions.assertEquals(expectedType, data.getDataType());
        Assertions.assertEquals(expectedName, data.getSimpleName());
        Assertions.assertEquals(expectedVoid, data.isVoid());
        verifyAllChecked();
    }
}
