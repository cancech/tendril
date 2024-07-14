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

import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.codegen.annotation.JAnnotation;
import tendril.codegen.field.type.ClassType;
import test.AbstractUnitTest;

/**
 * Test case for {@link JBase}
 */
public class JBaseTest extends AbstractUnitTest {

    /**
     * Concrete implementation of the {@link JBase} to allow for testing {@link JBase}
     */
    private static class TestBaseElement extends JBase {
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
        protected void generateSelf(CodeBuilder builder, Set<ClassType> classImports) {
            timesGenerateSelfCalled++;
        }

        /**
         * Verify that the generateSelf method has been called the expected number of times
         * 
         * @param expected int the number of times the method is expected to have been called
         */
        public void verifyTimesGenerateSelfCalled(int expected) {
            Assertions.assertEquals(expected, timesGenerateSelfCalled);
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

    // Instance to use for testing
    private TestBaseElement element;

    /**
     * @see test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        element = new TestBaseElement("MyElementName");
        Assertions.assertEquals("MyElementName", element.getName());
    }

    /**
     * Verify that without any annotation the code generation takes place as expected
     */
    @Test
    public void testGenerateNoAnnotation() {
        Assertions.assertIterableEquals(Collections.emptyList(), element.getAnnotations());
        
        element.generate(mockCodeBuilder, mockImports);
        element.verifyTimesGenerateSelfCalled(1);
    }

    /**
     * Verify that the correct code is generated with a single annotation
     */
    @Test
    public void testSingleAnnotation() {
        element.annotate(mockAnnotation1);
        Assertions.assertIterableEquals(Collections.singleton(mockAnnotation1), element.getAnnotations());
        
        element.generate(mockCodeBuilder, mockImports);
        verify(mockAnnotation1).generate(mockCodeBuilder, mockImports);
        element.verifyTimesGenerateSelfCalled(1);
    }

    /**
     * Verify that the correct code is generates with multiple annotations applied
     */
    @Test
    public void testMultipleAnnotations() {
        element.annotate(mockAnnotation1);
        Assertions.assertIterableEquals(Collections.singleton(mockAnnotation1), element.getAnnotations());
        element.annotate(mockAnnotation2);
        Assertions.assertIterableEquals(Arrays.asList(mockAnnotation1, mockAnnotation2), element.getAnnotations());
        element.annotate(mockAnnotation3);
        Assertions.assertIterableEquals(Arrays.asList(mockAnnotation1, mockAnnotation2, mockAnnotation3), element.getAnnotations());
        
        element.generate(mockCodeBuilder, mockImports);
        verify(mockAnnotation1).generate(mockCodeBuilder, mockImports);
        verify(mockAnnotation2).generate(mockCodeBuilder, mockImports);
        verify(mockAnnotation3).generate(mockCodeBuilder, mockImports);
        element.verifyTimesGenerateSelfCalled(1);
    }
}
