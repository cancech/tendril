/*
z * Copyright 2024 Jaroslav Bosak
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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.codegen.field.JVisibleType;
import tendril.codegen.field.VisibileTypeBuilder;
import tendril.codegen.field.type.Type;
import tendril.test.AbstractUnitTest;

/**
 * Test case for {@link VisibileTypeBuilder}
 */
public class VisibileTypeBuilderTest extends AbstractUnitTest {
    
    private class TestVisibilityBuilder extends VisibileTypeBuilder<Type, JVisibleType<Type>, TestVisibilityBuilder> {

        public TestVisibilityBuilder() {
            super("VisibleElement");
            setType(mockType);
        }

        /**
         * @see tendril.codegen.BaseBuilder#validate()
         */
        @Override
        protected void validate() {
            Assertions.fail("Should not be called");
        }

        /**
         * @see tendril.codegen.BaseBuilder#create()
         */
        @Override
        protected JVisibleType<Type> create() {
            Assertions.fail("Should not be called");
            return mockVisibleType;
        }

        /**
         * Verify that the visibility parameter is correct
         * 
         * @param expected {@link VisibilityType} that should be applied to the method
         */
        public void verifyVisibility(VisibilityType expected) {
            Assertions.assertEquals(expected, visibility);
        }

        /**
         * Verify that the static parameter is correct
         * 
         * @param expected boolean that should be applied to the method
         */
        public void verifyStatic(boolean expected) {
            Assertions.assertEquals(expected, isStatic);
        }
        
    }
    
    // Mocks to use for testing
    @Mock
    private Type mockType;
    @Mock
    private JVisibleType<Type> mockVisibleType;
    @Mock
    private VisibilityType mockVisibilityType;

    // Instance to test
    private TestVisibilityBuilder builder;

    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        builder = new TestVisibilityBuilder();

        // Verify the default values
        builder.verifyVisibility(VisibilityType.PACKAGE_PRIVATE);
        builder.verifyStatic(false);
    }

    /**
     * Verify that the visibility can be properly updated
     */
    @Test
    public void changeVisibility() {
        builder.verifyVisibility(VisibilityType.PACKAGE_PRIVATE);
        builder.setVisibility(mockVisibilityType);
        builder.verifyVisibility(mockVisibilityType);

        for (VisibilityType t : VisibilityType.values()) {
            builder.setVisibility(t);
            builder.verifyVisibility(t);
        }
    }
    
    /**
     * Verify that the static flag can be updated
     */
    @Test
    public void changeStatic() {
        builder.setStatic(true);
        builder.verifyStatic(true);
        builder.setStatic(false);
        builder.verifyStatic(false);
    }
}
