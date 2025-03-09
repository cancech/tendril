/*
 * Copyright 2025 Jaroslav Bosak
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
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.codegen.CodeBuilder;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.value.JValue;
import tendril.test.AbstractUnitTest;

/**
 * Test case for the {@link EnumerationEntry}
 */
public class EnumerationEntryTest extends AbstractUnitTest {
    
    // Mocks to use for testing
    @Mock
    private CodeBuilder mockCodeBuilder;
    @Mock
    private Set<ClassType> mockImports;
    @Mock
    private JValue<?, ?> mockValue1;
    @Mock
    private JValue<?, ?> mockValue2;
    @Mock
    private JValue<?, ?> mockValue3;
    
    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        // Intentionally left blank
    }

    /**
     * Verify that an entry without any parameters is properly handled
     */
    @Test
    public void testEntryWithNoParameters() {
        EnumerationEntry entry = new EnumerationEntry("NoName", Collections.emptyList());
        Assertions.assertEquals("NoName", entry.getName());
        Assertions.assertIterableEquals(Collections.emptyList(), entry.getParameters());
        
        entry.generateSelf(mockCodeBuilder, mockImports, ",");
        verify(mockCodeBuilder).append("NoName,");
    }

    /**
     * Verify that an entry with a single parameter is properly handled
     */
    @Test
    public void testEntryWithSingleParameter() {
        when(mockValue1.generate(mockImports)).thenReturn("blah");
        
        EnumerationEntry entry = new EnumerationEntry("MyName", Collections.singletonList(mockValue1));
        Assertions.assertEquals("MyName", entry.getName());
        Assertions.assertIterableEquals(Collections.singletonList(mockValue1), entry.getParameters());
        
        entry.generateSelf(mockCodeBuilder, mockImports, ";");
        verify(mockCodeBuilder).append("MyName(blah);");
        verify(mockValue1).generate(mockImports);
    }

    /**
     * Verify that an entry with multiple parameters is properly handled
     */
    @Test
    public void testEntryWithMultipleParameter() {
        when(mockValue1.generate(mockImports)).thenReturn("1");
        when(mockValue2.generate(mockImports)).thenReturn("2");
        when(mockValue3.generate(mockImports)).thenReturn("3");
        
        EnumerationEntry entry = new EnumerationEntry("Entry", Arrays.asList(mockValue1, mockValue2, mockValue3));
        Assertions.assertEquals("Entry", entry.getName());
        Assertions.assertIterableEquals(Arrays.asList(mockValue1, mockValue2, mockValue3), entry.getParameters());
        
        entry.generateSelf(mockCodeBuilder, mockImports, "-`-");
        verify(mockCodeBuilder).append("Entry(1, 2, 3)-`-");
        verify(mockValue1).generate(mockImports);
    }
}
