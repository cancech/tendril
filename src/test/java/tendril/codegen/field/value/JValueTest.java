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
package tendril.codegen.field.value;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.Type;

/**
 * Test case for {@link JValue}
 */
public class JValueTest extends SharedJValueTest {
    
    private static final String GENERATED_TEXT = "Generated Code";
    
    /**
     * Concrete implementation of JValue to be used for testing
     */
    private class TestJValue extends JValue<Type, Type> {
        
        /** Counter for how many times generate() was called */
        private int timesGenerateCalled = 0;

        /**
         * CTOR 
         */
        protected TestJValue() {
            super(mockType, mockValue);
        }

        /**
         * @see tendril.codegen.field.value.JValue#generate(java.util.Set)
         */
        @Override
        public String generate(Set<ClassType> classImports) {
            timesGenerateCalled++;
            Assertions.assertEquals(mockImports, classImports);
            return GENERATED_TEXT;
        }
        
        /**
         * Verify that generate() was called the expected number of times
         * 
         * @param expected int times generate() should have been called
         */
        private void verifyTimesGenerateCalled(int expected) {
            Assertions.assertEquals(expected, timesGenerateCalled);
        }
        
    }
    
    // Mocks to use for testing
    @Mock
    private Type mockType;
    @Mock
    private Type mockValue;
    @Mock
    private Type mockOtherDataType;
    
    // Instance to test
    private TestJValue value;

    /**
     * @see test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        value = new TestJValue();
    }

    /**
     * Verify that generate does what is expected
     */
    @Test
    public void testGenerate() {
        assertCode(GENERATED_TEXT, value);
        value.verifyTimesGenerateCalled(1);
        verifyNoInteractions(mockValue);
    }
    /**
     * Verify that the getters work as expected
     */
    @Test
    public void testGetters() {
        Assertions.assertEquals(mockType, value.getType());
        Assertions.assertEquals(mockValue, value.getValue());
    }
    
    /**
     * Verify the instanceof works as expected
     */
    @Test
    public void testInstanceOf() {
        // If either type is null, fail the test
        Assertions.assertFalse(value.isInstanceOf(null));
        
        // If the equals check fails, check fails
        when(mockType.isAssignableTo(mockOtherDataType)).thenReturn(false);
        Assertions.assertFalse(value.isInstanceOf(mockOtherDataType));
        verify(mockType).isAssignableTo(mockOtherDataType);
        
        // If the equals check passes, check passes
        when(mockType.isAssignableTo(mockOtherDataType)).thenReturn(true);
        Assertions.assertTrue(value.isInstanceOf(mockOtherDataType));
        verify(mockType, times(2)).isAssignableTo(mockOtherDataType);
    }
}
