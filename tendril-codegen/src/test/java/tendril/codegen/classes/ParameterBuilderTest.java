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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.PrimitiveType;
import tendril.codegen.field.type.Type;
import tendril.codegen.field.type.VoidType;
import tendril.test.AbstractUnitTest;

/**
 * Test case for {@link ParameterBuilder}
 */
public class ParameterBuilderTest extends AbstractUnitTest {

    // Mocks to use for testing
    @Mock
    private ParameterizedElementBuilder<MethodBuilder<VoidType>> mockParameterizedElementBuilder;
    @Mock
    private MethodBuilder<VoidType> mockMethodBuilder;
    @Mock
    private Type mockType;

    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        // Not required
    }

    /**
     * Verify that validation is performed properly
     */
    @Test
    public void testValidate() {
        // Void parameter type is not allowed
        ParameterBuilder<Type, VoidType> builder = new ParameterBuilder<Type, VoidType>(VoidType.INSTANCE, "paramName");
        Assertions.assertThrows(IllegalArgumentException.class, () -> builder.validate());

        // Primitive is OK
        for (PrimitiveType type : PrimitiveType.values()) {
            builder.setType(type);
            builder.validate();
        }

        // Class is OK
        builder.setType(new ClassType(ClassType.class));
        builder.validate();

        // Non-void is OK
        when(mockType.isVoid()).thenReturn(false);
        builder.setType(mockType);
        builder.validate();
        verify(mockType).isVoid();

        // Void is not OK
        when(mockType.isVoid()).thenReturn(true);
        Assertions.assertThrows(IllegalArgumentException.class, () -> builder.validate());
        verify(mockType, times(2)).isVoid();
    }

    /**
     * Verify that creating with a {@link MethodBuilder} adds the parameter to the builder
     */
    @Test
    public void testWithMethodBuilder() {
        when(mockType.isVoid()).thenReturn(false);
        when(mockParameterizedElementBuilder.addParameter(any())).thenReturn(mockMethodBuilder);
        ParameterBuilder<Type, MethodBuilder<VoidType>> builder = new ParameterBuilder<Type, MethodBuilder<VoidType>>(mockParameterizedElementBuilder, mockType, "paramName");
        Assertions.assertThrows(IllegalStateException.class, () -> builder.build());
        Assertions.assertEquals(mockMethodBuilder, builder.finish());
        verify(mockParameterizedElementBuilder).addParameter(new JParameter<Type>(mockType, "paramName"));
        verify(mockType).isVoid();
    }

    /**
     * Verify that creating without a {@link MethodBuilder} does not add the parameter to the builder
     */
    @Test
    public void testWithoutMethodBuilder() {
        when(mockType.isVoid()).thenReturn(false);
        ParameterBuilder<Type, VoidType> builder = new ParameterBuilder<Type, VoidType>(mockType, "parameter");
        Assertions.assertThrows(IllegalStateException.class, () -> builder.finish());
        Assertions.assertEquals(new JParameter<Type>(mockType, "parameter"), builder.build());
        verify(mockType).isVoid();
    }

}
