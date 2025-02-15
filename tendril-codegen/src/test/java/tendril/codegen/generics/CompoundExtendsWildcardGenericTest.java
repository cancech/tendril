/*
 * Copyright 2025 Jaroslav Bosak
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

import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.codegen.CodeGenerationException;
import tendril.codegen.field.type.ClassType;
import tendril.test.AbstractUnitTest;

/**
 * Test case for {@link CompoundExtendsWildcardGeneric}
 */
public class CompoundExtendsWildcardGenericTest  extends AbstractUnitTest {

    // Mocks to use for testing
    @Mock
    private ClassType mockParent1;
    @Mock
    private ClassType mockParent2;
    @Mock
    private ClassType mockParent3;
    
    // Instance to test
    private CompoundExtendsWildcardGeneric gen;
    
    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        gen = new CompoundExtendsWildcardGeneric(Arrays.asList(mockParent1, mockParent2, mockParent3));
    }
    
    /**
     * Verify that the definition cannot be produced.
     */
    @Test
    public void testGenerateDefinition() {
        Assertions.assertThrows(CodeGenerationException.class, () -> gen.generateDefinition());
    }
    
    /**
     * Verify that the application can be produced
     */
    @Test
    public void testGenerateApplication() {
        when(mockParent1.getSimpleName()).thenReturn("mockParent1");
        when(mockParent2.getSimpleName()).thenReturn("mockParent2");
        when(mockParent3.getSimpleName()).thenReturn("mockParent3");
        Assertions.assertEquals("? extends mockParent1 & mockParent2 & mockParent3", gen.generateApplication());
        verify(mockParent1).getSimpleName();
        verify(mockParent2).getSimpleName();
        verify(mockParent3).getSimpleName();
    }
}
