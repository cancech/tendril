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
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.Type;
import tendril.test.AbstractUnitTest;

/**
 * Test case for {@link NamedType}
 */
public class NamedTypeTest extends AbstractUnitTest {
    
    /**
     * Concrete implementation of {@link NamedType} to employ for testing
     */
    private class TestNamedType extends NamedType<Type> {

        /**
         * CTOR
         * 
         * @param type {@link Type}
         * @param name {@link String}
         */
        public TestNamedType(Type type, String name) {
            super(type, name);
        }

        /**
         * @see tendril.codegen.JBase#generateSelf(tendril.codegen.CodeBuilder, java.util.Set)
         */
        @Override
        protected void generateSelf(CodeBuilder builder, Set<ClassType> classImports) {
            Assertions.fail("generateSelf should not be called, no need to test it here...");
        }
        
    }
    

    // Mocks required for testing
    @Mock
    private Type mockTypeData;
    @Mock
    private Type mockOTherTypeData;

    // The instance to test
    private TestNamedType element;

    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        element = new TestNamedType(mockTypeData, "MyName");
    }

    /**
     * Ensure that the getters are doing what is expected of them
     */
    @Test
    public void testGetters() {
        Assertions.assertEquals(mockTypeData, element.getType());
        Assertions.assertEquals("MyName", element.getName());
    }
    
    /**
     * Verify that the hashcode is proper calculated
     */
    @Test
    public void testHashCode() {
        Assertions.assertEquals(mockTypeData.hashCode() + "MyName".hashCode(), element.hashCode());
    }

    /**
     * Verify that equality is properly determined
     */
    @Test
    public void testEquals() {
        Assertions.assertTrue(element.equals(new TestNamedType(mockTypeData, "MyName")));
        Assertions.assertFalse(element.equals(new TestNamedType(mockTypeData, "OtherName")));
        Assertions.assertFalse(element.equals(new TestNamedType(mockOTherTypeData, "MyName")));
        Assertions.assertFalse(element.equals((Object)mockTypeData));
    }
}
