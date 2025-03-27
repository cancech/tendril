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

import javax.lang.model.type.TypeKind;

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
    private ClassType mockType;
    @Mock
    private ClassType mockOtherType;
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
        EnumerationEntry entry = new EnumerationEntry(mockType, "NoName", Collections.emptyList());
        Assertions.assertEquals(mockType, entry.getEnclosingClass());
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
        
        EnumerationEntry entry = new EnumerationEntry(mockType, "MyName", Collections.singletonList(mockValue1));
        Assertions.assertEquals(mockType, entry.getEnclosingClass());
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
        
        EnumerationEntry entry = new EnumerationEntry(mockType, "Entry", Arrays.asList(mockValue1, mockValue2, mockValue3));
        Assertions.assertEquals(mockType, entry.getEnclosingClass());
        Assertions.assertEquals("Entry", entry.getName());
        Assertions.assertIterableEquals(Arrays.asList(mockValue1, mockValue2, mockValue3), entry.getParameters());
        
        entry.generateSelf(mockCodeBuilder, mockImports, "-`-");
        verify(mockCodeBuilder).append("Entry(1, 2, 3)-`-");
        verify(mockValue1).generate(mockImports);
    }
    
    /**
     * Verify that equality is properly determined
     */
    @SuppressWarnings("unlikely-arg-type")
    @Test
    public void testEquals() {
        EnumerationEntry entry = new EnumerationEntry(mockType, "name", Arrays.asList(mockValue1, mockValue2));
        
        // Failures
        Assertions.assertFalse(entry.equals(new String("abc123")));
        Assertions.assertFalse(entry.equals(new EnumerationEntry(mockOtherType, "name", Collections.emptyList())));
        Assertions.assertFalse(entry.equals(new EnumerationEntry(mockType, "DifferentName", Collections.emptyList())));
        
        // Passes
        Assertions.assertTrue(entry.equals(new EnumerationEntry(mockType, "name", Collections.emptyList())));
    }
    
    /**
     * Verify the conversion from "real" enums is done properly
     */
    @Test
    public void testFrom() {
        // When ClassType is derived
        EnumerationEntry entry = EnumerationEntry.from(TypeKind.BYTE);
        Assertions.assertEquals(new ClassType(TypeKind.class), entry.getEnclosingClass());
        Assertions.assertEquals(TypeKind.BYTE.name(), entry.getName());
        Assertions.assertIterableEquals(Collections.emptyList(), entry.getParameters());

        // When ClassType is explicitly specified
        entry = EnumerationEntry.from(mockType, TypeKind.DECLARED);
        Assertions.assertEquals(mockType, entry.getEnclosingClass());
        Assertions.assertEquals(TypeKind.DECLARED.name(), entry.getName());
        Assertions.assertIterableEquals(Collections.emptyList(), entry.getParameters());

        // When the details are inferred
        entry = EnumerationEntry.from(mockType, "SOME_ENUM_VALUE");
        Assertions.assertEquals(mockType, entry.getEnclosingClass());
        Assertions.assertEquals("SOME_ENUM_VALUE", entry.getName());
        Assertions.assertIterableEquals(Collections.emptyList(), entry.getParameters());
    }
}
