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

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import tendril.codegen.DefinitionException;
import tendril.codegen.VisibilityType;
import tendril.codegen.field.JType;
import tendril.codegen.field.value.JValue;
import tendril.codegen.field.value.JValueFactory;
import tendril.test.helper.assertions.TendrilAssert;

/**
 * Test case for {@link ClassType}
 */
public class ClassTypeTest extends SharedTypeTest<ClassType> {

    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        type = new ClassType(VisibilityType.class);
    }

    /**
     * Verify that the correct import is registered
     */
    @Test
    public void testRegisterImports() {
        type.registerImport(mockImports);
        verify(mockImports).add(type);
        verifyDataState();
    }

    /** Listing of all class types that are to be used for testing asValue */
    private static final Class<?>[] allToTest = { VisibilityType.class, String.class, Boolean.class, Byte.class, Character.class, Double.class, Float.class, Integer.class, Long.class, Short.class };
    /** Listing of all values that are to be passed for testing asValue */
    private static final Object[] testValues = { VisibilityType.PUBLIC, "abc123", true, Byte.valueOf("10"), 'b', 1.23d, 2.34f, 345, 456l, Short.valueOf((short) 567) };

    /**
     * Verify that the appropriate Class element is created
     */
    @Test
    public void testAsValue() {
        // Enum, String, and Primitives properly generate a values (of the correct type)
        verifyAsValue(VisibilityType.class, JValueFactory.create((VisibilityType) testValues[0]));
        verifyAsValue(String.class, JValueFactory.create((String) testValues[1]));
        verifyAsValue(Boolean.class, JValueFactory.create((boolean) testValues[2]));
        verifyAsValue(Byte.class, JValueFactory.create((byte) testValues[3]));
        verifyAsValue(Character.class, JValueFactory.create((char) testValues[4]));
        verifyAsValue(Double.class, JValueFactory.create((double) testValues[5]));
        verifyAsValue(Float.class, JValueFactory.create((float) testValues[6]));
        verifyAsValue(Integer.class, JValueFactory.create((int) testValues[7]));
        verifyAsValue(Long.class, JValueFactory.create((long) testValues[8]));
        verifyAsValue(Short.class, JValueFactory.create((short) testValues[9]));
        
        // Everything else generates an exception
        type = new ClassType(ClassType.class);
        verifyDataState(ClassType.class);
        Assertions.assertThrows(DefinitionException.class, () -> type.asValue(new ClassType(getClass())));
    }

    /**
     * Verify that the correct {@link JValue} is created or exception thrown when using {@link ClassType} to generate a {@link JValue}
     * 
     * @param <T>           the type of class which is to be stored in the {@link ClassType}
     * @param valueClass    {@link Class} where the class represented by the {@link ClassType} is defined
     * @param expectedValue {@link JValue} that is expected to be produced
     */
    private <T> void verifyAsValue(Class<T> valueClass, JValue<?, ?> expectedValue) {
        type = new ClassType(valueClass);
        verifyDataState(valueClass);

        for (int i = 0; i < allToTest.length; i++) {
            Class<?> test = allToTest[i];
            Object value = testValues[i];

            if ((valueClass.isEnum() && test.equals(Enum.class)) || test.equals(valueClass))
                TendrilAssert.assertJValue(expectedValue, type.asValue(value));
            else
                Assertions.assertThrows(DefinitionException.class, () -> type.asValue(value));
        }

        verifyDataState(valueClass);
    }

    /**
     * Verify that the various CTORs work as expected
     */
    @Test
    public void testCtor() {
        TendrilAssert.assertImportData(JType.class, new ClassType(JType.class));
        TendrilAssert.assertImportData("a.b.c.d", "EfGh", new ClassType("a.b.c.d.EfGh"));
        TendrilAssert.assertImportData("1.2.3.4", "Abcd", new ClassType("1.2.3.4", "Abcd"));
    }

    /**
     * Verify that a new class data is properly generated from an existing one when a class name suffix is supplied
     */
    @Test
    public void testGenerateFromSuffix() {
        TendrilAssert.assertImportData(VisibilityType.class.getPackageName(), VisibilityType.class.getSimpleName() + "Suffix", type.generateFromClassSuffix("Suffix"));
        TendrilAssert.assertImportData("a.b.c.d", "EfGhQwerty", new ClassType("a.b.c.d.EfGh").generateFromClassSuffix("Qwerty"));
        TendrilAssert.assertImportData("1.2.3.4", "AbcdEfgh", new ClassType("1.2.3.4", "Abcd").generateFromClassSuffix("Efgh"));
    }

    /**
     * Verify that the assignment check is properly performed
     */
    @Test
    public void testAssignableTo() {
        ClassType lhs = new ClassType("a.b.c.d.E");

        // These are expected to fail
        for (PrimitiveType pd : PrimitiveType.values()) {
            Assertions.assertFalse(lhs.isAssignableFrom(pd));
        }
        Assertions.assertFalse(lhs.isAssignableFrom(VoidType.INSTANCE));
        Assertions.assertFalse(lhs.isAssignableFrom(new ClassType("a.b.c.D")));
        Assertions.assertFalse(lhs.isAssignableFrom(new ClassType(JType.class)));
        Assertions.assertFalse(lhs.isAssignableFrom(new ClassType("a.b.c.d.e")));

        // These are expected to pass
        Assertions.assertTrue(lhs.isAssignableFrom(new ClassType("a.b.c.d.E")));
        Assertions.assertTrue(lhs.isAssignableFrom(new ClassType("a.b.c.d", "E")));
        lhs = new ClassType(JType.class);
        Assertions.assertTrue(lhs.isAssignableFrom(new ClassType(JType.class)));
        Assertions.assertTrue(lhs.isAssignableFrom(new ClassType(JType.class.getName())));
        Assertions.assertTrue(lhs.isAssignableFrom(new ClassType(JType.class.getPackageName(), JType.class.getSimpleName())));
    }

    /**
     * Verify that the data state is correct
     */
    private void verifyDataState() {
        verifyDataState(VisibilityType.class);
    }

    /**
     * Verify that the data state is correct
     */
    private void verifyDataState(Class<?> expectedClass) {
        verifyDataState(expectedClass.getSimpleName(), false);
    }
    
    /**
     * Verify that the fully qualified name is proper generated
     */
    @Test
    public void testFullyQualifiedName() {
        ClassType data = new ClassType(JType.class);
        Assertions.assertEquals(JType.class.getName(), data.getFullyQualifiedName());
        Assertions.assertEquals(JType.class.getName(), data.toString());
        
        data = new ClassType("a.b.c.d.EfGh");
        Assertions.assertEquals("a.b.c.d.EfGh", data.getFullyQualifiedName());
        Assertions.assertEquals("a.b.c.d.EfGh", data.toString());
        
        data = new ClassType("1.2.3.4", "Abcd");
        Assertions.assertEquals("1.2.3.4.Abcd", data.getFullyQualifiedName());
        Assertions.assertEquals("1.2.3.4.Abcd", data.toString());
    }
    
    /**
     * Verify that a class that appears in the default package generates an exception
     */
    @Test
    public void testClassInDefaultPackage() {
        Assertions.assertThrows(DefinitionException.class, () -> new ClassType("SomeClass"));
        Assertions.assertThrows(DefinitionException.class, () -> new ClassType(null, "SomeOtherClass"));
        Assertions.assertThrows(DefinitionException.class, () -> new ClassType("", "YetOtherClass"));
        Assertions.assertThrows(DefinitionException.class, () -> new ClassType(" ", "MyClass"));
        Assertions.assertThrows(DefinitionException.class, () -> new ClassType("        ", "MyOtherClass"));
    }
    
    /**
     * Verify that the hash code is properly generated
     */
    @Test
    public void testHashCode() {
        String packageName = "my.package.com.abc123";
        String className = "MyClassName";
        Assertions.assertEquals(packageName.hashCode() + className.hashCode(), new ClassType(packageName, className).hashCode());
    }
    
    /**
     * Verify that equals works as expected
     */
    @SuppressWarnings("unlikely-arg-type")
    @Test
    public void testEquals() {
        ClassType lhs = new ClassType("a.b.c.d.E");
        
        // These are expected to fail
        Assertions.assertFalse(lhs.equals("abc123"));
        Assertions.assertFalse(lhs.equals(new ClassType("a.b.c.D")));
        Assertions.assertFalse(lhs.equals(new ClassType(JType.class)));
        Assertions.assertFalse(lhs.equals(new ClassType("a.b.c.d.e")));
        
        // These are expected to pass
        Assertions.assertTrue(lhs.equals(new ClassType("a.b.c.d.E")));
        Assertions.assertTrue(lhs.equals(new ClassType("a.b.c.d", "E")));
        lhs = new ClassType(JType.class);
        Assertions.assertTrue(lhs.equals(new ClassType(JType.class)));
        Assertions.assertTrue(lhs.equals(new ClassType(JType.class.getName())));
        Assertions.assertTrue(lhs.equals(new ClassType(JType.class.getPackageName(), JType.class.getSimpleName())));
    }
}
