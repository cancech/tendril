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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.codegen.CodeGenerationException;
import tendril.codegen.field.type.ClassType;
import tendril.test.AbstractUnitTest;

/**
 * Test case for {@link CompoundSuperGeneric}
 */
public class CompoundSuperGenericTest extends AbstractUnitTest {
    
    // Mocks to use for testing
    @Mock
    private ClassType mockParent;
    
    // Instance to test
    private CompoundSuperGeneric gen;

    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        gen = new CompoundSuperGeneric(mockParent);
    }
    
    /**
     * Verify that the correct keyword is produced.
     */
    @Test
    public void testKeyword() {
        Assertions.assertEquals("super ", gen.getKeyword());
    }

    /**
     * Verify that a definition cannot be produced.
     */
    @Test
    public void testGenerateDefinition() {
        Assertions.assertThrows(CodeGenerationException.class, () -> gen.generateDefinition());
    }

    /**
     * Verify that an application can be produced
     */
    @Test
    public void testGenerateApplication() {
        when(mockParent.getSimpleName()).thenReturn("mockParent");
        Assertions.assertEquals("? super mockParent", gen.generateApplication());
        verify(mockParent).getSimpleName();
    }
}
