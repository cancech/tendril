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
package tendril.codegen.field.type;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.test.AbstractUnitTest;

/**
 * Test case for {@link TypeFactory}
 */
public class TypeFactoryTest extends AbstractUnitTest {

    // Mocks to use for testing
    @Mock
    private TypeMirror mockMirror;

    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        // Not required
    }

    /**
     * Verify that a Void type can be created properly from {@link TypeKind}
     */
    @Test
    public void testCreateVoidFromTypeKind() {
        performCreateTest(TypeKind.VOID, VoidType.INSTANCE);
        verify(mockMirror).getKind();
    }

    /**
     * Verify that a primitive type can be created properly from {@link TypeKind}
     */
    @Test
    public void testCreatePrimitiveTypeKind() {
        int timesGetKind = 0;
        performCreateTest(TypeKind.BOOLEAN, PrimitiveType.BOOLEAN);
        verify(mockMirror, times(++timesGetKind)).getKind();
        performCreateTest(TypeKind.BYTE, PrimitiveType.BYTE);
        verify(mockMirror, times(++timesGetKind)).getKind();
        performCreateTest(TypeKind.CHAR, PrimitiveType.CHAR);
        verify(mockMirror, times(++timesGetKind)).getKind();
        performCreateTest(TypeKind.DOUBLE, PrimitiveType.DOUBLE);
        verify(mockMirror, times(++timesGetKind)).getKind();
        performCreateTest(TypeKind.FLOAT, PrimitiveType.FLOAT);
        verify(mockMirror, times(++timesGetKind)).getKind();
        performCreateTest(TypeKind.INT, PrimitiveType.INT);
        verify(mockMirror, times(++timesGetKind)).getKind();
        performCreateTest(TypeKind.LONG, PrimitiveType.LONG);
        verify(mockMirror, times(++timesGetKind)).getKind();
        performCreateTest(TypeKind.SHORT, PrimitiveType.SHORT);
        verify(mockMirror, times(++timesGetKind)).getKind();
    }

    /**
     * Verify that a class can be created Properly from {@link TypeKind}
     */
    @Test
    public void testCreateClassTypeKind() {
        when(mockMirror.toString()).thenReturn("a.b.c.D");
        performCreateTest(TypeKind.DECLARED, new ClassType("a.b.c.D"));
        verify(mockMirror).getKind();
    }
    
    /**
     * Verify that an exception is thrown if creation from any other {@link TypeKind} is attempted
     */
    @Test
    public void testCreateOtherTypeKind() {
        int timesGetKind = 0;
        for (TypeKind kind: TypeKind.values()) {
            if (kind.isPrimitive() || kind == TypeKind.VOID || kind == TypeKind.DECLARED)
                continue;
            
            when(mockMirror.getKind()).thenReturn(kind);
            Assertions.assertThrows(IllegalArgumentException.class, () -> TypeFactory.create(mockMirror));
            verify(mockMirror, times(++timesGetKind)).getKind();
        }
    }

    /**
     * Perform the steps necessary to verify the create factory method works as expected
     * 
     * @param mirrorKind    {@link TypeKind} the mirror passed into the create method returns
     * @param expectCreated {@link Type} that is expected to be created
     */
    private void performCreateTest(TypeKind mirrorKind, Type expectCreated) {
        when(mockMirror.getKind()).thenReturn(mirrorKind);
        Assertions.assertEquals(expectCreated, TypeFactory.create(mockMirror));
    }
    
    /**
     * Verify that the primitives can be created from the class
     */
    @Test
    public void testCreatePrimitiveFromClass() {
        performCreateTest(Boolean.class, PrimitiveType.BOOLEAN);
        performCreateTest(boolean.class, PrimitiveType.BOOLEAN);
        performCreateTest(Byte.class, PrimitiveType.BYTE);
        performCreateTest(byte.class, PrimitiveType.BYTE);
        performCreateTest(Character.class, PrimitiveType.CHAR);
        performCreateTest(char.class, PrimitiveType.CHAR);
        performCreateTest(Double.class, PrimitiveType.DOUBLE);
        performCreateTest(double.class, PrimitiveType.DOUBLE);
        performCreateTest(Float.class, PrimitiveType.FLOAT);
        performCreateTest(float.class, PrimitiveType.FLOAT);
        performCreateTest(Integer.class, PrimitiveType.INT);
        performCreateTest(int.class, PrimitiveType.INT);
        performCreateTest(Long.class, PrimitiveType.LONG);
        performCreateTest(long.class, PrimitiveType.LONG);
        performCreateTest(Short.class, PrimitiveType.SHORT);
        performCreateTest(short.class, PrimitiveType.SHORT);
    }
    
    /**
     * Verify that a {@link ClassType} can be created from an arbitrary {@link Class}
     */
    @Test
    public void createClassTypeFromClass() {
        performCreateTest(ClassType.class, new ClassType(ClassType.class));
    }
    
    /**
     * Verify that the appropriate Type is created from an Array
     */
    @Test
    public void createArrayFromClass() {
        performCreateTest(Boolean[].class, new ArrayType<PrimitiveType>(PrimitiveType.BOOLEAN));
        performCreateTest(boolean[].class, new ArrayType<PrimitiveType>(PrimitiveType.BOOLEAN));
        performCreateTest(Byte[].class, new ArrayType<PrimitiveType>(PrimitiveType.BYTE));
        performCreateTest(byte[].class, new ArrayType<PrimitiveType>(PrimitiveType.BYTE));
        performCreateTest(Character[].class, new ArrayType<PrimitiveType>(PrimitiveType.CHAR));
        performCreateTest(char[].class, new ArrayType<PrimitiveType>(PrimitiveType.CHAR));
        performCreateTest(Double[].class, new ArrayType<PrimitiveType>(PrimitiveType.DOUBLE));
        performCreateTest(double[].class, new ArrayType<PrimitiveType>(PrimitiveType.DOUBLE));
        performCreateTest(Float[].class, new ArrayType<PrimitiveType>(PrimitiveType.FLOAT));
        performCreateTest(float[].class, new ArrayType<PrimitiveType>(PrimitiveType.FLOAT));
        performCreateTest(Integer[].class, new ArrayType<PrimitiveType>(PrimitiveType.INT));
        performCreateTest(int[].class, new ArrayType<PrimitiveType>(PrimitiveType.INT));
        performCreateTest(Long[].class, new ArrayType<PrimitiveType>(PrimitiveType.LONG));
        performCreateTest(long[].class, new ArrayType<PrimitiveType>(PrimitiveType.LONG));
        performCreateTest(Short[].class, new ArrayType<PrimitiveType>(PrimitiveType.SHORT));
        performCreateTest(short[].class, new ArrayType<PrimitiveType>(PrimitiveType.SHORT));
        performCreateTest(ClassType[].class, new ArrayType<ClassType>(new ClassType(ClassType.class)));
    }

    /**
     * Perform the steps necessary to verify the create factory method works as expected
     * 
     * @param mirrorKind    {@link Class} defining the type that is to be created
     * @param expectCreated {@link Type} that is expected to be created
     */
    private void performCreateTest(Class<?> klass, Type expectCreated) {
        Assertions.assertEquals(expectCreated, TypeFactory.create(klass));
    }
}
