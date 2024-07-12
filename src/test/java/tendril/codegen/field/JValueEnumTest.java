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

import org.junit.jupiter.api.Test;

import tendril.codegen.VisibilityType;
import tendril.dom.type.core.ClassType;
import tendril.dom.type.core.PoDType;

/**
 * Test case for {@link JValueEnum}
 */
public class JValueEnumTest extends AbstractJValueTest {

    private ClassType currentImport = null;

    /**
     * Verify that the appropriate code is generated
     */
    @Test
    public void testGenerate() {
        testEnum(VisibilityType.PROTECTED, VisibilityType.class);
        testEnum(PoDType.DOUBLE, PoDType.class);
        testEnum(TestEnum.VALUE3, TestEnum.class);
    }

    /**
     * Helper to shorthand the creation of the {@link JValueEnum} and the necessary import
     * 
     * @param <T>       {@link Enum} to use
     * @param value     T specific Enum entry to use
     * @param enumClass {@link Class} of the Enum
     */
    private <T extends Enum<T>> void testEnum(T value, Class<T> enumClass) {
        currentImport = new ClassType(enumClass);
        assertCode(enumClass.getSimpleName() + "." + value.name(), new JValueEnum<T>(value));
    }

    /**
     * @see tendril.codegen.field.AbstractJValueTest#verifyMockImports()
     */
    @Override
    protected void verifyMockImports() {
        verify(mockImports).add(currentImport);
    }
}
