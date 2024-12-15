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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.Type;
import tendril.codegen.generics.GenericType;

/**
 * Test case for {@link JMethod}
 */
public class JMethodTest extends AbstractMethodTest {

    private static final String GENERATED_START = "Generated_Method_Start ";
    private static final String GENERATED_PARAMS = "PARAMETERS!";
    
    private class TestJMethod extends JMethod<Type> {

        /** Counter for how many times generateSignatureStart has been called */
        private int timesCalled = 0;
        /** The last hasImplementation that was passed to generateSignatureStart */
        private boolean hasImplementation = false;

        /**
         * CTOR
         */
        protected TestJMethod() {
            super(mockReturnType, "method_name", Collections.emptyList());
        }

        /**
         * @see tendril.codegen.classes.method.JMethod#generateSignatureStart(boolean)
         */
        @Override
        protected String generateSignatureStart(boolean hasImplementation) {
            timesCalled++;
            this.hasImplementation = hasImplementation;
            return GENERATED_START;
        }
        
        /**
         * Override to produce a "mock" value, simplifying the testing 
         */
        @Override
        protected String generateParameters(Set<ClassType> classImports) {
            Assertions.assertEquals(mockImports, classImports);
            return GENERATED_PARAMS;
        }
        
        private void verifyCalled(int expectedTime, boolean expectedImplementation) {
            Assertions.assertEquals(expectedTime, timesCalled);
            Assertions.assertEquals(expectedImplementation, hasImplementation);
        }
        
    }
    
    // Mocks to use for testing
    @Mock
    private GenericType mockGeneric1;
    @Mock
    private GenericType mockGeneric2;
    @Mock
    private GenericType mockGeneric3;
    
    // Instance to test
    private TestJMethod method;

    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        super.prepareTest();
        method = new TestJMethod();
        method.setVisibility(mockVisibility);
    }

    /**
     * Verify that the signature end if properly generated
     */
    @Test
    public void testGenerateSignatureEnd() {
        Assertions.assertEquals(" {", method.generateSignatureEnd(true));
        Assertions.assertEquals(";", method.generateSignatureEnd(false));
    }

    /**
     * Verify that the method signature is properly generated when there is no implementation
     */
    @Test
    public void testGenerateSignatureNoImplementation() {
        Assertions.assertEquals(GENERATED_START + SIMPLE_MOCK_RETURN_TYPE + " method_name(" + GENERATED_PARAMS + ");", method.generateSignature(mockImports, false));
        verify(mockReturnType).getSimpleName();
        method.verifyCalled(1, false);
    }

    /**
     * Verify that the method signature is properly generated when there is some implementation
     */
    @Test
    public void testGenerateSignatureWithImplementation() {
        Assertions.assertEquals(GENERATED_START + SIMPLE_MOCK_RETURN_TYPE + " method_name(" + GENERATED_PARAMS + ") {", method.generateSignature(mockImports, true));
        verify(mockReturnType).getSimpleName();
        method.verifyCalled(1, true);
    }

    /**
     * Verify that the method can be defined with generics
     */
    @Test
    public void testGenerateSignatureWithSingleGeneric() {
        when(mockGeneric1.generateDefinition()).thenReturn("GEN1");
        method.addGeneric(mockGeneric1);
        
        Assertions.assertEquals(GENERATED_START + "<GEN1> " + SIMPLE_MOCK_RETURN_TYPE + " method_name(" + GENERATED_PARAMS + ") {", method.generateSignature(mockImports, true));
        verify(mockGeneric1).generateDefinition();
        verify(mockReturnType).getSimpleName();
        method.verifyCalled(1, true);
    }

    /**
     * Verify that the method can be defined with generics
     */
    @Test
    public void testGenerateSignatureWithMultipleGenerics() {
        when(mockGeneric1.generateDefinition()).thenReturn("GEN1");
        when(mockGeneric2.generateDefinition()).thenReturn("GEN2");
        when(mockGeneric3.generateDefinition()).thenReturn("GEN3");
        method.addGeneric(mockGeneric1);
        method.addGeneric(mockGeneric2);
        method.addGeneric(mockGeneric3);
        
        Assertions.assertEquals(GENERATED_START + "<GEN1, GEN2, GEN3> " + SIMPLE_MOCK_RETURN_TYPE + " method_name(" + GENERATED_PARAMS + ") {", method.generateSignature(mockImports, true));
        verify(mockGeneric1).generateDefinition();
        verify(mockGeneric2).generateDefinition();
        verify(mockGeneric3).generateDefinition();
        verify(mockReturnType).getSimpleName();
        method.verifyCalled(1, true);
    }
}
