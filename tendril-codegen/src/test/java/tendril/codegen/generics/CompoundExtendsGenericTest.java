/*
 * Copyright 2025 Jaroslav Bosak
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
package tendril.codegen.generics;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.codegen.field.type.ClassType;
import tendril.test.AbstractUnitTest;

/**
 * Test case for {@link CompoundExtendsGeneric}
 */
public class CompoundExtendsGenericTest extends AbstractUnitTest {
    
    // Mocks to use for testing
    @Mock
    private List<ClassType> mockParents;
    @Mock
    private List<ClassType> mockOtherParents;
    
    // Instance to test
    private CompoundExtendsGeneric gen;

    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        gen = new CompoundExtendsGeneric("GenericName", mockParents);
    }
    
    /**
     * Verify that the correct keyword is produced
     */
    @Test
    public void testKeyword() {
        Assertions.assertEquals("extends ", gen.getKeyword());
    }

    /**
     * Verify that the generic is properly determining equality
     */
    @SuppressWarnings("unlikely-arg-type")
    @Test
    public void testEquals() {
        Assertions.assertTrue(gen.equals(new CompoundExtendsGeneric("GenericName", mockParents)));

        Assertions.assertFalse(gen.equals(new CompoundExtendsGeneric("GenericNameAbc123", mockParents)));
        Assertions.assertFalse(gen.equals(new CompoundExtendsGeneric("GenericName", mockOtherParents)));
        Assertions.assertFalse(gen.equals(Integer.valueOf(123)));
    }
}
