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

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.codegen.DefinitionException;
import tendril.codegen.VisibilityType;
import tendril.codegen.classes.EnumerationEntry;
import tendril.codegen.field.type.ClassType;
import tendril.test.helper.TestEnum;

/**
 * Test case for {@link JValueFactory}
 */
public class JValueFactoryTest extends SharedJValueTest {
    
    // Mocks to use for testing
    @Mock
    private EnumerationEntry mockEntry;
    @Mock
    private ClassType mockType;

    private int timesImported = 1;
    private ClassType lastImport = null;
    
    /**
     * @see tendril.codegen.field.value.SharedJValueTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        super.prepareTest();
        lastImport = null;
        timesImported = 1;
    }

    /**
     * @see tendril.codegen.field.value.SharedJValueTest#verifyMockImports()
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
    	ClassType classType = new ClassType(ClassType.class);
        lastImport = classType;
    	assertCode("ClassType.class", JValueFactory.create(classType));
    	
        assertCode("\"StringValue\"", JValueFactory.create("StringValue"));
        assertCode("'a'", JValueFactory.create('a'));
        assertCode("123456l", JValueFactory.create(123456l));
        assertCode("45678", JValueFactory.create(45678));
        assertCode("(short) 789", JValueFactory.create((short) 789));
        assertCode("1.234d", JValueFactory.create(1.234));
        assertCode("2.3456f", JValueFactory.create(2.3456f));
        assertCode("false", JValueFactory.create(false));
        assertCode("true", JValueFactory.create(true));
        assertCode("10", JValueFactory.create((byte) Byte.valueOf("10")));
        verifyMockImports();

        lastImport = new ClassType(VisibilityType.class);
        assertCode("VisibilityType.PACKAGE_PRIVATE", JValueFactory.create(VisibilityType.PACKAGE_PRIVATE));
        verifyMockImports();
        
        lastImport = mockType;
        when(mockEntry.getEnclosingClass()).thenReturn(mockType);
        when(mockEntry.getName()).thenReturn("ELSE");
        when(mockType.getSimpleName()).thenReturn("SOMETHING");
        assertCode("SOMETHING.ELSE", JValueFactory.create(mockEntry));
        verify(mockEntry).getEnclosingClass();
        verify(mockEntry).getName();
        verify(mockType).getSimpleName();
        verifyMockImports();
        
        Assertions.assertThrows(DefinitionException.class, () -> JValueFactory.create(new Object()));
    }
    
    /**
     * Verify that can create arrays of all of the different supported values
     */
    @Test
    public void testCreateArrays() {
        assertCode("{\"abc\", \"def\", \"ghi\", \"jkl\"}", JValueFactory.createArray("abc", "def", "ghi", "jkl"));
        assertCode("{true, false, false, true}", JValueFactory.createArray(true, false, false, true));
        assertCode("{10, 101}", JValueFactory.createArray((byte) Byte.valueOf("10"), (byte) Byte.valueOf("101")));
        assertCode("{'a', 'b', 'c', 'd'}", JValueFactory.createArray('a', 'b', 'c', 'd'));
        assertCode("{1.234d, 5.678d, 9.012d, 0.123d}", JValueFactory.createArray(1.234, 5.678, 9.012, 0.123));
        assertCode("{2.3456f, 7.8901f, 2.3456f}", JValueFactory.createArray(2.3456f, 7.8901f, 2.3456f));
        assertCode("{123, 456, 789}", JValueFactory.createArray(123, 456, 789));
        assertCode("{123456l, 234567l, 345678l, 456789l}", JValueFactory.createArray(123456l, 234567l, 345678l, 456789l));
        assertCode("{(short) 1, (short) 2, (short) 3, (short) 4}", JValueFactory.createArray((short) 1, (short)2, (short) 3, (short) 4));

        lastImport = new ClassType(TestEnum.class);
        timesImported = 3;
        assertCode("{TestEnum.VALUE1, TestEnum.VALUE2, TestEnum.VALUE3}", JValueFactory.createArray(TestEnum.values()));
        verifyMockImports();

        lastImport = mockType;
        when(mockEntry.getEnclosingClass()).thenReturn(mockType);
        when(mockEntry.getName()).thenReturn("ELSE");
        when(mockType.getSimpleName()).thenReturn("SOMETHING");
        timesImported = 1;
        assertCode("{SOMETHING.ELSE}", JValueFactory.createArray(mockEntry));
        verify(mockEntry).getEnclosingClass();
        verify(mockEntry).getName();
        verify(mockType).getSimpleName();
        verifyMockImports();
    }
}
