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
package tendril.codegen.field;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.codegen.CodeBuilder;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.Type;
import tendril.test.AbstractUnitTest;

/**
 * Test case for {@link ParameterType}
 */
public class ParameterTypeTest extends AbstractUnitTest {

    // Mocks to use for testing
    @Mock
    private Type mockType;
    @Mock
    private CodeBuilder mockBuilder;
    @Mock
    private Set<ClassType> mockImports;
    
    // Instance to test
    private ParameterType<Type> param;
    
    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        param = new ParameterType<Type>(mockType, "parameterName");
    }
    
    /**
     * Verify that the appropriate code is generated for the parameter
     */
    @Test
    public void testGenerateSelf() {
        when(mockType.getSimpleName()).thenReturn("MockType");
        param.generateSelf(mockBuilder, mockImports);
        verify(mockType).registerImport(mockImports);
        verify(mockType).getSimpleName();
        verify(mockBuilder).append("MockType parameterName");
    }

}
