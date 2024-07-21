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

import static org.mockito.Mockito.lenient;

import org.junit.jupiter.api.Assertions;
import org.mockito.Mock;

import tendril.codegen.CodeBuilder;
import tendril.codegen.VisibilityType;
import tendril.codegen.field.type.Type;
import tendril.test.AbstractUnitTest;
import tendril.test.assertions.matchers.MultiLineStringMatcher;

/**
 * Contains the shared elements which are needed for all {@link JMethod} tests
 */
public abstract class SharedJMethodTest extends AbstractUnitTest {
    
    // Mocks to use for testing
    @Mock
    protected VisibilityType mockVisibility;
    @Mock
    protected Type mockReturnType;

    // For tracking the generated code
    protected CodeBuilder builder;
    // Matcher to match the generated code
    protected MultiLineStringMatcher matcher;

    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        builder = new CodeBuilder();
        matcher = new MultiLineStringMatcher();
        
        lenient().when(mockReturnType.getSimpleName()).thenReturn("Return_Type");        
    }

    /**
     * Initialize the method and ensure that its simple values are correct
     * 
     */
    protected void verifyMethodInit(String methodName, JMethod<?> method) {
        Assertions.assertEquals(methodName, method.getName());
        Assertions.assertEquals(mockReturnType, method.getType());
    }

}
