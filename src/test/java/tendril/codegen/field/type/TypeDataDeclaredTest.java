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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.dom.type.core.ClassType;
import tendril.dom.type.value.ValueElement;
import test.assertions.TendrilAssert;

/**
 * Test case for {@link TypeDataDeclared}
 */
public class TypeDataDeclaredTest extends SharedTypeDataTest<TypeDataDeclared> {

    // Mocks to use for testing
    @Mock
    private ClassType mockClassType;
    @Mock
    private Object mockValue;

    /**
     * @see test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        when(mockClassType.getClassName()).thenReturn("mockClassType");
        data = new TypeDataDeclared(mockClassType);
        verify(mockClassType).getClassName();
        
        verifyDataState();
    }

    /**
     * Verify that the correct import is registered
     */
    @Test
    public void testRegisterImports() {
        data.registerImport(mockImports);
        verify(mockImports).add(mockClassType);
        verifyDataState();
    }
    
    /**
     * Verify that the appropriate element is created
     */
    @Test
    public void testAsValue() {
        TendrilAssert.assertValueElement(new ValueElement<>(mockClassType, mockValue), data.asValue(mockValue));
        verifyDataState();
    }
    
    /**
     * Verify that the data state is correct
     */
    private void verifyDataState() {
        verifyDataState(mockClassType, "mockClassType", false);
    }
}
