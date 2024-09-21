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

import tendril.codegen.classes.method.JMethod;
import tendril.codegen.field.type.Type;
import tendril.codegen.field.value.JValue;
import tendril.test.AbstractUnitTest;

/**
 * Test case for {@link MethodBuilder}
 */
public class MethodBuilderTest extends AbstractUnitTest {

    /**
     * Concrete test implementation for testing the builder
     */
    private class TestMethodBuilder extends MethodBuilder<Type> {
        
        /**
         * CTOR
         */
        protected TestMethodBuilder() {
            super(mockClassBuilder, "MethodName");
        }

        /**
         * @see tendril.codegen.BaseBuilder#create()
         */
        @Override
        protected JMethod<Type> create() {
            Assertions.fail("This should not be called over the course of the test");
            return null;
        }
    }

    // Mocks to use for testing
    @Mock
    private ClassBuilder mockClassBuilder;
    @Mock
    private JMethod<Type> mockMethod;
    @Mock
    private JValue<Type, ?> mockValue;

    // Instance to test
    private TestMethodBuilder builder;

    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        builder = new TestMethodBuilder();
    }

    /**
     * Verify that default values are not supported by default
     */
    @Test
    public void testDefaultValueThrowsException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> builder.setDefaultValue(mockValue));
    }
    
    /**
     * Verify that it is added to the class properly
     */
    @Test
    public void testAddToClass() {
        builder.addToClass(mockClassBuilder, mockMethod);
        verify(mockClassBuilder).add(mockMethod);
    }
}
