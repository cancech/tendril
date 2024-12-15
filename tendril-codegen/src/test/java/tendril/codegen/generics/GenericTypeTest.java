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
import static org.mockito.Mockito.never;
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
 * Test case for {@link GenericType}
 */
public class GenericTypeTest extends AbstractUnitTest {
    
    // Mocks to use for testing
    @Mock
    private ClassType mockClassType;
    @Mock
    private Set<ClassType> mockImports;
    @Mock
    private Object mockObject;
    @Mock
    private Type mockType;

    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        // Not required
    }

    /**
     * Verify that a wild-card generic works as expected
     */
    @Test
    public void testWildcardGeneric() {
        GenericType gen = new GenericType();
        Assertions.assertTrue(gen.isWildcard());
        verifyAllChecked();
        
        // Verify the core details
        Assertions.assertFalse(gen.isVoid());
        Assertions.assertEquals("?", gen.getSimpleName());
        Assertions.assertThrows(IllegalArgumentException.class, () -> gen.generateDefinition());
        Assertions.assertEquals("?", gen.generateApplication());
        
        // Verify import registration
        gen.registerImport(mockImports);
        verify(mockImports, never()).add(any());
        verifyAllChecked();
        
        // Verify assignability
        Assertions.assertTrue(gen.isTypeOf(mockObject));
        Assertions.assertTrue(gen.isAssignableFrom(mockType));
        verifyAllChecked();
        
        // Verify value conversion
        Assertions.assertEquals(JValueFactory.create(true), gen.asValue(true));
        Assertions.assertEquals(JValueFactory.create("abc123"), gen.asValue("abc123"));
        Assertions.assertEquals(JValueFactory.create(123), gen.asValue(123));
        Assertions.assertEquals(JValueFactory.create(PrimitiveType.BYTE), gen.asValue(PrimitiveType.BYTE));
        verifyAllChecked();
    }

    /**
     * Verify that a wild-card generic works as expected
     */
    @Test
    public void testNamedGeneric() {
        GenericType gen = new GenericType("T");
        Assertions.assertFalse(gen.isWildcard());
        verifyAllChecked();
        
        // Verify the core details
        Assertions.assertFalse(gen.isVoid());
        Assertions.assertEquals("T", gen.getSimpleName());
        Assertions.assertEquals("T", gen.generateDefinition());
        Assertions.assertEquals("T", gen.generateApplication());
        
        // Verify import registration
        gen.registerImport(mockImports);
        verify(mockImports, never()).add(any());
        verifyAllChecked();
        
        // Verify assignability
        Assertions.assertTrue(gen.isTypeOf(mockObject));
        Assertions.assertTrue(gen.isAssignableFrom(mockType));
        verifyAllChecked();
        
        // Verify value conversion
        Assertions.assertEquals(JValueFactory.create(true), gen.asValue(true));
        Assertions.assertEquals(JValueFactory.create("abc123"), gen.asValue("abc123"));
        Assertions.assertEquals(JValueFactory.create(123), gen.asValue(123));
        Assertions.assertEquals(JValueFactory.create(PrimitiveType.BYTE), gen.asValue(PrimitiveType.BYTE));
        verifyAllChecked();
    }

    /**
     * Verify that a wild-card generic works as expected
     */
    @Test
    public void testConcreteClassGeneric() {
        when(mockClassType.getSimpleName()).thenReturn("GenericTypeName");
        GenericType gen = new GenericType(mockClassType);
        Assertions.assertFalse(gen.isWildcard());
        verify(mockClassType).getSimpleName();
        verifyAllChecked();
        
        // Verify the core details
        Assertions.assertFalse(gen.isVoid());
        Assertions.assertEquals("GenericTypeName", gen.getSimpleName());
        Assertions.assertThrows(IllegalArgumentException.class, () -> gen.generateDefinition());
        Assertions.assertEquals("GenericTypeName", gen.generateApplication());
        
        // Verify import registration
        gen.registerImport(mockImports);
        verify(mockImports).add(mockClassType);
        verifyAllChecked();
        
        // Verify assignability
        when(mockClassType.isTypeOf(mockObject)).thenReturn(false);
        Assertions.assertFalse(gen.isTypeOf(mockObject));
        verify(mockClassType).isTypeOf(mockObject);
        when(mockClassType.isTypeOf(mockObject)).thenReturn(true);
        Assertions.assertTrue(gen.isTypeOf(mockObject));
        verify(mockClassType, times(2)).isTypeOf(mockObject);

        when(mockClassType.isAssignableFrom(mockType)).thenReturn(false);
        Assertions.assertFalse(gen.isAssignableFrom(mockType));
        verify(mockClassType).isAssignableFrom(mockType);
        when(mockClassType.isAssignableFrom(mockType)).thenReturn(true);
        Assertions.assertTrue(gen.isAssignableFrom(mockType));
        verify(mockClassType, times(2)).isAssignableFrom(mockType);
        verifyAllChecked();
        
        // Verify value conversion
        when(mockClassType.isTypeOf(any())).thenReturn(false);
        Assertions.assertThrows(IllegalArgumentException.class, () -> gen.asValue(true));
        verify(mockClassType, times(3)).isTypeOf(any());
        Assertions.assertThrows(IllegalArgumentException.class, () -> gen.asValue("abc123"));
        verify(mockClassType, times(4)).isTypeOf(any());
        Assertions.assertThrows(IllegalArgumentException.class, () -> gen.asValue(123));
        verify(mockClassType, times(5)).isTypeOf(any());
        Assertions.assertThrows(IllegalArgumentException.class, () -> gen.asValue(PrimitiveType.BYTE));
        verify(mockClassType, times(6)).isTypeOf(any());
        verifyAllChecked();
        

        when(mockClassType.isTypeOf(any())).thenReturn(true);
        Assertions.assertEquals(JValueFactory.create(true), gen.asValue(true));
        verify(mockClassType, times(7)).isTypeOf(any());
        Assertions.assertEquals(JValueFactory.create("abc123"), gen.asValue("abc123"));
        verify(mockClassType, times(8)).isTypeOf(any());
        Assertions.assertEquals(JValueFactory.create(123), gen.asValue(123));
        verify(mockClassType, times(9)).isTypeOf(any());
        Assertions.assertEquals(JValueFactory.create(PrimitiveType.BYTE), gen.asValue(PrimitiveType.BYTE));
        verify(mockClassType, times(10)).isTypeOf(any());
        verifyAllChecked();
    }
}
