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
package tendril.codegen.classes;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.codegen.VisibilityType;
import tendril.codegen.field.JVisibleType;
import tendril.codegen.field.type.Type;
import tendril.test.AbstractUnitTest;

/**
 * Test case for {@link NestedClassElementBuilder}
 */
public class NestedClassElementBuilderTest extends AbstractUnitTest {
    
    /**
     * Concrete implementation for the purpose of testing
     */
    private class TestClassBuilder extends NestedClassElementBuilder<Type, JVisibleType<Type>, TestClassBuilder> {
        
        private int numOfTimesAddToClassCalled = 0;

        /**
         * CTOR
         * 
         * @param classBuilder {@link ClassBuilder}
         * @param name {@link String}
         */
        public TestClassBuilder(ClassBuilder classBuilder, String name) {
            super(classBuilder, name);
        }

        /**
         * @see tendril.codegen.classes.NestedClassElementBuilder#addToClass(tendril.codegen.classes.ClassBuilder, tendril.codegen.field.JVisibleType)
         */
        @Override
        protected void addToClass(ClassBuilder classBuilder, JVisibleType<Type> toAdd) {
            Assertions.assertEquals(mockClassBuilder, classBuilder);
            Assertions.assertEquals(mockElement, toAdd);
            numOfTimesAddToClassCalled++;
        }
        
        /**
         * @see tendril.codegen.field.TypeBuilder#validate()
         */
        @Override
        protected void validate() {
            // Validation not required
        }

        /**
         * @see tendril.codegen.BaseBuilder#create()
         */
        @Override
        protected JVisibleType<Type> create() {
            return mockElement;
        }
        
        /**
         * Verify that the addToClass method is called the appropriate number of times.
         * 
         * @param expected int how many times addToClass should have been called
         */
        private void verifyTimesAddToClassCalled(int expected) {
            Assertions.assertEquals(expected, numOfTimesAddToClassCalled);
        }
        
    }
    
    // Mocks to use for testing
    @Mock
    private ClassBuilder mockClassBuilder;
    @Mock
    private JVisibleType<Type> mockElement;

    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        // Not required
    }

    /**
     * Verify that creating with a {@link ClassBuilder} adds the element to the builder
     */
    @Test
    public void testWithClassBuilder() {
        TestClassBuilder builder = new TestClassBuilder(mockClassBuilder, "WithClassBuilder");
        Assertions.assertThrows(IllegalStateException.class, () -> builder.build());
        Assertions.assertEquals(mockClassBuilder, builder.finish());
        verify(mockElement).setVisibility(VisibilityType.PACKAGE_PRIVATE);
        verify(mockElement).setStatic(false);
        verify(mockElement).setFinal(false);
        builder.verifyTimesAddToClassCalled(1);
    }

    /**
     * Verify that creating without a {@link ClassBuilder} does not add the element to the builder
     */
    @Test
    public void testWithoutClassBuilder() {
        TestClassBuilder builder = new TestClassBuilder(null, "WithClassBuilder");
        Assertions.assertThrows(IllegalStateException.class, () -> builder.finish());
        Assertions.assertEquals(mockElement, builder.build());
        verify(mockElement).setVisibility(VisibilityType.PACKAGE_PRIVATE);
        verify(mockElement).setStatic(false);
        verify(mockElement).setFinal(false);
        builder.verifyTimesAddToClassCalled(0);
    }
}
