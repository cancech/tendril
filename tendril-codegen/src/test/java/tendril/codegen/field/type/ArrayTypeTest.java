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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.lang.reflect.Array;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.codegen.DefinitionException;
import tendril.codegen.field.value.JValueArray;
import tendril.codegen.field.value.JValueFactory;
import tendril.test.AbstractUnitTest;
import tendril.test.helper.assertions.TendrilAssert;

/**
 * Test case for {@link ArrayType}
 */
public class ArrayTypeTest extends AbstractUnitTest {

    // Mocks to use for testing
    @Mock
    private Type mockType;
    @Mock
    private Set<ClassType> mockImports;
    @Mock
    private Type mockOtherType;
    @Mock
    private ArrayType<Type> mockOtherArrayType;
    
    // Instance to test
    private ArrayType<Type> type;
    
    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        type = new ArrayType<>(mockType);
    }
    
    /**
     * Verify that the contained data type is properly reported
     */
    @Test
    public void testContainedDataType() {
        Assertions.assertEquals(mockType, type.getContainedType());
    }
    

    /**
     * Verify that the array properly registers imports
     */
    @Test
    public void testRegisterImports() {
        type.registerImport(mockImports);
        verify(mockType).registerImport(mockImports);
    }
    
    /**
     * Verify isVoid
     */
    @Test
    public void testIsVoid() {
        when(mockType.isVoid()).thenReturn(true, false);
        Assertions.assertTrue(type.isVoid());
        verify(mockType).isVoid();
        Assertions.assertFalse(type.isVoid());
        verify(mockType, times(2)).isVoid();
    }
    
    /**
     * Verify simple name is properly constructed
     */
    @Test
    public void testGetSimpleName() {
        when(mockType.getSimpleName()).thenReturn("SimpleName");
        Assertions.assertEquals("SimpleName[]", type.getSimpleName());
        verify(mockType).getSimpleName();
    }
    
    /**
     * Verify that isTypeOf properly determines the match
     */
    @Test
    public void testTypeOf() {
        Assertions.assertFalse(type.isTypeOf("abc123"));
        Assertions.assertFalse(type.isTypeOf(123));
        Assertions.assertFalse(type.isTypeOf(new Object()));
        verifyNoInteractions(mockType);

        // False if the type says so
        when(mockType.isAssignableFrom(new ClassType(String.class))).thenReturn(false);
        Assertions.assertFalse(type.isTypeOf(new String[] {}));
        verify(mockType).isAssignableFrom(new ClassType(String.class));
        
        // True if the type says so
        when(mockType.isAssignableFrom(PrimitiveType.INT)).thenReturn(true);
        Assertions.assertTrue(type.isTypeOf(new int[] {}));
        verify(mockType).isAssignableFrom(PrimitiveType.INT);
    }
    
    /**
     * Verify if the array properly determines assignability
     */
    @Test
    public void testNonArrayIsAssignable() {
        when(mockType.isAssignableFrom(mockOtherType)).thenReturn(false, true);
        
        Assertions.assertFalse(type.isAssignableFrom(mockOtherType));
        verify(mockType).isAssignableFrom(mockOtherType);
        Assertions.assertTrue(type.isAssignableFrom(mockOtherType));
        verify(mockType, times(2)).isAssignableFrom(mockOtherType);
    }
    
    /**
     * Verify if the array properly determines assignability
     */
    @Test
    public void testArrayIsAssignable() {
        when(mockOtherArrayType.getContainedType()).thenReturn(mockOtherType);
        when(mockType.isAssignableFrom(mockOtherType)).thenReturn(false, true);
        
        Assertions.assertFalse(type.isAssignableFrom(mockOtherArrayType));
        verify(mockOtherArrayType).getContainedType();
        verify(mockType).isAssignableFrom(mockOtherType);
        Assertions.assertTrue(type.isAssignableFrom(mockOtherArrayType));
        verify(mockOtherArrayType, times(2)).getContainedType();
        verify(mockType, times(2)).isAssignableFrom(mockOtherType);
    }
    
    /**
     * Verify that a {@link JValueArray} can be properly created
     */
    @Test
    public void testAsValue() {
        when(mockType.isAssignableFrom(any())).thenReturn(true);

        TendrilAssert.assertJValue((JValueArray<?, ?>)(Object) JValueFactory.createArray("a", "b", "c"), (JValueArray<?, ?>) type.asValue(new String[] {"a", "b", "c"}));
        verify(mockType).isAssignableFrom(any());
        TendrilAssert.assertJValue((JValueArray<?, ?>)(Object) JValueFactory.createArray(1f, 2f, 3f, 4f, 5f), (JValueArray<?, ?>) type.asValue(new Float[] {1f, 2f, 3f, 4f, 5f}));
        verify(mockType, times(2)).isAssignableFrom(any());

        type = new ArrayType<>(PrimitiveType.INT);
        TendrilAssert.assertJValue((JValueArray<?, ?>)(Object) JValueFactory.createArray(1, 2, 3, 4, 5), (JValueArray<?, ?>) type.asValue(new int[] {1, 2, 3, 4, 5}));
    }
    
    /**
     * Verify that a {@link JValueArray} can be properly created
     */
    @Test
    public void testAsValueIncorrectType() {
        when(mockType.isAssignableFrom(any())).thenReturn(false);
        Assertions.assertThrows(DefinitionException.class, () -> type.asValue(new String[] {"a", "b", "c"}));
        verify(mockType).isAssignableFrom(any());
        verify(mockType, times(2)).getSimpleName();
    }
    
    /**
     * Verify that the proper class type is reported
     */
    @Test
    public void testAsClassType() {
        Assertions.assertEquals(new ClassType(Array.class), type.asClassType());
    }
    
    /**
     * Verify that the toString produces the expected result
     */
    @Test
    public void testToString() {
        Assertions.assertEquals("mockType[]", type.toString());
        Assertions.assertEquals("int[]", new ArrayType<>(PrimitiveType.INT).toString());
        Assertions.assertEquals("a.b.c.D[]", new ArrayType<>(new ClassType("a.b.c.D")).toString());
    }
}
