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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.codegen.CodeBuilder;
import tendril.codegen.VisibilityType;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.PrimitiveType;
import tendril.codegen.field.type.Type;
import tendril.codegen.field.value.JValue;
import tendril.codegen.field.value.JValueFactory;
import tendril.test.AbstractUnitTest;

/**
 * Test case for {@link JField} when a value is applied
 */
public class JFieldTestWithValue extends AbstractUnitTest {

    // Mocks to use for testing
    @Mock
    private Type mockType;
    @Mock
    private JValue<Type, ?> mockValue;
    @Mock
    private JValue<Type, ?> mockOtherValue;
    @Mock
    private CodeBuilder mockBuilder;
    @Mock
    private Set<ClassType> mockImports;
    
    // Instance with value to test
    private JField<Type> field;

    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        field = new JField<Type>(VisibilityType.PUBLIC, mockType, "fieldName", mockValue);
    }

    /**
     * Verify that equality is properly determined
     */
    @SuppressWarnings("unlikely-arg-type")
    @Test
    public void testEquals() {
        // Fails
        Assertions.assertFalse(field.equals(new JField<Type>(VisibilityType.PACKAGE_PRIVATE, mockType, "fieldName", mockValue)));
        Assertions.assertFalse(field.equals(new JField<PrimitiveType>(VisibilityType.PUBLIC, PrimitiveType.BOOLEAN, "fieldName", JValueFactory.create(false))));
        Assertions.assertFalse(field.equals(new JField<Type>(VisibilityType.PUBLIC, mockType, "otherFieldName", mockValue)));
        Assertions.assertFalse(field.equals(new JField<Type>(VisibilityType.PUBLIC, mockType, "fieldName", mockOtherValue)));
        Assertions.assertFalse(field.equals(new JField<Type>(VisibilityType.PUBLIC, mockType, "fieldName")));
        Assertions.assertFalse(field.equals("abc123"));
        Assertions.assertFalse(field.equals(null));
        
        // Passes
        Assertions.assertTrue(field.equals(field));
        Assertions.assertTrue(field.equals(new JField<Type>(VisibilityType.PUBLIC, mockType, "fieldName", mockValue)));
    }
    
    /**
     * Verify that the appropriate code is generated
     */
    @Test
    public void testGenerateSelf() {
        when(mockType.getSimpleName()).thenReturn("MockType");
        when(mockValue.generate(mockImports)).thenReturn("value");
        
        field.appendSelf(mockBuilder, mockImports);
        verify(mockType).registerImport(mockImports);
        verify(mockBuilder).append("public MockType fieldName = value;");
        verify(mockType).getSimpleName();
        verify(mockValue).generate(mockImports);
    }
}
