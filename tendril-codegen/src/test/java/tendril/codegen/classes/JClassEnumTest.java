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
import tendril.test.AbstractUnitTest;

/**
 * Test case for the {@link JClassEnum}
 */
public class JClassEnumTest extends AbstractUnitTest {
    
    // Mocks to use for testing
    @Mock
    private ClassType mockType;
    @Mock
    private CodeBuilder mockCodeBuilder;
    @Mock
    private Set<ClassType> mockImports;
    @Mock
    private EnumerationEntry mockEntry1;
    @Mock
    private EnumerationEntry mockEntry2;
    @Mock
    private EnumerationEntry mockEntry3;
    
    // Instance to test
    private JClassEnum enumCls;

    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        when(mockType.getClassName()).thenReturn("ClassName");
        when(mockType.getPackageName()).thenReturn("PackageName");
        
        enumCls = new JClassEnum(mockType);
        verify(mockType).getClassName();
        verify(mockType).getPackageName();
    }

    /**
     * Verify that the core elements work as expected
     */
    @Test
    public void testCoreElements() {
        Assertions.assertEquals("enum ", enumCls.getClassKeyword());
        
        // Must be final
        enumCls.setFinal(true);
        Assertions.assertTrue(enumCls.isFinal());
        Assertions.assertEquals("", enumCls.getFinalKeyword());
        enumCls.setFinal(false);
        Assertions.assertTrue(enumCls.isFinal());
        Assertions.assertEquals("", enumCls.getFinalKeyword());
    }
    
    /**
     * Verify that no entries are properly tracked.
     */
    @Test
    public void testNoEntries() {
        Assertions.assertIterableEquals(Collections.emptyList(), enumCls.getEnumerations());
        enumCls.processFields(mockCodeBuilder, mockImports);
    }
    
    /**
     * Verify that a single entry is properly tracked.
     */
    @Test
    public void testSingleEntry() {
        enumCls.add(mockEntry1);
        
        Assertions.assertIterableEquals(Collections.singletonList(mockEntry1), enumCls.getEnumerations());
        enumCls.processFields(mockCodeBuilder, mockImports);
        verify(mockEntry1).generateSelf(mockCodeBuilder, mockImports, ";");
        verify(mockCodeBuilder).blankLine();
    }
    
    /**
     * Verify that a multiple entries are properly tracked.
     */
    @Test
    public void testMultipleEntries() {
        enumCls.add(mockEntry1);
        enumCls.add(mockEntry2);
        enumCls.add(mockEntry3);
        
        Assertions.assertIterableEquals(Arrays.asList(mockEntry1, mockEntry2, mockEntry3), enumCls.getEnumerations());
        enumCls.processFields(mockCodeBuilder, mockImports);
        verify(mockEntry1).generateSelf(mockCodeBuilder, mockImports, ",");
        verify(mockEntry2).generateSelf(mockCodeBuilder, mockImports, ",");
        verify(mockEntry3).generateSelf(mockCodeBuilder, mockImports, ";");
        verify(mockCodeBuilder).blankLine();
    }
}
