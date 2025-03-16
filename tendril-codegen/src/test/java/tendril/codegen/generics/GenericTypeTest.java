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
package tendril.codegen.generics;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Set;

import org.apache.commons.lang3.NotImplementedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.Type;
import tendril.codegen.field.value.JValue;
import tendril.test.AbstractUnitTest;

/**
 * Test case for {@link GenericType}
 */
public class GenericTypeTest extends AbstractUnitTest {
    
    /**
     * Concrete implementation of {@link GenericType} to use for testing
     */
    private class TestGenericType extends GenericType {

        /**
         * CTOR
         *
         * @param name {@link String} the name to apply to the type
         * @param type boolean the isTypeOf value to return
         * @param assignable boolean the isAssignableFrom value to return
         * @param value JValue<?,?> the asValue value to return
         */
        TestGenericType(String name) {
            super(name);
        }

        /**
         * @see tendril.codegen.field.type.Type#isTypeOf(java.lang.Object)
         */
        @Override
        public boolean isTypeOf(Object value) {
            throw new NotImplementedException("This is not part of the test, and hence not implemented");
        }

        /**
         * @see tendril.codegen.field.type.Type#isAssignableFrom(tendril.codegen.field.type.Type)
         */
        @Override
        public boolean isAssignableFrom(Type other) {
            throw new NotImplementedException("This is not part of the test, and hence not implemented");
        }

        /**
         * @see tendril.codegen.field.type.Type#asValue(java.lang.Object)
         */
        @Override
        public JValue<?, ?> asValue(Object value) {
            throw new NotImplementedException("This is not part of the test, and hence not implemented");
        }

        /**
         * @see tendril.codegen.field.type.Type#asClassType()
         */
        @Override
        public ClassType asClassType() {
            throw new NotImplementedException("This is not part of the test, and hence not implemented");
        }
        
    }
    
    // Mocks to use for testing
    @Mock
    private ClassType mockClassType;
    @Mock
    private Set<ClassType> mockImports;
    @Mock
    private Object mockObject;
    @Mock
    private JValue<?,?> mockValue;
    @Mock
    private Type mockType;
    
    // The instance to test
    private GenericType gen;

    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        gen = new TestGenericType("TestGenericName");
    }
    
    /**
     * Verify that the appropriate name is provided
     */
    @Test
    public void testName() {
        Assertions.assertEquals("TestGenericName", gen.getSimpleName());
    }
    
    /**
     * Verify that the generic is not considered void
     */
    @Test
    public void testVoid() {
        Assertions.assertFalse(gen.isVoid());
    }

    /**
     * Verify that the appropriate definition is generated.
     */
    @Test
    public void testGenerateDefinition() {
        Assertions.assertEquals("TestGenericName", gen.generateDefinition());
    }
    
    /**
     * Verify that the appropriate application is generated
     */
    @Test
    public void testGenerateApplication() {
        Assertions.assertEquals("TestGenericName", gen.generateApplication());
    }
    
    /**
     * Verify that registering imports does nothing.
     */
    @Test
    public void testRegisterImport() {
        gen.registerImport(mockImports);
        verify(mockImports, never()).add(any());
    }
}
