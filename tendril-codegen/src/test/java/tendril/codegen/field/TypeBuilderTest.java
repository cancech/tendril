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
package tendril.codegen.field;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.codegen.field.type.Type;
import tendril.test.AbstractUnitTest;

/**
 * Test case for {@link TypeBuilder}
 */
public class TypeBuilderTest extends AbstractUnitTest {
    
    /**
     * Concrete implementation to use for testing purposes
     */
    private class TestTypeBuilder extends TypeBuilder<Type, JType<Type>, TestTypeBuilder> {

        /**
         * CTOR
         */
        public TestTypeBuilder() {
            super("TestTypeBuilder");
        }

        /**
         * @see tendril.codegen.BaseBuilder#create()
         */
        @Override
        protected JType<Type> create() {
            Assertions.fail("Should not be called");
            return null;
        }
        
        /**
         * Verify that the correct type is applied
         * 
         * @param expected {@link Type} that should be applied
         */
        private void verifyType(Type expected) {
            Assertions.assertEquals(expected, type);
        }
        
    }
    
    // Mocks to use for testing
    @Mock
    private Type mockType1;
    @Mock
    private Type mockType2;

    // Instance to test
    private TestTypeBuilder builder;
    
    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        builder = new TestTypeBuilder();
    }
    
    /**
     * Verify that the type can be properly applied
     */
    @Test
    public void testTypeApplied() {
        // Initially null
        builder.verifyType(null);
        Assertions.assertThrows(IllegalArgumentException.class, () -> builder.validate());
        
        // Make sure that we can update the type
        builder.setType(mockType1);
        builder.verifyType(mockType1);
        builder.validate();
        builder.setType(mockType2);
        builder.verifyType(mockType2);
        builder.validate();
        
        // Make sure that it can be reset
        builder.setType(null);
        builder.verifyType(null);
        Assertions.assertThrows(IllegalArgumentException.class, () -> builder.validate());
    }

}
