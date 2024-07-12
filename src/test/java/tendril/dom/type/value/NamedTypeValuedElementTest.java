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
package tendril.dom.type.value;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.codegen.field.type.TypeData;
import tendril.dom.type.Type;
import test.AbstractUnitTest;

/**
 * Test case for {@link NamedTypeValuedElement}
 */
public class NamedTypeValuedElementTest extends AbstractUnitTest {

    // Mocks to use for testing
    @Mock
    private TypeData<Type> mockDataType;
    @Mock
    private Type mockType;
    @Mock
    private ValueElement<Type, Object> mockValue;

    // Instance to test
    private NamedTypeValuedElement<Type, Object> element;

    /**
     * @see test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        when(mockDataType.isVoid()).thenReturn(false);
        when(mockDataType.getDataType()).thenReturn(mockType);
        when(mockValue.isInstanceOf(mockType)).thenReturn(true);
        
        element = new NamedTypeValuedElement<Type, Object>(mockDataType, "elementName", mockValue);
        
        verify(mockDataType).isVoid();
        verify(mockDataType).getDataType();
        verify(mockValue).isInstanceOf(mockType);
    }

    /**
     * Verify that the value from a legitimate element is properly retrieved
     */
    @Test
    public void testValue() {
        Assertions.assertEquals(mockValue, element.getValue());
    }
    
    /**
     * Verify that the sanity checks for creating an element work as expected
     */
    @Test
    public void testInvalidTypeOrValue() {
        when(mockDataType.isVoid()).thenReturn(true, false);
        Assertions.assertThrows(IllegalArgumentException.class, () -> new NamedTypeValuedElement<Type, Object>(mockDataType, "elementName", mockValue));
        verify(mockDataType, times(2)).isVoid();
        verifyAllChecked();
        
        when(mockValue.isInstanceOf(mockType)).thenReturn(false);
        Assertions.assertThrows(IllegalArgumentException.class, () -> new NamedTypeValuedElement<Type, Object>(mockDataType, "elementName", mockValue));
        verify(mockDataType, times(3)).isVoid();
        verify(mockDataType, times(2)).getDataType();
        verify(mockValue).getType();
    }
}
