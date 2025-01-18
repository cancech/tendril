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

import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.mockito.Mock;

import tendril.codegen.VisibilityType;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.Type;
import tendril.test.AbstractUnitTest;

/**
 * Shared abstract method with common elements to facilitate the testing of Method(like) representations
 */
public class AbstractMethodTest extends AbstractUnitTest {
    
    protected static final String SIMPLE_MOCK_RETURN_TYPE = "Return_Type";

    // Mocks to use for testing
    @Mock
    protected VisibilityType mockVisibility;
    @Mock
    protected Type mockReturnType;
    @Mock
    protected Set<ClassType> mockImports;

    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        lenient().when(mockVisibility.getKeyword()).thenReturn("mockVisibility ");
        lenient().when(mockReturnType.getSimpleName()).thenReturn(SIMPLE_MOCK_RETURN_TYPE);    
    }

    /**
     * Initialize the method and ensure that its simple values are correct
     * 
     */
    protected void verifyMethodInit(String methodName, JAbstractMethodElement<?> method) {
        Assertions.assertEquals(methodName, method.getName());
        Assertions.assertEquals(mockReturnType, method.getType());
    }

}
