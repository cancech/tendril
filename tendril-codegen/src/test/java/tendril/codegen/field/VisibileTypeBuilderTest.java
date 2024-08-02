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

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.codegen.VisibilityType;
import tendril.codegen.field.type.Type;
import tendril.test.AbstractUnitTest;

/**
 * Test case for {@link VisibileTypeBuilder}
 */
public class VisibileTypeBuilderTest extends AbstractUnitTest {
    
    /**
     * Concrete implementation of {@link VisibileTypeBuilder} to use for testing
     */
    private class TestVisibileTypeBuilder extends VisibileTypeBuilder<Type, JVisibleType<Type>, TestVisibileTypeBuilder> {

        /**
         * CTOR
         */
        public TestVisibileTypeBuilder() {
            super("MockClassName");
            setType(mockType);
        }

        /**
         * @see tendril.codegen.BaseBuilder#create()
         */
        @Override
        protected JVisibleType<Type> create() {
            Assertions.fail("Should not be called");
            return null;
        }
        
    }
    
    // Mocks to use for testing
    @Mock
    private Type mockType;
    @Mock
    private JVisibleType<Type> mockElement;
    
    // Instance to test
    private TestVisibileTypeBuilder builder;

    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        builder = new TestVisibileTypeBuilder();
    }
    
    /**
     * Verify that the correct defaults are applied
     */
    @Test
    public void testDefaults() {
        builder.applyDetails(mockElement);
        verify(mockElement).setVisibility(VisibilityType.PACKAGE_PRIVATE);
        verify(mockElement).setStatic(false);
        verify(mockElement).setFinal(false);
    }
    
    /**
     * Verify that visibility and static is properly applied to the element
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testApplyDetails() {
        boolean isStatic = false;
        for (VisibilityType type: VisibilityType.values()) {
            builder.setStatic(isStatic);
            builder.setVisibility(type);

            builder.applyDetails(mockElement);
            verify(mockElement).setVisibility(type);
            verify(mockElement).setStatic(isStatic);
            verify(mockElement).setFinal(false);
            verifyAllChecked();
            
            reset(mockElement);
            isStatic = !isStatic;
        }
    }
}
