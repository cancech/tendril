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
package tendril.codegen.field.value;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.codegen.VisibilityType;
import tendril.codegen.classes.EnumerationEntry;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.PrimitiveType;
import tendril.codegen.field.type.TypeFactory;
import tendril.test.helper.TestEnum;

/**
 * Test case for {@link JValueEnum}
 */
public class JValueEnumTest extends SharedJValueTest {
    
    // Mocks to use for testing
    @Mock
    private ClassType mockType;
    @Mock
    private EnumerationEntry mockEntry;

    /** Tracks which import should have been last registered */
    private ClassType currentImport = null;

    /**
     * Verify that the appropriate code is generated
     */
    @Test
    public void testGenerateFromValue() {
        testEnum(VisibilityType.PROTECTED, VisibilityType.class);
        testEnum(PrimitiveType.DOUBLE, PrimitiveType.class);
        testEnum(TestEnum.VALUE3, TestEnum.class);
    }
    
    /**
     * Verify that the appropriate code is generated 
     */
    @Test
    public void testGenerateFromValueAndType() {
        when(mockType.getSimpleName()).thenReturn("SIMPLE");
        currentImport = mockType;
        
        assertCode("SIMPLE.PUBLIC", new JValueEnum(mockType, VisibilityType.PUBLIC));
        verify(mockType).getSimpleName();
    }
    
    /**
     * Verify that the appropriate code is generated
     */
    @Test
    public void testGenerateFromEnumerationEntry() {
        when(mockEntry.getEnclosingClass()).thenReturn(mockType);
        when(mockEntry.getName()).thenReturn("ENTRY");
        when(mockType.getSimpleName()).thenReturn("NAME");
        currentImport = mockType;

        assertCode("NAME.ENTRY", new JValueEnum(mockEntry));
        verify(mockEntry).getEnclosingClass();
        verify(mockEntry).getName();
        verify(mockType).getSimpleName();
    }

    /**
     * Helper to shorthand the creation of the {@link JValueEnum} and the necessary import
     * 
     * @param <T>       {@link Enum} to use
     * @param value     T specific Enum entry to use
     * @param enumClass {@link Class} of the Enum
     */
    private <T extends Enum<T>> void testEnum(T value, Class<T> enumClass) {
        currentImport = TypeFactory.createClassType(enumClass);
        assertCode(enumClass.getSimpleName() + "." + value.name(), new JValueEnum(value));
    }

    /**
     * @see tendril.codegen.field.value.SharedJValueTest#verifyMockImports()
     */
    @Override
    protected void verifyMockImports() {
        verify(mockImports).add(currentImport);
    }
}
