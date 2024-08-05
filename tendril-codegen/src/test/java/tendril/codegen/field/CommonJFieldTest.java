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

import org.mockito.Mock;

import tendril.codegen.CodeBuilder;
import tendril.codegen.VisibilityType;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.Type;
import tendril.codegen.field.value.JValue;
import tendril.test.AbstractUnitTest;

/**
 * Common functionality for the purpose of testing {@link JField}
 */
public abstract class CommonJFieldTest extends AbstractUnitTest {

    // Mocks to use for testing
    @Mock
    protected Type mockType;
    @Mock
    protected JValue<Type, ?> mockOtherValue;
    @Mock
    protected CodeBuilder mockBuilder;
    @Mock
    protected Set<ClassType> mockImports;

    // Instance with value to test
    protected JField<Type> field;

    /**
     * Create a field with the specified characteristics
     * 
     * @param visibility {@link VisibilityType}
     * @param type       {@link Type}
     * @param name       {@link String}
     * @return {@link JField}
     */
    protected JField<Type> create(VisibilityType visibility, Type type, String name) {
        return create(visibility, type, name, null);
    }

    /**
     * Create a field with the specified characteristics
     * 
     * @param <DATA_TYPE> extending {@link Type} indicating what the field contains
     * @param visibility  {@link VisibilityType}
     * @param type        {@link Type}
     * @param name        {@link String}
     * @param value       {@link JValue}
     * @return {@link JField}
     */
    protected <DATA_TYPE extends Type> JField<DATA_TYPE> create(VisibilityType visibility, DATA_TYPE type, String name, JValue<DATA_TYPE, ?> value) {
        JField<DATA_TYPE> field = new JField<>(type, name);
        field.setVisibility(visibility);

        if (value != null)
            field.setValue(value);

        return field;
    }
}
