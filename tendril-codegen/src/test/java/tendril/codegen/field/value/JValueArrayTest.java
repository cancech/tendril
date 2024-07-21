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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.Type;
import tendril.test.AbstractUnitTest;

/**
 * Test case for {@link JValueArray}
 */
public class JValueArrayTest extends AbstractUnitTest {
    
    // Mocks to use for testing
    @Mock
    private Type mockType;
    @Mock
    private JValue<Type, Type> mockValue1;
    @Mock
    private JValue<Type, Type> mockValue2;
    @Mock
    private JValue<Type, Type> mockValue3;
    @Mock
    private Set<ClassType> mockImports;

    // Instance to use for testing
    private JValueArray<Type, Type> value;
    
    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        value = new JValueArray<>(mockType, Arrays.asList(mockValue1, mockValue2, mockValue3));
    }
    
    /**
     * Verify that generate produces the appropriate code
     */
    @Test
    public void testGenerate() {
        when(mockValue1.generate(mockImports)).thenReturn("mockValue1");
        when(mockValue2.generate(mockImports)).thenReturn("mockValue2");
        when(mockValue3.generate(mockImports)).thenReturn("mockValue3");
        
        Assertions.assertEquals("{mockValue1, mockValue2, mockValue3}", value.generate(mockImports));
        verify(mockValue1).generate(mockImports);
        verify(mockValue2).generate(mockImports);
        verify(mockValue3).generate(mockImports);
    }

}
