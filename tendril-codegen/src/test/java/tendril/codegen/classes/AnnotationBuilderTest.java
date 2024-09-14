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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.codegen.classes.method.AnnotationMethodBuilder;
import tendril.codegen.field.type.ClassType;
import tendril.test.AbstractUnitTest;
import tendril.test.assertions.ClassAssert;

/**
 * Test case for {@link AnnotationBuilder}
 */
public class AnnotationBuilderTest extends AbstractUnitTest {

    // Mocks to use for testing
    @Mock
    private ClassType mockClassType;

    // Instance to test
    private AnnotationBuilder builder;

    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        when(mockClassType.getSimpleName()).thenReturn("MockClass");
        builder = new AnnotationBuilder(mockClassType);
        verify(mockClassType).getSimpleName();
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
        Assertions.assertThrows(IllegalArgumentException.class, () -> builder.extendsClass(mockClassType));
        Assertions.assertThrows(IllegalArgumentException.class, () -> builder.implementsInterface(mockClassType));
    }

}
