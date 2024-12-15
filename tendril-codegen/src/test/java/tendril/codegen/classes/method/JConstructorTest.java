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

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.codegen.VisibilityType;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.generics.GenericType;
import tendril.test.AbstractUnitTest;

/**
 * 
 */
public class JConstructorTest extends AbstractUnitTest {
    
    /**
     * Test override to use for testing, to facilitate and simplify the tests that need to be performed.
     */
    private class TestConstructor extends JConstructor {
        
        /**
         * CTOR
         */
        private TestConstructor() {
            super(mockClass, mockCode);
        }
        
        /**
         * Overriding to simplify the tests that need to be covered.
         */
        @Override
        protected String generateParameters(Set<ClassType> classImports) {
            Assertions.assertEquals(mockImports, classImports);
            return "PARAMETERS";
        }
    }
    
    // Mocks to use for testing
    @Mock
    private ClassType mockClass;
    @Mock
    private List<String> mockCode;
    @Mock
    private Set<ClassType> mockImports;
    @Mock
    private GenericType mockGeneric1;
    @Mock
    private GenericType mockGeneric2;
    @Mock
    private GenericType mockGeneric3;
    
    // Instance to test
    private JConstructor ctor;
    
    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        when(mockClass.getSimpleName()).thenReturn("MockClass");
        ctor = new TestConstructor();
        verify(mockClass).getSimpleName();
    }

    /**
     * Verify that the correct code is generated 
     */
    @Test
    public void testGenerateSignatureWithCode() {
        for (VisibilityType type: VisibilityType.values()) {
            ctor.setVisibility(type);
            Assertions.assertEquals(type.getKeyword() + "MockClass(PARAMETERS) {", ctor.generateSignature(mockImports, true));
        }
    }

    /**
     * Verify that the correct code is generated 
     */
    @Test
    public void testSingleGenericGenerateSignatureWithCode() {
        when(mockGeneric1.generateDefinition()).thenReturn("GEN1");
        ctor.addGeneric(mockGeneric1);
        
        for (VisibilityType type: VisibilityType.values()) {
            ctor.setVisibility(type);
            Assertions.assertEquals(type.getKeyword() + "<GEN1> MockClass(PARAMETERS) {", ctor.generateSignature(mockImports, true));
            verify(mockGeneric1, times(type.ordinal() + 1)).generateDefinition();
        }
    }

    /**
     * Verify that the correct code is generated 
     */
    @Test
    public void testMultipleGenericsGenerateSignatureWithCode() {
        when(mockGeneric1.generateDefinition()).thenReturn("GEN1");
        when(mockGeneric2.generateDefinition()).thenReturn("GEN2");
        when(mockGeneric3.generateDefinition()).thenReturn("GEN3");
        ctor.addGeneric(mockGeneric1);
        ctor.addGeneric(mockGeneric2);
        ctor.addGeneric(mockGeneric3);
        
        for (VisibilityType type: VisibilityType.values()) {
            ctor.setVisibility(type);
            Assertions.assertEquals(type.getKeyword() + "<GEN1, GEN2, GEN3> MockClass(PARAMETERS) {", ctor.generateSignature(mockImports, true));
            verify(mockGeneric1, times(type.ordinal() + 1)).generateDefinition();
            verify(mockGeneric2, times(type.ordinal() + 1)).generateDefinition();
            verify(mockGeneric3, times(type.ordinal() + 1)).generateDefinition();
        }
    }
    
    /**
     * Verify that the ctor must have some code to work
     */
    @Test
    public void testCtorWithoutCode() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> ctor.generateSignature(mockImports, false));
    }
    
    /**
     * Verify that the static is properly handled
     */
    @Test
    public void testStatic() {
        Assertions.assertFalse(ctor.isStatic());
        
        Assertions.assertThrows(IllegalArgumentException.class, () -> ctor.setStatic(true));
        Assertions.assertFalse(ctor.isStatic());
        ctor.setStatic(false);
        Assertions.assertFalse(ctor.isStatic());
    }
    
    /**
     * Verify that the final is properly handled
     */
    @Test
    public void testFinal() {
        Assertions.assertFalse(ctor.isFinal());
        
        Assertions.assertThrows(IllegalArgumentException.class, () -> ctor.setFinal(true));
        Assertions.assertFalse(ctor.isFinal());
        ctor.setFinal(false);
        Assertions.assertFalse(ctor.isFinal());
    }
}
