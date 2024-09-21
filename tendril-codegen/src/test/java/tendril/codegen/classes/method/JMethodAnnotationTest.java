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
package tendril.codegen.classes.method;

import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.codegen.field.type.Type;
import tendril.codegen.field.value.JValue;

/**
 * Test case for {@link JMethodAnnotation}
 */
public class JMethodAnnotationTest extends AbstractMethodTest {
    
    // Mocks to use for testing
    @Mock
    private JValue<Type, ?> mockDefaultValue;
    
    /**
     * Verify that the method signature ending is properly generated if no default value is provided
     */
    @Test
    public void testMethodSignatureEndNoDefaultValue() {
        JMethodAnnotation<Type> method = new JMethodAnnotation<Type>(mockReturnType, "attribute", null);
        Assertions.assertEquals(";", method.generateSignatureEnd(true));
    }
    
    /**
     * Verify that the method signature ending is properly generated if a default value is provided
     */
    @Test
    public void testMethodSignatureEndWithDefaultValue() {
        JMethodAnnotation<Type> method = new JMethodAnnotation<Type>(mockReturnType, "attribute", mockDefaultValue);
        
        when(mockDefaultValue.generate(anySet())).thenReturn("mockValue");
        Assertions.assertEquals(" default mockValue;", method.generateSignatureEnd(false));
        verify(mockDefaultValue).generate(anySet());
    }

}
