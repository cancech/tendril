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
package tendril.dom.type;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.codegen.field.type.TypeData;
import test.AbstractUnitTest;

/**
 * Test case for {@link NamedTypeElement}
 */
public class NamedTypeElementTest extends AbstractUnitTest {

    // Mocks required for testing
    @Mock
    private TypeData<Type> mockTypeData;

    // The instance to test
    private NamedTypeElement<Type> element;

    /**
     * @see test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        element = new NamedTypeElement<Type>(mockTypeData, "MyName");
    }

    /**
     * Ensure that the getters are doing what is expected of them
     */
    @Test
    public void testGetters() {
        Assertions.assertEquals(mockTypeData, element.getType());
        Assertions.assertEquals("MyName", element.getName());
    }

}
