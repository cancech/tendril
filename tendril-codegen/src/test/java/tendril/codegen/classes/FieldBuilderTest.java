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
package tendril.codegen.classes;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.codegen.VisibilityType;
import tendril.codegen.field.JField;
import tendril.codegen.field.type.Type;
import tendril.codegen.field.value.JValue;
import tendril.test.AbstractUnitTest;

/**
 * Test case for the {@link FieldBuilder}
 */
public class FieldBuilderTest extends AbstractUnitTest {
    
    // Mocks to use for testing
    @Mock
    private ClassBuilder mockClassBuilder;
    @Mock
    private JField<Type> mockField;
    @Mock
    private JValue<Type, ?> mockValue;
    @Mock
    private Type mockType;
    
    // Instance to test
    private FieldBuilder<Type> builder;

    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        builder = new FieldBuilder<>("field");
    }

    /**
     * Verify that the value can be set properly
     */
    @Test
    public void testApplyDefaultValue() {
        // By default value is null
        builder.applyDetails(mockField);
        verify(mockField).setValue(null);
        verify(mockField).setFinal(false);
        verify(mockField).setStatic(false);
        verify(mockField).setVisibility(VisibilityType.PACKAGE_PRIVATE);
    }

    /**
     * Verify that the value can be set properly
     */
    @Test
    public void testApplySpecifiedValue() {
        // Apply some value
        builder.setValue(mockValue);
        builder.applyDetails(mockField);
        verify(mockField).setValue(mockValue);
        verify(mockField).setFinal(false);
        verify(mockField).setStatic(false);
        verify(mockField).setVisibility(VisibilityType.PACKAGE_PRIVATE);
    }
    
    /**
     * Verify that it creates the appropriate field
     */
    @Test
    public void testCreateField() {
        builder.setType(mockType);
        JField<Type> field = builder.create();
        Assertions.assertEquals(mockType, field.getType());
        Assertions.assertEquals("field", field.getName());
    }
    
    /**
     * Verify that the field is appropriately added to the class
     */
    @Test
    public void testAddToClass() {
        builder.addToClass(mockClassBuilder, mockField);
        verify(mockClassBuilder).add(mockField);
    }
}
