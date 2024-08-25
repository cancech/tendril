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
package tendril.processor;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.codegen.field.type.ClassType;
import tendril.test.AbstractUnitTest;

/**
 * Test case for {@link ClassDefinition}
 */
public class ClassDefinitionTest extends AbstractUnitTest {
    
    // Mocks to use for testing
    @Mock
    private ClassType mockType;
    
    // Instance to test
    private ClassDefinition def;

    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        def = new ClassDefinition(mockType, "code");
    }
    
    /**
     * Verify that the type can be properly retrieved
     */
    @Test
    public void testType() {
        Assertions.assertEquals(mockType, def.getType());
    }

    /**
     * Verify that the code can be properly retrieved
     */
    @Test
    public void testCode() {
        Assertions.assertEquals("code", def.getCode());
    }
}
