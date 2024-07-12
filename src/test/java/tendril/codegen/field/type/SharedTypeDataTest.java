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

import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.mockito.Mock;

import tendril.dom.type.Type;
import tendril.dom.type.core.ClassType;
import test.AbstractUnitTest;

/**
 * Shared test features for the various {@link TypeData} tests
 */
public abstract class SharedTypeDataTest<TYPE_DATA extends TypeData<?>> extends AbstractUnitTest {

    // Mocks to use for testing
    @Mock
    protected Set<ClassType> mockImports;

    // Instance to test
    protected TYPE_DATA data;

    /**
     * Helper to verify the full details of the {@link TypeData} instance being tested.
     * 
     * @param expectedType {@link Type} of the data
     * @param expectedName {@link String} of the data
     * @param expectedVoid boolean true if the data is to be void
     */
    protected void verifyDataState(Type expectedType, String expectedName, boolean expectedVoid) {
        Assertions.assertEquals(expectedType, data.getDataType());
        Assertions.assertEquals(expectedName, data.getSimpleName());
        Assertions.assertEquals(expectedVoid, data.isVoid());
        verifyAllChecked();
    }
}
