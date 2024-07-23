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
import tendril.test.helper.assertions.TendrilAssert;

/**
 * Test case for {@link PrimitiveType}
 */
public class PrimitiveTypeTest extends SharedTypeTest<PrimitiveType> {

    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        type = PrimitiveType.BOOLEAN;
    }
    
    /**
     * Verify that the correct {@link PrimitiveType} is provided for the class
     */
    @Test
    public void testFromClass() {
        Assertions.assertEquals(PrimitiveType.BOOLEAN, PrimitiveType.from(Boolean.class));
        Assertions.assertEquals(PrimitiveType.BOOLEAN, PrimitiveType.from(boolean.class));
        Assertions.assertEquals(PrimitiveType.BYTE, PrimitiveType.from(Byte.class));
        Assertions.assertEquals(PrimitiveType.BYTE, PrimitiveType.from(byte.class));
        Assertions.assertEquals(PrimitiveType.CHAR, PrimitiveType.from(Character.class));
        Assertions.assertEquals(PrimitiveType.CHAR, PrimitiveType.from(char.class));
        Assertions.assertEquals(PrimitiveType.DOUBLE, PrimitiveType.from(Double.class));
        Assertions.assertEquals(PrimitiveType.DOUBLE, PrimitiveType.from(double.class));
        Assertions.assertEquals(PrimitiveType.FLOAT, PrimitiveType.from(Float.class));
        Assertions.assertEquals(PrimitiveType.FLOAT, PrimitiveType.from(float.class));
        Assertions.assertEquals(PrimitiveType.INT, PrimitiveType.from(Integer.class));
        Assertions.assertEquals(PrimitiveType.INT, PrimitiveType.from(int.class));
        Assertions.assertEquals(PrimitiveType.LONG, PrimitiveType.from(Long.class));
        Assertions.assertEquals(PrimitiveType.LONG, PrimitiveType.from(long.class));
        Assertions.assertEquals(PrimitiveType.SHORT, PrimitiveType.from(Short.class));
        Assertions.assertEquals(PrimitiveType.SHORT, PrimitiveType.from(short.class));
        Assertions.assertThrows(IllegalArgumentException.class, () -> PrimitiveType.from(ClassType.class));
    }
    
    /**
     * Verify elements produce the proper string
     */
    @Test
    public void testToString() {
        Assertions.assertEquals(8, PrimitiveType.values().length);
        Assertions.assertEquals("boolean", PrimitiveType.BOOLEAN.toString());
        Assertions.assertEquals("byte", PrimitiveType.BYTE.toString());
        Assertions.assertEquals("char", PrimitiveType.CHAR.toString());
        Assertions.assertEquals("double", PrimitiveType.DOUBLE.toString());
        Assertions.assertEquals("float", PrimitiveType.FLOAT.toString());
        Assertions.assertEquals("int", PrimitiveType.INT.toString());
        Assertions.assertEquals("long", PrimitiveType.LONG.toString());
        Assertions.assertEquals("short", PrimitiveType.SHORT.toString());
    }
    
    /**
     * Verify that the assignability is properly configured
     */
    @Test
    public void testAssignable() {
        for (PrimitiveType i: PrimitiveType.values()) {
            for (PrimitiveType j: PrimitiveType.values())
                Assertions.assertEquals(i == j, i.isAssignableFrom(j));
        }
    }
    
    /**
     * Verify that a boolean TypeData works properly
     */
    @Test
    public void testBoolean() {
        type = PrimitiveType.BOOLEAN;
        type.registerImport(mockImports);
        verifyNoInteractions(mockImports);
        verifyDataState(PrimitiveType.BOOLEAN);

        TendrilAssert.assertJValue(JValueFactory.create(true), type.asValue(true));
        TendrilAssert.assertJValue(JValueFactory.create(false), type.asValue(false));
    }
    
    /**
     * Verify that a byte TypeData works properly
     */
    @Test
    public void testByte() {
        type = PrimitiveType.BYTE;
        type.registerImport(mockImports);
        verifyNoInteractions(mockImports);
        verifyDataState(PrimitiveType.BYTE);

        TendrilAssert.assertJValue(JValueFactory.create(Byte.valueOf("10")), type.asValue(Byte.valueOf("10")));
        TendrilAssert.assertJValue(JValueFactory.create(Byte.valueOf("101")), type.asValue(Byte.valueOf("101")));
    }
    
    /**
     * Verify that a char TypeData works properly
     */
    @Test
    public void testChar() {
        type = PrimitiveType.CHAR;
        type.registerImport(mockImports);
        verifyNoInteractions(mockImports);
        verifyDataState(PrimitiveType.CHAR);

        TendrilAssert.assertJValue(JValueFactory.create('q'), type.asValue('q'));
        TendrilAssert.assertJValue(JValueFactory.create('b'), type.asValue('b'));
    }
    
    /**
     * Verify that a double TypeData works properly
     */
    @Test
    public void testDouble() {
        type = PrimitiveType.DOUBLE;
        type.registerImport(mockImports);
        verifyNoInteractions(mockImports);
        verifyDataState(PrimitiveType.DOUBLE);

        TendrilAssert.assertJValue(JValueFactory.create(1.23), type.asValue(1.23));
        TendrilAssert.assertJValue(JValueFactory.create(4.567), type.asValue(4.567));
    }
    
    /**
     * Verify that a float TypeData works properly
     */
    @Test
    public void testFloat() {
        type = PrimitiveType.FLOAT;
        type.registerImport(mockImports);
        verifyNoInteractions(mockImports);
        verifyDataState(PrimitiveType.FLOAT);

        TendrilAssert.assertJValue(JValueFactory.create(1.23f), type.asValue(1.23f));
        TendrilAssert.assertJValue(JValueFactory.create(4.567f), type.asValue(4.567f));
    }
    
    /**
     * Verify that an int TypeData works properly
     */
    @Test
    public void testInteger() {
        type = PrimitiveType.INT;
        type.registerImport(mockImports);
        verifyNoInteractions(mockImports);
        verifyDataState(PrimitiveType.INT);

        TendrilAssert.assertJValue(JValueFactory.create(123), type.asValue(123));
        TendrilAssert.assertJValue(JValueFactory.create(456), type.asValue(456));
    }
    
    /**
     * Verify that a long TypeData works properly
     */
    @Test
    public void testLong() {
        type = PrimitiveType.LONG;
        type.registerImport(mockImports);
        verifyNoInteractions(mockImports);
        verifyDataState(PrimitiveType.LONG);

        TendrilAssert.assertJValue(JValueFactory.create(1234567890l), type.asValue(1234567890l));
        TendrilAssert.assertJValue(JValueFactory.create(45645645645l), type.asValue(45645645645l));
    }
    
    /**
     * Verify that a short TypeData works properly
     */
    @Test
    public void testShort() {
        type = PrimitiveType.SHORT;
        type.registerImport(mockImports);
        verifyNoInteractions(mockImports);
        verifyDataState(PrimitiveType.SHORT);

        TendrilAssert.assertJValue(JValueFactory.create((short) 789), type.asValue((short) 789));
        TendrilAssert.assertJValue(JValueFactory.create((short) 654), type.asValue((short) 654));
    }
    
    /**
     * Verify that is an invalid object is provided, an exception is thrown
     */
    @Test
    public void testInvalidAsValueObject() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> PrimitiveType.BOOLEAN.asValue(mockImports));
        Assertions.assertThrows(IllegalArgumentException.class, () -> PrimitiveType.BYTE.asValue(mockImports));
        Assertions.assertThrows(IllegalArgumentException.class, () -> PrimitiveType.CHAR.asValue(mockImports));
        Assertions.assertThrows(IllegalArgumentException.class, () -> PrimitiveType.DOUBLE.asValue(mockImports));
        Assertions.assertThrows(IllegalArgumentException.class, () -> PrimitiveType.FLOAT.asValue(mockImports));
        Assertions.assertThrows(IllegalArgumentException.class, () -> PrimitiveType.INT.asValue(mockImports));
        Assertions.assertThrows(IllegalArgumentException.class, () -> PrimitiveType.LONG.asValue(mockImports));
        Assertions.assertThrows(IllegalArgumentException.class, () -> PrimitiveType.SHORT.asValue(mockImports));
    }
    
    /**
     * Verify that the type of is correctly identified
     */
    @Test
    public void testTypeOf() {
        for (PrimitiveType type: PrimitiveType.values()) {
            Assertions.assertEquals(PrimitiveType.BOOLEAN == type, type.isTypeOf(true));
            Assertions.assertEquals(PrimitiveType.BOOLEAN == type, type.isTypeOf(false));
            Assertions.assertEquals(PrimitiveType.BYTE == type, type.isTypeOf(Byte.valueOf("10")));
            Assertions.assertEquals(PrimitiveType.BYTE == type, type.isTypeOf(Byte.valueOf("101")));
            Assertions.assertEquals(PrimitiveType.CHAR == type, type.isTypeOf('q'));
            Assertions.assertEquals(PrimitiveType.CHAR == type, type.isTypeOf('2'));
            Assertions.assertEquals(PrimitiveType.DOUBLE == type, type.isTypeOf(1.23d));
            Assertions.assertEquals(PrimitiveType.DOUBLE == type, type.isTypeOf(2.34d));
            Assertions.assertEquals(PrimitiveType.FLOAT == type, type.isTypeOf(5.67f));
            Assertions.assertEquals(PrimitiveType.FLOAT == type, type.isTypeOf(6.78f));
            Assertions.assertEquals(PrimitiveType.INT == type, type.isTypeOf(123));
            Assertions.assertEquals(PrimitiveType.INT == type, type.isTypeOf(456));
            Assertions.assertEquals(PrimitiveType.LONG == type, type.isTypeOf(123456l));
            Assertions.assertEquals(PrimitiveType.LONG == type, type.isTypeOf(890123l));
            Assertions.assertEquals(PrimitiveType.SHORT == type, type.isTypeOf((short) 987));
            Assertions.assertEquals(PrimitiveType.SHORT == type, type.isTypeOf((short) 654));
            Assertions.assertFalse(type.isTypeOf(null));
        }
    }
    
    /**
     * Verify the data of the data
     * 
     * @param type {@link PrimitiveType} of the data
     */
    private void verifyDataState(PrimitiveType type) {
        verifyDataState(type.toString(), false);
    }
}
