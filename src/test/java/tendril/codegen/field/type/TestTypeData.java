/**
 * 
 */
package tendril.codegen.field.type;

import org.junit.jupiter.api.Assertions;

import tendril.dom.type.Type;
import tendril.dom.type.value.ValueElement;

/**
 * Concrete {@link TypeData} to use for testing
 */
class TestTypeData extends TypeData<Type> {
    /** The value to return when asValue is called */
    private final ValueElement<Type, ?> mockValue;

    /** Counter for how many times asValue was called */
    private int timesAsValueCalled = 0;

    /**
     * CTOR
     * 
     * @param type      {@link Type} of the element
     * @param mockValue {@link ValueElement} to return when asValue is called
     */
    TestTypeData(Type type, ValueElement<Type, ?> mockValue) {
        super(type, "TestTypeData");
        this.mockValue = mockValue;
    }

    /**
     * @see tendril.codegen.field.type.TypeData#asValue(java.lang.Object)
     */
    @Override
    public ValueElement<Type, ?> asValue(Object value) {
        timesAsValueCalled++;
        return mockValue;
    }

    /**
     * Verify how many times asValue() had been called
     * 
     * @param expected int the number of times asValue() should have been called
     */
    public void verifyTimesAsValueCalled(int expected) {
        Assertions.assertEquals(expected, timesAsValueCalled);
    }

}
