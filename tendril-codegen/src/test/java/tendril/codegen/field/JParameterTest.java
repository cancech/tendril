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

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.codegen.CodeBuilder;
import tendril.codegen.annotation.JAnnotation;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.Type;
import tendril.test.AbstractUnitTest;

/**
 * Test case for {@link JParameter}
 */
public class JParameterTest extends AbstractUnitTest {

    // Mocks to use for testing
    @Mock
    private Type mockType;
    @Mock
    private CodeBuilder mockBuilder;
    @Mock
    private Set<ClassType> mockImports;
    @Mock
    private JAnnotation mockAnnotation1;
    @Mock
    private JAnnotation mockAnnotation2;
    @Mock
    private JAnnotation mockAnnotation3;
    
    // Instance to test
    private JParameter<Type> param;
    
    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        when(mockType.getSimpleName()).thenReturn("MockType");
        lenient().when(mockAnnotation1.generateSelf(mockImports)).thenReturn("@MockAnnotation1");
        lenient().when(mockAnnotation2.generateSelf(mockImports)).thenReturn("@MockAnnotation2");
        lenient().when(mockAnnotation3.generateSelf(mockImports)).thenReturn("@MockAnnotation3");
        param = new JParameter<Type>(mockType, "parameterName");
    }
    
    /**
     * Verify that the appropriate code is generated for the parameter when no annotations are present
     */
    @Test
    public void testGenerateSelfNoAnnotations() {
        param.generate(mockBuilder, mockImports);
        verify(mockType).registerImport(mockImports);
        verify(mockType).getSimpleName();
        verify(mockBuilder).append("MockType parameterName");
    }
    
    /**
     * Verify that the appropriate code is generated for the parameter when a single annotation is applied
     */
    @Test
    public void testGenerateSelfSingleAnnotation() {
        param.addAnnotation(mockAnnotation1);
        param.generate(mockBuilder, mockImports);
        verify(mockType).registerImport(mockImports);
        verify(mockType).getSimpleName();
        verify(mockAnnotation1).generateSelf(mockImports);
        verify(mockBuilder).append("@MockAnnotation1 MockType parameterName");
    }
    
    /**
     * Verify that the appropriate code is generated for the parameter when multiple annotations are applied
     */
    @Test
    public void testGenerateSelfMultipleAnnotations() {
        param.addAnnotation(mockAnnotation1);
        param.addAnnotation(mockAnnotation2);
        param.addAnnotation(mockAnnotation3);
        param.generate(mockBuilder, mockImports);
        verify(mockType).registerImport(mockImports);
        verify(mockType).getSimpleName();
        verify(mockAnnotation1).generateSelf(mockImports);
        verify(mockAnnotation2).generateSelf(mockImports);
        verify(mockAnnotation3).generateSelf(mockImports);
        verify(mockBuilder).append("@MockAnnotation1 @MockAnnotation2 @MockAnnotation3 MockType parameterName");
    }
}
