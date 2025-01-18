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

import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.codegen.CodeBuilder;
import tendril.codegen.VisibilityType;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.Type;
import tendril.test.AbstractUnitTest;

/**
 * Test case for {@link JVisibleType}
 */
public class JVisibleTypeTest extends AbstractUnitTest {
    
    /**
     * Concrete {@link JVisibleType} implementation to use for testing
     */
    private class TestJVisibleType extends JVisibleType<Type> {

        /**
         * CTOR
         */
        public TestJVisibleType() {
            super(mockType, "VisibleElement");
        }

        /**
         * @see tendril.codegen.JBase#appendSelf(tendril.codegen.CodeBuilder, java.util.Set)
         */
        @Override
        protected void appendSelf(CodeBuilder builder, Set<ClassType> classImports) {
            Assertions.fail("Should not be called");
        }

        /**
         * @see tendril.codegen.JBase#generateSelf(java.util.Set)
         */
        @Override
        public String generateSelf(Set<ClassType> classImports) {
            Assertions.fail("Should not be called");
            return null;
        }
        
    }
    
    // Mocks to use for testing
    @Mock
    private Type mockType;
    
    // Instance to test
    private JVisibleType<Type> element;
    
    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        element = new TestJVisibleType();
    }

    /**
     * Verify that visibility can be properly set
     */
    @Test
    public void testVisibility() {
        Assertions.assertEquals(VisibilityType.PACKAGE_PRIVATE, element.getVisibility());
        
        for (VisibilityType t: VisibilityType.values()) {
            element.setVisibility(t);
            Assertions.assertEquals(t, element.getVisibility());
        }
    }
    
    /**
     * Verify that the static flag can be properly set
     */
    @Test
    public void testStatic() {
        Assertions.assertFalse(element.isStatic());
        Assertions.assertEquals("", element.getStaticKeyword());
        
        element.setStatic(true);
        Assertions.assertTrue(element.isStatic());
        Assertions.assertEquals("static ", element.getStaticKeyword());
        
        element.setStatic(false);
        Assertions.assertFalse(element.isStatic());
        Assertions.assertEquals("", element.getStaticKeyword());
    }
}
