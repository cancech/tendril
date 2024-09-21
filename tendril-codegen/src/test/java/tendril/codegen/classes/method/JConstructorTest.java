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

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.codegen.VisibilityType;
import tendril.codegen.field.type.ClassType;
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
            
            String expected = "";
            if (type != VisibilityType.PACKAGE_PRIVATE)
                expected = type.toString() + " ";
            Assertions.assertEquals(expected + "MockClass(PARAMETERS) {", ctor.generateSignature(mockImports, true));
        }
    }
    
    /**
     * Verify that the ctor must have some code to work
     */
    @Test
    public void testCtorWithoutCode() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> ctor.generateSignature(mockImports, false));
    }
}
