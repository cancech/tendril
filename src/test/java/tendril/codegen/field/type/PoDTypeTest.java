/*
 * Copyright 2024 Jaroslav Bosak
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://opensource.org/license/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tendril.codegen.field.type;

import static org.mockito.Mockito.verifyNoInteractions;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import tendril.codegen.field.value.JValueFactory;
import test.assertions.TendrilAssert;

/**
 * Test case for {@link PoDType}
 */
public class PoDTypeTest extends SharedTypeTest<PoDType> {

    /**
     * @see test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        type = PoDType.BOOLEAN;
    }
    
    /**
     * Verify that the correct PoDType is provided for the class
     */
    @Test
    public void testFromClass() {
        Assertions.assertEquals(PoDType.BOOLEAN, PoDType.from(Boolean.class));
        Assertions.assertEquals(PoDType.BYTE, PoDType.from(Byte.class));
        Assertions.assertEquals(PoDType.CHAR, PoDType.from(Character.class));
        Assertions.assertEquals(PoDType.DOUBLE, PoDType.from(Double.class));
        Assertions.assertEquals(PoDType.FLOAT, PoDType.from(Float.class));
        Assertions.assertEquals(PoDType.INT, PoDType.from(Integer.class));
        Assertions.assertEquals(PoDType.LONG, PoDType.from(Long.class));
        Assertions.assertEquals(PoDType.SHORT, PoDType.from(Short.class));
    }
    
    /**
     * Verify elements produce the proper string
     */
    @Test
    public void testToString() {
        Assertions.assertEquals(8, PoDType.values().length);
        Assertions.assertEquals("boolean", PoDType.BOOLEAN.toString());
        Assertions.assertEquals("byte", PoDType.BYTE.toString());
        Assertions.assertEquals("char", PoDType.CHAR.toString());
        Assertions.assertEquals("double", PoDType.DOUBLE.toString());
        Assertions.assertEquals("float", PoDType.FLOAT.toString());
        Assertions.assertEquals("int", PoDType.INT.toString());
        Assertions.assertEquals("long", PoDType.LONG.toString());
        Assertions.assertEquals("short", PoDType.SHORT.toString());
    }
    
    /**
     * Verify that the assignability is properly configured
     */
    @Test
    public void testAssignable() {
        for (PoDType i: PoDType.values()) {
            for (PoDType j: PoDType.values())
                Assertions.assertEquals(i == j, i.isAssignableTo(j));
        }
    }
    
    /**
     * Verify that a boolean TypeData works properly
     */
    @Test
    public void testBoolean() {
        type = PoDType.BOOLEAN;
        type.registerImport(mockImports);
        verifyNoInteractions(mockImports);
        verifyDataState(PoDType.BOOLEAN);

        TendrilAssert.assertJValue(JValueFactory.create(true), type.asValue(true));
        TendrilAssert.assertJValue(JValueFactory.create(false), type.asValue(false));
    }
    
    /**
     * Verify that a byte TypeData works properly
     */
    @Test
    public void testByte() {
        type = PoDType.BYTE;
        type.registerImport(mockImports);
        verifyNoInteractions(mockImports);
        verifyDataState(PoDType.BYTE);

        TendrilAssert.assertJValue(JValueFactory.create(Byte.valueOf("10")), type.asValue(Byte.valueOf("10")));
        TendrilAssert.assertJValue(JValueFactory.create(Byte.valueOf("101")), type.asValue(Byte.valueOf("101")));
    }
    
    /**
     * Verify that a char TypeData works properly
     */
    @Test
    public void testChar() {
        type = PoDType.CHAR;
        type.registerImport(mockImports);
        verifyNoInteractions(mockImports);
        verifyDataState(PoDType.CHAR);

        TendrilAssert.assertJValue(JValueFactory.create('q'), type.asValue('q'));
        TendrilAssert.assertJValue(JValueFactory.create('b'), type.asValue('b'));
    }
    
    /**
     * Verify that a double TypeData works properly
     */
    @Test
    public void testDouble() {
        type = PoDType.DOUBLE;
        type.registerImport(mockImports);
        verifyNoInteractions(mockImports);
        verifyDataState(PoDType.DOUBLE);

        TendrilAssert.assertJValue(JValueFactory.create(1.23), type.asValue(1.23));
        TendrilAssert.assertJValue(JValueFactory.create(4.567), type.asValue(4.567));
    }
    
    /**
     * Verify that a float TypeData works properly
     */
    @Test
    public void testFloat() {
        type = PoDType.FLOAT;
        type.registerImport(mockImports);
        verifyNoInteractions(mockImports);
        verifyDataState(PoDType.FLOAT);

        TendrilAssert.assertJValue(JValueFactory.create(1.23f), type.asValue(1.23f));
        TendrilAssert.assertJValue(JValueFactory.create(4.567f), type.asValue(4.567f));
    }
    
    /**
     * Verify that an int TypeData works properly
     */
    @Test
    public void testInteger() {
        type = PoDType.INT;
        type.registerImport(mockImports);
        verifyNoInteractions(mockImports);
        verifyDataState(PoDType.INT);

        TendrilAssert.assertJValue(JValueFactory.create(123), type.asValue(123));
        TendrilAssert.assertJValue(JValueFactory.create(456), type.asValue(456));
    }
    
    /**
     * Verify that a long TypeData works properly
     */
    @Test
    public void testLong() {
        type = PoDType.LONG;
        type.registerImport(mockImports);
        verifyNoInteractions(mockImports);
        verifyDataState(PoDType.LONG);

        TendrilAssert.assertJValue(JValueFactory.create(1234567890l), type.asValue(1234567890l));
        TendrilAssert.assertJValue(JValueFactory.create(45645645645l), type.asValue(45645645645l));
    }
    
    /**
     * Verify that a short TypeData works properly
     */
    @Test
    public void testShort() {
        type = PoDType.SHORT;
        type.registerImport(mockImports);
        verifyNoInteractions(mockImports);
        verifyDataState(PoDType.SHORT);

        TendrilAssert.assertJValue(JValueFactory.create((short) 789), type.asValue((short) 789));
        TendrilAssert.assertJValue(JValueFactory.create((short) 654), type.asValue((short) 654));
    }
    
    /**
     * Verify that is an invalid object is provided, an exception is thrown
     */
    @Test
    public void testInvalidAsValueObject() {
        Assertions.assertThrows(ClassCastException.class, () -> PoDType.BOOLEAN.asValue(mockImports));
        Assertions.assertThrows(ClassCastException.class, () -> PoDType.BYTE.asValue(mockImports));
        Assertions.assertThrows(ClassCastException.class, () -> PoDType.CHAR.asValue(mockImports));
        Assertions.assertThrows(ClassCastException.class, () -> PoDType.DOUBLE.asValue(mockImports));
        Assertions.assertThrows(ClassCastException.class, () -> PoDType.FLOAT.asValue(mockImports));
        Assertions.assertThrows(ClassCastException.class, () -> PoDType.INT.asValue(mockImports));
        Assertions.assertThrows(ClassCastException.class, () -> PoDType.LONG.asValue(mockImports));
        Assertions.assertThrows(ClassCastException.class, () -> PoDType.SHORT.asValue(mockImports));
    }
    
    /**
     * Verify that the type of is correctly identified
     */
    @Test
    public void testTypeOf() {
        for (PoDType type: PoDType.values()) {
            Assertions.assertEquals(PoDType.BOOLEAN == type, type.isTypeOf(true));
            Assertions.assertEquals(PoDType.BOOLEAN == type, type.isTypeOf(false));
            Assertions.assertEquals(PoDType.BYTE == type, type.isTypeOf(Byte.valueOf("10")));
            Assertions.assertEquals(PoDType.BYTE == type, type.isTypeOf(Byte.valueOf("101")));
            Assertions.assertEquals(PoDType.CHAR == type, type.isTypeOf('q'));
            Assertions.assertEquals(PoDType.CHAR == type, type.isTypeOf('2'));
            Assertions.assertEquals(PoDType.DOUBLE == type, type.isTypeOf(1.23d));
            Assertions.assertEquals(PoDType.DOUBLE == type, type.isTypeOf(2.34d));
            Assertions.assertEquals(PoDType.FLOAT == type, type.isTypeOf(5.67f));
            Assertions.assertEquals(PoDType.FLOAT == type, type.isTypeOf(6.78f));
            Assertions.assertEquals(PoDType.INT == type, type.isTypeOf(123));
            Assertions.assertEquals(PoDType.INT == type, type.isTypeOf(456));
            Assertions.assertEquals(PoDType.LONG == type, type.isTypeOf(123456l));
            Assertions.assertEquals(PoDType.LONG == type, type.isTypeOf(890123l));
            Assertions.assertEquals(PoDType.SHORT == type, type.isTypeOf((short) 987));
            Assertions.assertEquals(PoDType.SHORT == type, type.isTypeOf((short) 654));
        }
    }
    
    /**
     * Verify the data of the data
     * 
     * @param type {@link PoDType} of the data
     */
    private void verifyDataState(PoDType type) {
        verifyDataState(type.toString(), false);
    }
}
