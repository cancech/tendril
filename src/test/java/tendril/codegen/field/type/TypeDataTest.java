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

import tendril.dom.type.Type;
import tendril.dom.type.value.ValueElement;

/**
 * Test case for {@link TypeData}
 */
public class TypeDataTest extends SharedTypeDataTest<TestTypeData> {

    // Mocks to use for testing
    @Mock
    private Type mockType;
    @Mock
    private ValueElement<Type, ?> mockValue;
    @Mock
    private Object mockObject;

    /**
     * @see test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        data = new TestTypeData(mockType, mockValue);
    }

    /**
     * Verify that the basic capabilities of the class work as expected
     */
    @Test
    public void testBasicCapabilities() {
        // The basic information is properly retrieved
        verifyDataState(0);

        // registerImport does nothing
        data.registerImport(mockImports);
        verifyDataState(0);

        // asValue has no impact on anything
        Assertions.assertEquals(mockValue, data.asValue(mockObject));
        verifyDataState(1);
    }

    /**
     * Helper to verify the current data state
     * 
     * @param expectedTimesAsValueCalled int the number of times that the asValue method should have been called
     */
    private void verifyDataState(int expectedTimesAsValueCalled) {
        verifyDataState(mockType, "TestTypeData", false);
        data.verifyTimesAsValueCalled(expectedTimesAsValueCalled);
    }

}
