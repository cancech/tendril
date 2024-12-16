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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.PrimitiveType;
import tendril.codegen.field.type.Type;
import tendril.codegen.field.value.JValueFactory;
import tendril.test.AbstractUnitTest;

/**
 * Test case for {@link SimpleClassTypeGeneric}
 */
class SimpleJClassGenericTest extends AbstractUnitTest {

    // Mocks to use for testing
    @Mock
    private Object mockObject;
    @Mock
    private ClassType mockClassType;
    @Mock
    private GenericType mockGenericType;
    @Mock
    private Type mockType;
    @Mock
    private Set<ClassType> mockImports;
    
    // Instance to test
    private SimpleClassTypeGeneric gen;
    
    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        when(mockClassType.getSimpleName()).thenReturn("GenericTypeName");
        gen = new SimpleClassTypeGeneric(mockClassType);
        verify(mockClassType).getSimpleName();
    }
    
    /**
     * Verify that the appropriate name is provided
     */
    @Test
    public void testName() {
        Assertions.assertEquals("GenericTypeName", gen.getSimpleName());
    }

    /**
     * Verify that the appropriate definition is generated.
     */
    @Test
    public void testGenerateDefinition() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> gen.generateDefinition());
    }
    
    /**
     * Verify that the appropriate application is generated
     */
    @Test
    public void testGenerateApplication() {
        Assertions.assertEquals("GenericTypeName", gen.generateApplication());
    }
    
    /**
     * Verify that the imports are properly registered
     */
    @Test
    public void testRegisterImports() {
        gen.registerImport(mockImports);
        verify(mockImports).add(mockClassType);
    }
    
    /**
     * Verify that the "type of" is appropriately determined
     */
    @Test
    public void testTypeOf() {
        // If class type says no, it is not
        when(mockClassType.isTypeOf(mockObject)).thenReturn(false);
        Assertions.assertFalse(gen.isTypeOf(mockObject));
        verify(mockClassType).isTypeOf(mockObject);
        
        // If class type says yes, it is
        when(mockClassType.isTypeOf(mockObject)).thenReturn(true);
        Assertions.assertTrue(gen.isTypeOf(mockObject));
        verify(mockClassType, times(2)).isTypeOf(mockObject);
    }
    
    /**
     * Verify that the assignability is properly determined
     */
    @Test
    public void testAssignableFrom() {
        // If class type says no, it is not
        when(mockClassType.isAssignableFrom(mockType)).thenReturn(false);
        Assertions.assertFalse(gen.isAssignableFrom(mockType));
        verify(mockClassType).isAssignableFrom(mockType);

        // If class type says yes, it is
        when(mockClassType.isAssignableFrom(mockType)).thenReturn(true);
        Assertions.assertTrue(gen.isAssignableFrom(mockType));
        verify(mockClassType, times(2)).isAssignableFrom(mockType);
    }
    
    /**
     * Verify the creation of values
     */
    @Test
    public void testAsValue() {
        // If class type says no, it is not
        when(mockClassType.isTypeOf(any())).thenReturn(false);
        Assertions.assertThrows(IllegalArgumentException.class, () -> gen.asValue(true));
        verify(mockClassType, times(1)).isTypeOf(any());
        Assertions.assertThrows(IllegalArgumentException.class, () -> gen.asValue("abc123"));
        verify(mockClassType, times(2)).isTypeOf(any());
        Assertions.assertThrows(IllegalArgumentException.class, () -> gen.asValue(123));
        verify(mockClassType, times(3)).isTypeOf(any());
        Assertions.assertThrows(IllegalArgumentException.class, () -> gen.asValue(PrimitiveType.BYTE));
        verify(mockClassType, times(4)).isTypeOf(any());

        // If class type says yes, it is
        when(mockClassType.isTypeOf(any())).thenReturn(true);
        Assertions.assertEquals(JValueFactory.create(true), gen.asValue(true));
        verify(mockClassType, times(5)).isTypeOf(any());
        Assertions.assertEquals(JValueFactory.create("abc123"), gen.asValue("abc123"));
        verify(mockClassType, times(6)).isTypeOf(any());
        Assertions.assertEquals(JValueFactory.create(123), gen.asValue(123));
        verify(mockClassType, times(7)).isTypeOf(any());
        Assertions.assertEquals(JValueFactory.create(PrimitiveType.BYTE), gen.asValue(PrimitiveType.BYTE));
        verify(mockClassType, times(8)).isTypeOf(any());
    }
}
