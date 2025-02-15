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

import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.codegen.CodeGenerationException;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.Type;
import tendril.test.AbstractUnitTest;

/**
 * Test case for {@link AnonymousMethod}
 */
public class AnonymousMethodTest extends AbstractUnitTest {

    // Mocks to be used for testing
    @Mock
    private Type mockReturnType;
    @Mock
    private Set<ClassType> mockImports;
    
    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        // Not required
    }

    /**
     * Verify that attempting to generate will throw an exception
     */
    @Test
    public void testGenerateThrowsException() {
        AnonymousMethod<Type> method = new AnonymousMethod<Type>(mockReturnType, "anonymousMethod");
        Assertions.assertThrows(CodeGenerationException.class, () -> method.generateSignatureStart(true));
        Assertions.assertThrows(CodeGenerationException.class, () -> method.generateSignatureStart(false));
        Assertions.assertThrows(CodeGenerationException.class, () -> method.generateSelf(mockImports));
    }
}
