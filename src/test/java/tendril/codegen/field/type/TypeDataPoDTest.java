/**
 * 
 */
package tendril.codegen.field.type;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.dom.type.core.ClassType;
import tendril.dom.type.core.PoDType;
import tendril.dom.type.value.ValueElement;
import test.assertions.TendrilAssert;

/**
 * Test case for {@link TypeDataPoD}
 */
public class TypeDataPoDTest extends SharedTypeDataTest<TypeDataPoD> {

    // Mocks to use for testing
    @Mock
    private ClassType mockClassType;

    /**
     * @see test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        // Not required
    }
    
    /**
     * Verify that a boolean TypeData works properly
     */
    @Test
    public void testBoolean() {
        data = new TypeDataPoD(PoDType.BOOLEAN);
        verifyDataState(PoDType.BOOLEAN);

        TendrilAssert.assertValueElement(new ValueElement<>(PoDType.BOOLEAN, true), data.asValue(true));
        TendrilAssert.assertValueElement(new ValueElement<>(PoDType.BOOLEAN, false), data.asValue(false));
    }
    
    /**
     * Verify that a byte TypeData works properly
     */
    @Test
    public void testByte() {
        data = new TypeDataPoD(PoDType.BYTE);
        verifyDataState(PoDType.BYTE);

        TendrilAssert.assertValueElement(new ValueElement<>(PoDType.BYTE, Byte.valueOf("10")), data.asValue(Byte.valueOf("10")));
        TendrilAssert.assertValueElement(new ValueElement<>(PoDType.BYTE, Byte.valueOf("101")), data.asValue(Byte.valueOf("101")));
    }
    
    /**
     * Verify that a char TypeData works properly
     */
    @Test
    public void testChar() {
        data = new TypeDataPoD(PoDType.CHAR);
        verifyDataState(PoDType.CHAR);

        TendrilAssert.assertValueElement(new ValueElement<>(PoDType.CHAR, 'q'), data.asValue('q'));
        TendrilAssert.assertValueElement(new ValueElement<>(PoDType.CHAR, 'b'), data.asValue('b'));
    }
    
    /**
     * Verify that a double TypeData works properly
     */
    @Test
    public void testDouble() {
        data = new TypeDataPoD(PoDType.DOUBLE);
        verifyDataState(PoDType.DOUBLE);

        TendrilAssert.assertValueElement(new ValueElement<>(PoDType.DOUBLE, 1.23), data.asValue(1.23));
        TendrilAssert.assertValueElement(new ValueElement<>(PoDType.DOUBLE, 4.567), data.asValue(4.567));
    }
    
    /**
     * Verify that a float TypeData works properly
     */
    @Test
    public void testFloat() {
        data = new TypeDataPoD(PoDType.FLOAT);
        verifyDataState(PoDType.FLOAT);

        TendrilAssert.assertValueElement(new ValueElement<>(PoDType.FLOAT, 1.23f), data.asValue(1.23f));
        TendrilAssert.assertValueElement(new ValueElement<>(PoDType.FLOAT, 4.567f), data.asValue(4.567f));
    }
    
    /**
     * Verify that an int TypeData works properly
     */
    @Test
    public void testInteger() {
        data = new TypeDataPoD(PoDType.INT);
        verifyDataState(PoDType.INT);

        TendrilAssert.assertValueElement(new ValueElement<>(PoDType.INT, 123), data.asValue(123));
        TendrilAssert.assertValueElement(new ValueElement<>(PoDType.INT, 456), data.asValue(456));
    }
    
    /**
     * Verify that a long TypeData works properly
     */
    @Test
    public void testLong() {
        data = new TypeDataPoD(PoDType.LONG);
        verifyDataState(PoDType.LONG);

        TendrilAssert.assertValueElement(new ValueElement<>(PoDType.LONG, 1234567890l), data.asValue(1234567890l));
        TendrilAssert.assertValueElement(new ValueElement<>(PoDType.LONG, 45645645645l), data.asValue(45645645645l));
    }
    
    /**
     * Verify that a short TypeData works properly
     */
    @Test
    public void testShort() {
        data = new TypeDataPoD(PoDType.SHORT);
        verifyDataState(PoDType.SHORT);

        TendrilAssert.assertValueElement(new ValueElement<>(PoDType.SHORT, (short) 789), data.asValue((short) 789));
        TendrilAssert.assertValueElement(new ValueElement<>(PoDType.SHORT, (short) 654), data.asValue((short) 654));
    }
    
    /**
     * Verify the data of the data
     * 
     * @param type {@link PoDType} of the data
     */
    private void verifyDataState(PoDType type) {
        verifyDataState(type, type.toString(), false);
    }
}
