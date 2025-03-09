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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.codegen.annotation.JAnnotation;
import tendril.codegen.generics.GenericType;
import tendril.test.AbstractUnitTest;

/**
 * Test case for {@link BaseBuilder}
 */
public class BaseBuilderTest extends AbstractUnitTest {
    
    /**
     * Concrete implementation of {@link BaseBuilder} to use for testing
     */
    private class TestBaseBuilder extends BaseBuilder<JBase, TestBaseBuilder> {
        // Counters for how many times the abstract methods were called
        private int timesValidateCalled = 0;
        private int timesCreateCalled = 0;
        
        /**
         * CTOR
         */
        public TestBaseBuilder() {
            super("TestBase");
        }

        /**
         * @see tendril.codegen.BaseBuilder#validate()
         */
        @Override
        protected void validate() {
            timesValidateCalled++;
        }

        /**
         * @see tendril.codegen.BaseBuilder#create()
         */
        @Override
        protected JBase create() {
            timesCreateCalled++;
            return mockElement;
        }
        
        private void verifyInteractions(int timesValidateExpected, int timesCreateExpected) {
            Assertions.assertEquals(timesValidateExpected, timesValidateCalled);
            Assertions.assertEquals(timesCreateExpected, timesCreateCalled);
        }
        
    }
    
    // Mocks to use for testing
    @Mock
    private JBase mockElement;
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
    private TestBaseBuilder builder;
    
    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        builder = new TestBaseBuilder();
    }
    
    /**
     * Verify that the proper values are applied by default
     */
    @Test
    public void testDefault() {
        Assertions.assertEquals(mockElement, builder.build());
        verify(mockElement).setFinal(false);
        builder.verifyInteractions(1, 1);
    }
    
    /**
     * Verify that the proper values are applied
     */
    @Test
    public void testFinalWithAnnotationsWithGenerics() {
        builder.setFinal(true);
        builder.addAnnotation(mockAnnotation1);
        builder.addAnnotation(mockAnnotation2);
        builder.addAnnotation(mockAnnotation3);
        builder.addGeneric(mockGeneric1);
        builder.addGeneric(mockGeneric2);
        builder.addGeneric(mockGeneric3);
        
        Assertions.assertEquals(mockElement, builder.build());
        verify(mockElement).setFinal(true);
        verify(mockElement).add(mockAnnotation1);
        verify(mockElement).add(mockAnnotation2);
        verify(mockElement).add(mockAnnotation3);
        verify(mockElement).addGeneric(mockGeneric1);
        verify(mockElement).addGeneric(mockGeneric2);
        verify(mockElement).addGeneric(mockGeneric3);
        builder.verifyInteractions(1, 1);
    }
    
    /**
     * Verify that the proper values are applied
     */
    @Test
    public void testNotFinalSingleAnnotationSingleGeneric() {
        builder.setFinal(true);
        builder.addAnnotation(mockAnnotation2);
        builder.addGeneric(mockGeneric1);
        
        Assertions.assertEquals(mockElement, builder.build());
        verify(mockElement).setFinal(true);
        verify(mockElement).add(mockAnnotation2);
        verify(mockElement).addGeneric(mockGeneric1);
        builder.verifyInteractions(1, 1);
    }

}
