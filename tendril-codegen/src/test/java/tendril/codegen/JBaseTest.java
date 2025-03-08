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
package tendril.codegen;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.codegen.annotation.JAnnotation;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.generics.GenericType;
import tendril.test.AbstractUnitTest;

/**
 * Test case for {@link JBase}
 */
public class JBaseTest extends AbstractUnitTest {

    /**
     * Concrete implementation of the {@link JBase} to allow for testing {@link JBase}
     */
    private class TestBaseElement extends JBase {
        // Counter for how many times appendSelf has been called
        private int timesAppendSelfCalled = 0;
        // Counter for how many times generateSelf has been called
        private int timesGenerateSelfCalled = 0;

        /**
         * CTOR
         * 
         * @param name {@link String}
         */
        protected TestBaseElement(String name) {
            super(name);
        }

        /**
         * Does nothing other than count how many times it has been called
         */
        @Override
        protected void appendSelf(CodeBuilder builder, Set<ClassType> classImports) {
            timesAppendSelfCalled++;
        }

        /**
         * @see tendril.codegen.JBase#generateSelf(java.util.Set)
         */
        @Override
        public String generateSelf(Set<ClassType> classImports) {
            Assertions.assertEquals(mockImports, classImports);
            timesGenerateSelfCalled++;
            return "generateSelf";
        }

        /**
         * Verify that the overridden methods have been called the expected number of times
         * 
         * @param expected int the number of times the appendSelf method is expected to have been called
         * @param expected int the number of times the generateSelf method is expected to have been called
         */
        public void verifyTimesCalled(int expectedAppend, int expectedGenerate) {
            Assertions.assertEquals(expectedAppend, timesAppendSelfCalled);
            Assertions.assertEquals(expectedGenerate, timesGenerateSelfCalled);
        }

    }

    // Mocks to use for testing
    @Mock
    private CodeBuilder mockCodeBuilder;
    @Mock
    private Set<ClassType> mockImports;
    @Mock
    private JAnnotation mockAnnotation1;
    @Mock
    private JAnnotation mockAnnotation2;
    @Mock
    private JAnnotation mockAnnotation3;
    @Mock
    private GenericType mockGeneric1;
    @Mock
    private GenericType mockGeneric2;
    @Mock
    private GenericType mockGeneric3;

    // Instance to use for testing
    private TestBaseElement element;

    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        element = new TestBaseElement("MyElementName");
        Assertions.assertEquals("MyElementName", element.getName());
        Assertions.assertEquals("MyElementName".hashCode(), element.hashCode());
    }

    /**
     * Verify that without any annotation the code generation takes place as expected
     */
    @Test
    public void testGenerateNoAnnotation() {
        Assertions.assertIterableEquals(Collections.emptyList(), element.getAnnotations());

        element.generate(mockCodeBuilder, mockImports);
        element.verifyTimesCalled(1, 0);
    }

    /**
     * Verify that the correct code is generated with a single annotation
     */
    @Test
    public void testSingleAnnotation() {
        when(mockAnnotation1.getType()).thenReturn(new ClassType(Override.class));
        
        element.setFinal(true);
        element.addAnnotation(mockAnnotation1);
        Assertions.assertIterableEquals(Collections.singleton(mockAnnotation1), element.getAnnotations());

        element.generate(mockCodeBuilder, mockImports);
        verify(mockAnnotation1).generate(mockCodeBuilder, mockImports);
        element.verifyTimesCalled(1, 0);

        Assertions.assertFalse(element.hasAnnotation(Deprecated.class));
        verify(mockAnnotation1).getType();
        Assertions.assertFalse(element.hasAnnotation(Test.class));
        verify(mockAnnotation1, times(2)).getType();
        Assertions.assertTrue(element.hasAnnotation(Override.class));
        verify(mockAnnotation1, times(3)).getType();
    }

    /**
     * Verify that the correct code is generates with multiple annotations applied
     */
    @Test
    public void testMultipleAnnotations() {
        when(mockAnnotation1.getType()).thenReturn(new ClassType(Override.class));
        when(mockAnnotation2.getType()).thenReturn(new ClassType(Deprecated.class));
        when(mockAnnotation3.getType()).thenReturn(new ClassType(Override.class));
        
        element.addAnnotation(mockAnnotation1);
        Assertions.assertIterableEquals(Collections.singleton(mockAnnotation1), element.getAnnotations());
        element.addAnnotation(mockAnnotation2);
        Assertions.assertIterableEquals(Arrays.asList(mockAnnotation1, mockAnnotation2), element.getAnnotations());
        element.addAnnotation(mockAnnotation3);
        Assertions.assertIterableEquals(Arrays.asList(mockAnnotation1, mockAnnotation2, mockAnnotation3), element.getAnnotations());

        element.generate(mockCodeBuilder, mockImports);
        verify(mockAnnotation1).generate(mockCodeBuilder, mockImports);
        verify(mockAnnotation2).generate(mockCodeBuilder, mockImports);
        verify(mockAnnotation3).generate(mockCodeBuilder, mockImports);
        element.verifyTimesCalled(1, 0);
        

        Assertions.assertTrue(element.hasAnnotation(Deprecated.class));
        verify(mockAnnotation1).getType();
        verify(mockAnnotation2).getType();
        Assertions.assertFalse(element.hasAnnotation(Test.class));
        verify(mockAnnotation1, times(2)).getType();
        verify(mockAnnotation2, times(2)).getType();
        verify(mockAnnotation3, times(1)).getType();
        Assertions.assertTrue(element.hasAnnotation(Override.class));
        verify(mockAnnotation1, times(3)).getType();
    }

    /**
     * Verify that there are no side-effect to generateSelf
     */
    @Test
    public void testGenerateSelf() {
        element.setFinal(true);
        Assertions.assertEquals("generateSelf", element.generateSelf(mockImports));
        element.verifyTimesCalled(0, 1);
    }

    /**
     * Verify that the final flag is properly applied
     */
    @Test
    public void testFinal() {
        Assertions.assertFalse(element.isFinal());
        Assertions.assertEquals("", element.getFinalKeyword());

        element.setFinal(true);
        Assertions.assertTrue(element.isFinal());
        Assertions.assertEquals("final ", element.getFinalKeyword());

        element.setFinal(false);
        Assertions.assertFalse(element.isFinal());
        Assertions.assertEquals("", element.getFinalKeyword());
    }

    /**
     * Verify that equality is properly determined
     */
    @SuppressWarnings("unlikely-arg-type")
    @Test
    public void testEquals() {
        Assertions.assertFalse(element.equals(null));
        Assertions.assertFalse(element.equals("abc123"));
        Assertions.assertFalse(element.equals(new TestBaseElement("")));
        Assertions.assertFalse(element.equals(new TestBaseElement(null)));
        Assertions.assertFalse(element.equals(new TestBaseElement("MyOtherElementName")));
        Assertions.assertFalse(element.equals(new TestBaseElement("myelementname")));
        Assertions.assertTrue(element.equals(new TestBaseElement("MyElementName")));
    }

    /**
     * Verify that a single generic can be added to the element
     */
    @Test
    public void testSingleGeneric() {
        element.addGeneric(mockGeneric1);

        // Generating will register the generics
        element.generate(mockCodeBuilder, mockImports);
        element.verifyTimesCalled(1, 0);
    }

    /**
     * Verify that multiple generics can be added to the element
     */
    @Test
    public void testMultipleGenerics() {
        element.addGeneric(mockGeneric1);
        element.addGeneric(mockGeneric2);
        element.addGeneric(mockGeneric3);

        // Generating will register the generics
        element.generate(mockCodeBuilder, mockImports);
        element.verifyTimesCalled(1, 0);
    }
}
