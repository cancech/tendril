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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.codegen.VisibilityType;
import tendril.codegen.field.type.PrimitiveType;
import tendril.codegen.field.type.Type;
import tendril.codegen.field.value.JValue;
import tendril.codegen.field.value.JValueFactory;

/**
 * Test case for {@link JField} when a value is applied
 */
public class JFieldTestWithValue extends CommonJFieldTest {

    // Mocks to use for testing
    @Mock
    private JValue<Type, ?> mockValue;

    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        field = create(VisibilityType.PUBLIC, mockType, "fieldName", mockValue);
        Assertions.assertEquals(mockValue, field.getValue());
    }

    /**
     * Verify that equality is properly determined
     */
    @SuppressWarnings("unlikely-arg-type")
    @Test
    public void testEquals() {
        // Fails
        Assertions.assertFalse(field.equals(create(VisibilityType.PUBLIC, PrimitiveType.BOOLEAN, "fieldName", JValueFactory.create(false))));
        Assertions.assertFalse(field.equals(create(VisibilityType.PUBLIC, mockType, "otherFieldName", mockValue)));
        Assertions.assertFalse(field.equals(create(VisibilityType.PUBLIC, mockType, "fieldName", mockOtherValue)));
        Assertions.assertFalse(field.equals(create(VisibilityType.PUBLIC, mockType, "fieldName")));
        Assertions.assertFalse(field.equals("abc123"));
        Assertions.assertFalse(field.equals(null));
        
        // Passes
        Assertions.assertTrue(field.equals(field));
        Assertions.assertTrue(field.equals(create(VisibilityType.PUBLIC, mockType, "fieldName", mockValue)));
        Assertions.assertTrue(field.equals(create(VisibilityType.PACKAGE_PRIVATE, mockType, "fieldName", mockValue)));
    }
    
    /**
     * Verify that the appropriate code is generated
     */
    @Test
    public void testGenerateSelf_PublicNotStaticNotFinal() {
        when(mockType.getSimpleName()).thenReturn("MockType");
        when(mockValue.generate(mockImports)).thenReturn("value");
        
        field.appendSelf(mockBuilder, mockImports);
        verify(mockType).registerImport(mockImports);
        verify(mockBuilder).append("public MockType fieldName = value;");
        verify(mockType).getSimpleName();
        verify(mockValue).generate(mockImports);
    }
    
    /**
     * Verify that the appropriate code is generated
     */
    @Test
    public void testGenerateSelf_PrivateStaticFinal() {
        field = create(VisibilityType.PRIVATE, mockType, "fieldName", mockValue);
        field.setStatic(true);
        field.setFinal(true);
        
        when(mockType.getSimpleName()).thenReturn("MockType");
        when(mockValue.generate(mockImports)).thenReturn("value");
        
        field.appendSelf(mockBuilder, mockImports);
        verify(mockType).registerImport(mockImports);
        verify(mockBuilder).append("private static final MockType fieldName = value;");
        verify(mockType).getSimpleName();
        verify(mockValue).generate(mockImports);
    }
    
    /**
     * Verify that the field can have generics.
     */
    @Test
    public void testSingleGeneric() {
        when(mockType.getSimpleName()).thenReturn("MockType");
        when(mockGeneric1.generateApplication()).thenReturn("GEN1");
        when(mockValue.generate(mockImports)).thenReturn("value");
        
        field.addGeneric(mockGeneric1);
        
        field.appendSelf(mockBuilder, mockImports);
        verify(mockType).registerImport(mockImports);
        verify(mockType).getSimpleName();
        verify(mockGeneric1).generateApplication();
        verify(mockBuilder).append("public MockType<GEN1> fieldName = value;");
    }
    
    /**
     * Verify that the field can have generics.
     */
    @Test
    public void testMultipleGenerics() {
        when(mockType.getSimpleName()).thenReturn("MockType");
        when(mockGeneric1.generateApplication()).thenReturn("GEN1");
        when(mockGeneric2.generateApplication()).thenReturn("GEN2");
        when(mockGeneric3.generateApplication()).thenReturn("GEN3");
        when(mockValue.generate(mockImports)).thenReturn("value");
        
        field.addGeneric(mockGeneric1);
        field.addGeneric(mockGeneric2);
        field.addGeneric(mockGeneric3);
        
        field.appendSelf(mockBuilder, mockImports);
        verify(mockType).registerImport(mockImports);
        verify(mockType).getSimpleName();
        verify(mockGeneric1).generateApplication();
        verify(mockGeneric2).generateApplication();
        verify(mockGeneric3).generateApplication();
        verify(mockBuilder).append("public MockType<GEN1, GEN2, GEN3> fieldName = value;");
    }
    
    /**
     * Verify that the field can be of type generic
     */
    @Test
    public void testGenerateSelf_GenericType() {
        field = create(VisibilityType.PACKAGE_PRIVATE, mockGeneric1, "fieldName", mockValue);
        field.setFinal(true);
        
        when(mockGeneric1.getSimpleName()).thenReturn("GEN1");
        when(mockValue.generate(mockImports)).thenReturn("value");
        
        field.appendSelf(mockBuilder, mockImports);
        verify(mockGeneric1).registerImport(mockImports);
        verify(mockBuilder).append("final GEN1 fieldName = value;");
        verify(mockGeneric1).getSimpleName();
        verify(mockValue).generate(mockImports);
    }
}
