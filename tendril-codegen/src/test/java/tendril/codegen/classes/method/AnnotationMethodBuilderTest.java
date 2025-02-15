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

import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.codegen.DefinitionException;
import tendril.codegen.VisibilityType;
import tendril.codegen.classes.MethodBuilder;
import tendril.codegen.classes.SharedMethodBuilderTest;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.PrimitiveType;
import tendril.codegen.field.type.Type;
import tendril.codegen.field.value.JValue;

/**
 * Test case for {@link InterfaceMethodBuilder}
 */
public class AnnotationMethodBuilderTest extends SharedMethodBuilderTest<ConcreteMethodBuilder<Type>> {
    
    // Mocks to use for testing
    @Mock
    private JValue<PrimitiveType, ?> mockPrimitiveValue;
    @Mock
    private JValue<ClassType, ?> mockClassValue;
    @Mock
    private ClassType mockReturnTypeClassType;
    @Mock
    private ClassType mockValueClassType;
    @Mock
    private Set<ClassType> mockImports;
    @Mock
    private JValue<Type, ?> mockDefaultValue;
    
    /**
     * @see tendril.codegen.classes.SharedMethodBuilderTest#createBuilder()
     */
    @Override
    protected MethodBuilder<Type> createBuilder() {
        AnnotationMethodBuilder<Type> builder = new AnnotationMethodBuilder<>(mockClassBuilder, METHOD_NAME);
        verify(mockClassBuilder).getType();
        return builder;
    }

    /**
     * Verify that the validation properly accounts for all cases
     */
    @Test
    public void testValidateNotVoid() {
        when(mockReturnType.isVoid()).thenReturn(false);
        when(mockDefaultValue.getType()).thenReturn(mockReturnType);
        when(mockReturnType.isAssignableFrom(mockReturnType)).thenReturn(true);
        builder.setDefaultValue(mockDefaultValue);

        // Only public is allowed
        verifyValidateDoesNotThrow(VisibilityType.PUBLIC);
        verify(mockReturnType).isVoid();
        verify(mockDefaultValue).getType();
        verify(mockReturnType).isAssignableFrom(mockReturnType);
        verifyValidateDoesThrow(VisibilityType.PROTECTED, false);
        verify(mockReturnType).isVoid();
        verify(mockReturnType).isVoid();
        verify(mockDefaultValue).getType();
        verifyValidateDoesThrow(VisibilityType.PACKAGE_PRIVATE, false);
        verify(mockReturnType).isVoid();
        verify(mockReturnType).isVoid();
        verify(mockDefaultValue).getType();
        verifyValidateDoesThrow(VisibilityType.PRIVATE, false);
        verify(mockReturnType).isVoid();
        verify(mockReturnType).isVoid();
        verify(mockDefaultValue).getType();
    }

    /**
     * Verify that the validation properly accounts for all cases
     */
    @Test
    public void testValidateIsVoid() {
        when(mockReturnType.isVoid()).thenReturn(true);
        builder.setDefaultValue(mockDefaultValue);
        
        // Void is not allowed at all
        verifyValidateDoesThrow(VisibilityType.PUBLIC, false);
        verify(mockReturnType).isVoid();
        verifyValidateDoesThrow(VisibilityType.PROTECTED, false);
        verify(mockReturnType).isVoid();
        verifyValidateDoesThrow(VisibilityType.PACKAGE_PRIVATE, false);
        verify(mockReturnType).isVoid();
        verifyValidateDoesThrow(VisibilityType.PRIVATE, false);
        verify(mockReturnType).isVoid();
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
        Assertions.assertThrows(DefinitionException.class, () -> builder.emptyImplementation());
        verify(mockEnclosingClassType).getFullyQualifiedName();
        Assertions.assertThrows(DefinitionException.class, () -> builder.addCode());
        verify(mockEnclosingClassType, times(2)).getFullyQualifiedName();
        Assertions.assertThrows(DefinitionException.class, () -> builder.addCode("a", "b", "c", "d"));
        verify(mockEnclosingClassType, times(3)).getFullyQualifiedName();
    }
    
    /**
     * Verify that applying a different primitive type to a primitive attribute results in an exception
     */
    @Test
    public void testApplyDifferentPrimitiveValueTypeFails() {
        int times = 0;
        
        for (PrimitiveType methodType: PrimitiveType.values()) {
            AnnotationMethodBuilder<PrimitiveType> builder = new AnnotationMethodBuilder<>(mockClassBuilder, "primitiveAttr");
            verify(mockClassBuilder, times(methodType.ordinal() + 2)).getType();
            builder.setType(methodType);

            for (PrimitiveType valueType: PrimitiveType.values()) {
                if (methodType == valueType)
                    continue;

                when(mockPrimitiveValue.getType()).thenReturn(valueType);
                builder.setDefaultValue(mockPrimitiveValue);
                Assertions.assertThrows(DefinitionException.class, ()-> builder.validate());
                verify(mockEnclosingClassType, times(++times)).getFullyQualifiedName();
                verify(mockPrimitiveValue, times(times)).getType();
            }
        }
    }
    
    /**
     * Verify that applying a different class type to a class type attribute results in an exception
     */
    @Test
    public void testApplyDifferentClassValueTypeFails() {
        when(mockClassValue.getType()).thenReturn(mockValueClassType);
        when(mockReturnTypeClassType.isAssignableFrom(mockValueClassType)).thenReturn(false);
        when(mockReturnTypeClassType.isVoid()).thenReturn(false);
        
        AnnotationMethodBuilder<ClassType> builder = new AnnotationMethodBuilder<>(mockClassBuilder, "classAttr");
        verify(mockClassBuilder, times(2)).getType();
        builder.setType(mockReturnTypeClassType);
        builder.setDefaultValue(mockClassValue);
        Assertions.assertThrows(DefinitionException.class, ()-> builder.validate());
        verify(mockEnclosingClassType).getFullyQualifiedName();
        verify(mockClassValue).getType();
        verify(mockReturnTypeClassType).isVoid();
        verify(mockReturnTypeClassType).isAssignableFrom(mockValueClassType);
    }
}
