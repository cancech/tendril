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
package tendril.codegen.generics;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.codegen.CodeGenerationException;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.PrimitiveType;
import tendril.codegen.field.type.Type;
import tendril.codegen.field.value.JValueFactory;
import tendril.test.AbstractUnitTest;

/**
 * Test case for {@link SimpleWildcardGeneric}
 */
class SimpleWildcardGenericTest extends AbstractUnitTest {

    // Mocks to use for testing
    @Mock
    private Object mockObject;
    @Mock
    private ClassType mockClassType;
    @Mock
    private GenericType mockGenericType;
    @Mock
    private Type mockType;
    
    // Instance to test
    private SimpleWildcardGeneric gen;
    
    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        gen = new SimpleWildcardGeneric();
    }
    
    /**
     * Verify that the appropriate name is provided
     */
    @Test
    public void testName() {
        Assertions.assertEquals("?", gen.getSimpleName());
    }

    /**
     * Verify that the appropriate definition is generated.
     */
    @Test
    public void testGenerateDefinition() {
        Assertions.assertThrows(CodeGenerationException.class, () -> gen.generateDefinition());
    }
    
    /**
     * Verify that the appropriate application is generated
     */
    @Test
    public void testGenerateApplication() {
        Assertions.assertEquals("?", gen.generateApplication());
    }
    
    /**
     * Verify that the "type of" is appropriately determined
     */
    @Test
    public void testTypeOf() {
        Assertions.assertTrue(gen.isTypeOf(123));
        Assertions.assertTrue(gen.isTypeOf(1.23));
        Assertions.assertTrue(gen.isTypeOf(true));
        Assertions.assertTrue(gen.isTypeOf("abc123"));
        Assertions.assertTrue(gen.isTypeOf(PrimitiveType.BYTE));
        Assertions.assertTrue(gen.isTypeOf(mockObject));
    }
    
    /**
     * Verify that the assignability is properly determined
     */
    @Test
    public void testAssignableFrom() {
        for (PrimitiveType p: PrimitiveType.values())
            Assertions.assertTrue(gen.isAssignableFrom(p));
        Assertions.assertTrue(gen.isAssignableFrom(mockClassType));
        Assertions.assertTrue(gen.isAssignableFrom(mockGenericType));
        Assertions.assertTrue(gen.isAssignableFrom(mockType));
    }
    
    /**
     * Verify the creation of values
     */
    @Test
    public void testAsValue() {
        Assertions.assertEquals(JValueFactory.create(true), gen.asValue(true));
        Assertions.assertEquals(JValueFactory.create("abc123"), gen.asValue("abc123"));
        Assertions.assertEquals(JValueFactory.create(123), gen.asValue(123));
        Assertions.assertEquals(JValueFactory.create(PrimitiveType.BYTE), gen.asValue(PrimitiveType.BYTE));
    }

    /**
     * Verify that the generic is properly determining equality
     */
    @SuppressWarnings("unlikely-arg-type")
    @Test
    public void testEquals() {
        Assertions.assertTrue(gen.equals(new SimpleWildcardGeneric()));
        Assertions.assertFalse(gen.equals(new SimpleGeneric("Abc123")));
        Assertions.assertFalse(gen.equals(new SimpleGeneric("?")));
        Assertions.assertFalse(gen.equals(Integer.valueOf(123)));
    }
}
