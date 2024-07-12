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
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.jupiter.api.Test;

import tendril.codegen.VisibilityType;
import tendril.dom.type.core.ClassType;

/**
 * Test case for {@link JValueFactory}
 */
public class JValueFactoryTest extends AbstractJValueTest {

    private int timesImported = 1;
    private ClassType lastImport = null;

    /**
     * @see tendril.codegen.field.AbstractJValueTest#verifyMockImports()
     */
    @Override
    protected void verifyMockImports() {
        if (lastImport != null)
            verify(mockImports, times(timesImported)).add(lastImport);
        verifyNoMoreInteractions(mockImports);
    }

    /**
     * Verify the values created by the factory generate properly
     */
    @Test
    public void testCreatedValues() {
        assertCode("\"StringValue\"", JValueFactory.from("StringValue"));
        assertCode("'a'", JValueFactory.from('a'));
        assertCode("123456l", JValueFactory.from(123456l));
        assertCode("45678", JValueFactory.from(45678));
        assertCode("789", JValueFactory.from((short) 789));
        assertCode("1.234d", JValueFactory.from(1.234));
        assertCode("2.3456f", JValueFactory.from(2.3456f));
        assertCode("false", JValueFactory.from(false));
        assertCode("true", JValueFactory.from(true));
        verifyNoInteractions(mockImports);

        lastImport = new ClassType(VisibilityType.class);
        assertCode("VisibilityType.PACKAGE_PRIVATE", JValueFactory.from(VisibilityType.PACKAGE_PRIVATE));
        lastImport = new ClassType(TestEnum.class);
        timesImported = 3;
        assertCode("{TestEnum.VALUE1, TestEnum.VALUE2, TestEnum.VALUE3}", JValueFactory.from(TestEnum.values()));
    }

}
