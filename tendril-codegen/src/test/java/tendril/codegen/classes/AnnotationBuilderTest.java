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
package tendril.codegen.classes;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.codegen.DefinitionException;
import tendril.codegen.classes.method.AnnotationMethodBuilder;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.generics.GenericType;
import tendril.test.AbstractUnitTest;
import tendril.test.assertions.ClassAssert;

/**
 * Test case for {@link AnnotationBuilder}
 */
public class AnnotationBuilderTest extends AbstractUnitTest {

    // Mocks to use for testing
    @Mock
    private ClassType mockClassType;
    @Mock
    private JClass mockJClass;
    @Mock
    private GenericType mockGeneric;

    // Instance to test
    private AnnotationBuilder builder;

    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        when(mockClassType.getSimpleName()).thenReturn("MockClass");
        when(mockClassType.getGenerics()).thenReturn(Collections.emptyList());
        builder = new AnnotationBuilder(mockClassType);
        verify(mockClassType).getSimpleName();
        verify(mockClassType).getGenerics();
    }

    /**
     * Verify that the correct method builder is created
     */
    @Test
    public void testCreatMethodBuilder() {
        ClassAssert.assertInstance(AnnotationMethodBuilder.class, builder.createMethodBuilder("method"));
    }

    /**
     * Verify that create produces the correct class type
     */
    @Test
    public void testCreate() {
        when(mockClassType.getPackageName()).thenReturn("package");
        when(mockClassType.getClassName()).thenReturn("ClassName");
        ClassAssert.assertInstance(JClassAnnotation.class, builder.create());
        verify(mockClassType).getClassName();
        verify(mockClassType).getPackageName();
    }
    
    /**
     * Verify that the class hierarchy is properly handled
     */
    @Test
    public void testClassHierarchy() {
        Assertions.assertThrows(DefinitionException.class, () -> builder.extendsClass(mockJClass));
        verify(mockClassType).getFullyQualifiedName();
        Assertions.assertThrows(DefinitionException.class, () -> builder.implementsInterface(mockJClass));
        verify(mockClassType, times(2)).getFullyQualifiedName();
    }
    
    /**
     * Verify that the interface cannot create a constructor
     */
    @Test
    public void testCannotCreateConstructor() {
        Assertions.assertThrows(DefinitionException.class, () -> builder.buildConstructor());
        verify(mockClassType).getFullyQualifiedName();
    }
    
    /**
     * Verify that the interface cannot be generic
     */
    @Test
    public void testCannotAddGeneric() {
        Assertions.assertThrows(DefinitionException.class, () -> builder.addGeneric(mockGeneric));
        verify(mockClassType).getFullyQualifiedName();
    }

}
