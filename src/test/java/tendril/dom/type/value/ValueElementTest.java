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

import tendril.dom.type.Type;
import test.AbstractUnitTest;

/**
 * Test case for {@link ValueElement}
 */
public class ValueElementTest extends AbstractUnitTest {
    
    // Mocks to use for testing
    @Mock
    private Type mockDataType;
    @Mock
    private Object mockValue;
    @Mock
    private Type mockOtherDataType;
    
    // Instance to use for testing
    private ValueElement<Type, Object> value;

    /**
     * @see test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        value = new ValueElement<>(mockDataType, mockValue);
    }

    /**
     * Verify that the getters work as expected
     */
    @Test
    public void testGetters() {
        Assertions.assertEquals(mockDataType, value.getType());
        Assertions.assertEquals(mockValue, value.getValue());
    }
    
    /**
     * Verify the instanceof works as expected
     */
    @Test
    public void testInstanceOf() {
        // If either type is null, fail the test
        Assertions.assertFalse(value.isInstanceOf(null));
        Assertions.assertFalse(new ValueElement<>(null, mockValue).isInstanceOf(mockOtherDataType));
        
        // If the equals check fails, check fails
        when(mockDataType.isAssignableTo(mockOtherDataType)).thenReturn(false);
        Assertions.assertFalse(value.isInstanceOf(mockOtherDataType));
        verify(mockDataType).isAssignableTo(mockOtherDataType);
        
        // If the equals check passes, check passes
        when(mockDataType.isAssignableTo(mockOtherDataType)).thenReturn(true);
        Assertions.assertTrue(value.isInstanceOf(mockOtherDataType));
        verify(mockDataType, times(2)).isAssignableTo(mockOtherDataType);
    }
}
