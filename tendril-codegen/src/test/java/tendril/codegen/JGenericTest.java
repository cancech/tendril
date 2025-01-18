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
package tendril.codegen;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.codegen.field.type.ClassType;
import tendril.codegen.generics.GenericType;
import tendril.test.AbstractUnitTest;

/**
 * Test case for {@link JGeneric}
 */
public class JGenericTest extends AbstractUnitTest {

    // Mocks to use for testing
    @Mock
    private Set<ClassType> mockImports;
    @Mock
    private GenericType mockGeneric1;
    @Mock
    private GenericType mockGeneric2;
    @Mock
    private GenericType mockGeneric3;

    // Instance to test
    private JGeneric element;

    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        element = new JGeneric();
    }

    /**
     * Verify that no generic need be added to the element
     */
    @Test
    public void testNoGeneric() {
        Assertions.assertIterableEquals(Collections.emptyList(), element.getGenerics());
        Assertions.assertEquals(" ", element.getGenericsApplicationKeyword(true));
        Assertions.assertEquals("", element.getGenericsApplicationKeyword(false));
        Assertions.assertEquals(" ", element.getGenericsDefinitionKeyword(true));
        Assertions.assertEquals("", element.getGenericsDefinitionKeyword(false));
    }

    /**
     * Verify that a single generic can be added to the element
     */
    @Test
    public void testSingleGeneric() {
        when(mockGeneric1.generateApplication()).thenReturn("GEN_1_APP");
        when(mockGeneric1.generateDefinition()).thenReturn("GEN_1_DEF");
        element.addGeneric(mockGeneric1);

        Assertions.assertIterableEquals(Collections.singleton(mockGeneric1), element.getGenerics());
        Assertions.assertEquals("<GEN_1_APP> ", element.getGenericsApplicationKeyword(true));
        Assertions.assertEquals("<GEN_1_APP>", element.getGenericsApplicationKeyword(false));
        Assertions.assertEquals("<GEN_1_DEF> ", element.getGenericsDefinitionKeyword(true));
        Assertions.assertEquals("<GEN_1_DEF> ", element.getGenericsDefinitionKeyword(false));

        // Generating will register the generics
        element.registerGenerics(mockImports);
        verify(mockGeneric1).registerImport(mockImports);
    }

    /**
     * Verify that multiple generics can be added to the element
     */
    @Test
    public void testMultipleGenerics() {
        when(mockGeneric1.generateApplication()).thenReturn("GEN_1_APP");
        when(mockGeneric2.generateApplication()).thenReturn("GEN_2_APP");
        when(mockGeneric3.generateApplication()).thenReturn("GEN_3_APP");
        when(mockGeneric1.generateDefinition()).thenReturn("GEN_1_DEF");
        when(mockGeneric2.generateDefinition()).thenReturn("GEN_2_DEF");
        when(mockGeneric3.generateDefinition()).thenReturn("GEN_3_DEF");
        element.addGeneric(mockGeneric1);
        element.addGeneric(mockGeneric2);
        element.addGeneric(mockGeneric3);

        Assertions.assertIterableEquals(Arrays.asList(mockGeneric1, mockGeneric2, mockGeneric3), element.getGenerics());
        Assertions.assertEquals("<GEN_1_APP, GEN_2_APP, GEN_3_APP> ", element.getGenericsApplicationKeyword(true));
        Assertions.assertEquals("<GEN_1_APP, GEN_2_APP, GEN_3_APP>", element.getGenericsApplicationKeyword(false));
        Assertions.assertEquals("<GEN_1_DEF, GEN_2_DEF, GEN_3_DEF> ", element.getGenericsDefinitionKeyword(true));
        Assertions.assertEquals("<GEN_1_DEF, GEN_2_DEF, GEN_3_DEF> ", element.getGenericsDefinitionKeyword(false));

        // Generating will register the generics
        element.registerGenerics(mockImports);
        verify(mockGeneric1).registerImport(mockImports);
        verify(mockGeneric2).registerImport(mockImports);
        verify(mockGeneric3).registerImport(mockImports);
    }

}
