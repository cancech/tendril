/*
 * Copyright 2025 Jaroslav Bosak
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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.codegen.field.type.ClassType;
import tendril.test.AbstractUnitTest;

/**
 * Test case for {@link CompoundGeneric}
 */
public class CompoundGenericTest extends AbstractUnitTest {
    
    /**
     * Concrete implementation of {@link CompoundGeneric} to use for testing
     */
    private class TestCompoundGeneric extends CompoundGeneric {

        /**
         * CTOR
         * 
         * @param name {@link String} to apply to the generic
         */
        TestCompoundGeneric(String name) {
            this(name, parentList);
        }

        /**
         * CTOR
         * 
         * @param name {@link String} to apply to the generic
         * @param parents {@link List} of {@link ClassType}s representing the parents to employs for the generic
         */
        TestCompoundGeneric(String name, List<ClassType> parents) {
            super(name, parents);
        }

        /**
         * @see tendril.codegen.generics.CompoundGeneric#getKeyword()
         */
        @Override
        protected String getKeyword() {
            return "KEYWORD ";
        }
        
    }
    
    // Mocks to use for testing
    @Mock
    private ClassType mockParent1;
    @Mock
    private ClassType mockParent2;
    @Mock
    private ClassType mockParent3;
    @Mock
    private Set<ClassType> mockImports;
    
    // Instance to test
    private List<ClassType> parentList;
    private CompoundGeneric gen;

    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        parentList = Arrays.asList(mockParent1, mockParent2, mockParent3);
        gen = new TestCompoundGeneric("testGeneric");
    }
    
    /**
     * Verify that the imports are properly registered
     */
    @Test
    public void testRegisterImports() {
        gen.registerImport(mockImports);
        verify(mockImports).addAll(parentList);
    }
    
    /**
     * Verify that the definition is properly generated
     */
    @Test
    public void testGenerateDefinition() {
        when(mockParent1.getSimpleName()).thenReturn("mockParent1");
        when(mockParent2.getSimpleName()).thenReturn("mockParent2");
        when(mockParent3.getSimpleName()).thenReturn("mockParent3");
        Assertions.assertEquals("testGeneric KEYWORD mockParent1 & mockParent2 & mockParent3", gen.generateDefinition());
        verify(mockParent1).getSimpleName();
        verify(mockParent2).getSimpleName();
        verify(mockParent3).getSimpleName();
    }

    /**
     * Verify that the generic is properly determining equality
     */
    @SuppressWarnings("unlikely-arg-type")
    @Test
    public void testEquals() {
        Assertions.assertTrue(gen.equals(new TestCompoundGeneric("testGeneric", Arrays.asList(mockParent1, mockParent2, mockParent3))));

        Assertions.assertFalse(gen.equals(new TestCompoundGeneric("testGeneric123", Arrays.asList(mockParent1, mockParent2, mockParent3))));
        Assertions.assertFalse(gen.equals(new TestCompoundGeneric("testGeneric", Arrays.asList(mockParent1, mockParent3))));
        Assertions.assertFalse(gen.equals(Integer.valueOf(123)));
    }

}
