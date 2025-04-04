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

import static org.mockito.Mockito.verifyNoInteractions;

import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.mockito.Mock;

import tendril.codegen.field.type.ClassType;
import tendril.test.AbstractUnitTest;

/**
 * Test case covering the shared/common needs for JValue tests
 */
public class SharedJValueTest extends AbstractUnitTest {
    
    // Mocks to use for testing
    @Mock
    protected Set<ClassType> mockImports;

    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        // Nothing to do
    }

    /**
     * Verify that the code generated by the {@link JValue} is correct
     * 
     * @param expectedCode {@link String} the code that should be generated
     * @param actualValue {@link JValue} which generates the code
     */
    protected void assertCode(String expectedCode, JValue<?, ?> actualValue) {
        Assertions.assertEquals(expectedCode, actualValue.generate(mockImports));
        verifyMockImports();
    }
    
    /**
     * Verify that the expected interactions took place with the mockImports
     */
    protected void verifyMockImports() {
        verifyNoInteractions(mockImports);
    }
}
