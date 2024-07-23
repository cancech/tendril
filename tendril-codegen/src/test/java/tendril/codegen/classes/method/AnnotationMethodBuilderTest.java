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
package tendril.codegen.classes.method;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import tendril.codegen.VisibilityType;
import tendril.codegen.classes.SharedMethodBuilderTest;
import tendril.codegen.field.type.Type;

/**
 * Test case for {@link InterfaceMethodBuilder}
 */
public class AnnotationMethodBuilderTest extends SharedMethodBuilderTest<ConcreteMethodBuilder<Type>> {
    
    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        builder = new AnnotationMethodBuilder<Type>(mockClass, mockReturnType, "MethodName");
    }

    /**
     * Verify that the validation properly accounts for all cases
     */
    @Test
    public void testValidateNotVoid() {
        when(mockReturnType.isVoid()).thenReturn(false);
        
        // Only public is allowed
        verifyValidateDoesNotThrow(VisibilityType.PUBLIC);
        verify(mockReturnType).isVoid();
        verifyValidateDoesThrow(VisibilityType.PROTECTED);
        verify(mockReturnType, times(2)).isVoid();
        verifyValidateDoesThrow(VisibilityType.PACKAGE_PRIVATE);
        verify(mockReturnType, times(3)).isVoid();
        verifyValidateDoesThrow(VisibilityType.PRIVATE);
        verify(mockReturnType, times(4)).isVoid();
    }

    /**
     * Verify that the validation properly accounts for all cases
     */
    @Test
    public void testValidateIsVoid() {
        when(mockReturnType.isVoid()).thenReturn(true);
        
        // Void is not allowed at all
        verifyValidateDoesThrow(VisibilityType.PUBLIC);
        verify(mockReturnType).isVoid();
        verifyValidateDoesThrow(VisibilityType.PROTECTED);
        verify(mockReturnType, times(2)).isVoid();
        verifyValidateDoesThrow(VisibilityType.PACKAGE_PRIVATE);
        verify(mockReturnType, times(3)).isVoid();
        verifyValidateDoesThrow(VisibilityType.PRIVATE);
        verify(mockReturnType, times(4)).isVoid();
    }

    /**
     * Verify that the proper method is created
     */
    @Test
    public void testBuildMethod() {
        verifyBuildMethodType(JMethodAnnotation.class);
    }
    
    /**
     * Verify that code cannot be added to the annotation
     */
    @Test
    public void testAddCodeThrowsException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> builder.emptyImplementation());
        Assertions.assertThrows(IllegalArgumentException.class, () -> builder.addCode());
        Assertions.assertThrows(IllegalArgumentException.class, () -> builder.addCode("a", "b", "c", "d"));
    }
}
