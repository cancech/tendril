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
package tendril.codegen.field.type;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.dom.type.core.VoidType;

/**
 * Test case for {@link TypeDataVoid}
 */
public class TypeDataVoidTest extends SharedTypeDataTest<TypeDataVoid> {

    // Mocks to use for testing
    @Mock
    private Object mockObject;
    
    /**
     * @see test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        data = new TypeDataVoid();
        verifyDataState(VoidType.INSTANCE, "void", true);
    }

    /**
     * Verify that attempting to generate an asValue throws an exception
     */
    @Test
    public void testAsValueThrowsException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> data.asValue(mockObject));
    }
}
