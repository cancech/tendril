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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.codegen.classes.JClass;
import tendril.codegen.field.type.ClassType;
import tendril.test.AbstractUnitTest;

/**
 * Test case for {@link SimpleJClassGeneric}
 */
class SimpleClassTypeGenericTest extends AbstractUnitTest {

    // Mocks to use for testing
    @Mock
    private JClass mockClass;
    @Mock
    private ClassType mockClassType;
    
    // Instance to test
    private SimpleJClassGeneric gen;
    
    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        when(mockClass.getType()).thenReturn(mockClassType);
        when(mockClassType.getSimpleName()).thenReturn("GenericJClassName");
        gen = new SimpleJClassGeneric(mockClass);
        verify(mockClass).getType();
        verify(mockClassType).getSimpleName();
    }
    
    /**
     * Verify that the appropriate name is provided
     */
    @Test
    public void testName() {
        Assertions.assertEquals("GenericJClassName", gen.getSimpleName());
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
        when(mockClass.getAppliedCode(false)).thenReturn("APPLIED_JCLASS_CODE");
        Assertions.assertEquals("APPLIED_JCLASS_CODE", gen.generateApplication());
        verify(mockClass).getAppliedCode(false);
    }
}
