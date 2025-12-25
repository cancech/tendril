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

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import tendril.codegen.VisibilityType;
import tendril.codegen.field.type.PrimitiveType;
import tendril.codegen.field.value.JValueFactory;

/**
 * Test case for {@link JField} when a value is not applied
 */
public class JFieldTestWithoutValue extends CommonJFieldTest {

    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        field = create(VisibilityType.PACKAGE_PRIVATE, mockType, "fieldName");
        Assertions.assertNull(field.getValue());
        Assertions.assertEquals("", field.getCustomInitialization());
    }

    /**
     * Verify that equality is properly determined
     */
    @SuppressWarnings("unlikely-arg-type")
    @Test
    public void testEquals() {
        // Fails
        Assertions.assertFalse(field.equals(create(VisibilityType.PACKAGE_PRIVATE, PrimitiveType.BOOLEAN, "fieldName", JValueFactory.create(false))));
        Assertions.assertFalse(field.equals(create(VisibilityType.PACKAGE_PRIVATE, mockType, "otherFieldName")));
        Assertions.assertFalse(field.equals(create(VisibilityType.PACKAGE_PRIVATE, mockType, "fieldName", mockOtherValue)));
        Assertions.assertFalse(field.equals("abc123"));
        Assertions.assertFalse(field.equals(null));

        // Passes
        Assertions.assertTrue(field.equals(field));
        Assertions.assertTrue(field.equals(create(VisibilityType.PACKAGE_PRIVATE, mockType, "fieldName")));
        Assertions.assertTrue(field.equals(create(VisibilityType.PRIVATE, mockType, "fieldName")));
    }

    /**
     * Verify that the appropriate code is generated
     */
    @Test
    public void testGenerateSelfAllDefaults() {
        when(mockType.getSimpleName()).thenReturn("MockType");

        field.appendSelf(mockBuilder, mockImports);
        verify(mockType).registerImport(mockImports);
        verify(mockBuilder).append("MockType fieldName;");
        verify(mockType).getSimpleName();
    }

    /**
     * Verify that the appropriate code is generated
     */
    @Test
    public void testGenerateSelfAllUpdateVisibility() {
        when(mockType.getSimpleName()).thenReturn("MockType");

        int timesRepeated = 0;
        for (VisibilityType visType: VisibilityType.values()) {
            timesRepeated++;
            
            field.setVisibility(visType);
            field.appendSelf(mockBuilder, mockImports);
            verify(mockType, times(timesRepeated)).registerImport(mockImports);
            verify(mockType, times(timesRepeated)).getSimpleName();
            verify(mockBuilder).append(visType.getKeyword() + "MockType fieldName;");
        }
    }
    
    /**
     * Verify that the static flag is properly applied
     */
    @Test
    public void testGenerateSelfStaticUpdate() {
        when(mockType.getSimpleName()).thenReturn("MockType");

        field.appendSelf(mockBuilder, mockImports);
        verify(mockType).registerImport(mockImports);
        verify(mockBuilder).append("MockType fieldName;");
        verify(mockType).getSimpleName();
        
        field.setStatic(true);
        field.appendSelf(mockBuilder, mockImports);
        verify(mockType, times(2)).registerImport(mockImports);
        verify(mockBuilder).append("static MockType fieldName;");
        verify(mockType, times(2)).getSimpleName();
        
        field.setStatic(false);
        field.appendSelf(mockBuilder, mockImports);
        verify(mockType, times(3)).registerImport(mockImports);
        verify(mockBuilder, times(2)).append("MockType fieldName;");
        verify(mockType, times(3)).getSimpleName();
    }
    
    /**
     * Verify that the final flag is properly applied
     */
    @Test
    public void testGenerateSelfFinalUpdate() {
        when(mockType.getSimpleName()).thenReturn("MockType");

        field.appendSelf(mockBuilder, mockImports);
        verify(mockType).registerImport(mockImports);
        verify(mockBuilder).append("MockType fieldName;");
        verify(mockType).getSimpleName();
        
        field.setFinal(true);
        field.appendSelf(mockBuilder, mockImports);
        verify(mockType, times(2)).registerImport(mockImports);
        verify(mockBuilder).append("final MockType fieldName;");
        verify(mockType, times(2)).getSimpleName();
        
        field.setFinal(false);
        field.appendSelf(mockBuilder, mockImports);
        verify(mockType, times(3)).registerImport(mockImports);
        verify(mockBuilder, times(2)).append("MockType fieldName;");
        verify(mockType, times(3)).getSimpleName();
    }
    
    /**
     * Verify that the field can have generics.
     */
    @Test
    public void testSingleGeneric() {
        when(mockType.getSimpleName()).thenReturn("MockType");
        when(mockGeneric1.generateApplication()).thenReturn("GEN1");
        field.addGeneric(mockGeneric1);
        
        field.appendSelf(mockBuilder, mockImports);
        verify(mockType).registerImport(mockImports);
        verify(mockType).getSimpleName();
        verify(mockGeneric1).generateApplication();
        verify(mockBuilder).append("MockType<GEN1> fieldName;");
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
        field.addGeneric(mockGeneric1);
        field.addGeneric(mockGeneric2);
        field.addGeneric(mockGeneric3);
        
        field.appendSelf(mockBuilder, mockImports);
        verify(mockType).registerImport(mockImports);
        verify(mockType).getSimpleName();
        verify(mockGeneric1).generateApplication();
        verify(mockGeneric2).generateApplication();
        verify(mockGeneric3).generateApplication();
        verify(mockBuilder).append("MockType<GEN1, GEN2, GEN3> fieldName;");
    }
    
    /**
     * Verify that the field can be of type generic
     */
    @Test
    public void testGenerateSelf_GenericType() {
        field = create(VisibilityType.PROTECTED, mockGeneric1, "fieldName");
        field.setStatic(true);
        
        when(mockGeneric1.getSimpleName()).thenReturn("GEN1");
        
        field.appendSelf(mockBuilder, mockImports);
        verify(mockGeneric1).registerImport(mockImports);
        verify(mockBuilder).append("protected static GEN1 fieldName;");
        verify(mockGeneric1).getSimpleName();
    }
}
