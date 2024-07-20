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

import java.util.Set;

import tendril.codegen.field.value.JValue;
import tendril.codegen.field.value.JValueFactory;
import tendril.util.TendrilUtil;

/**
 * Enumeration of all possible java primitives
 */
public enum PrimitiveType implements Type {
    /** boolean primitive */
    BOOLEAN(Boolean.class, boolean.class),
    /** byte primitive */
    BYTE(Byte.class, byte.class),
    /** char primitive */
    CHAR(Character.class, char.class),
    /** double primitive */
    DOUBLE(Double.class, double.class),
    /** float primitive */
    FLOAT(Float.class, float.class),
    /** int primitive */
    INT(Integer.class, int.class),
    /** long primitive */
    LONG(Long.class, long.class),
    /** short primitive */
    SHORT(Short.class, short.class);

    /**
     * Provide the appropriate {@link PrimitiveType} for the specified {@link Class}. If no {@link PrimitiveType} exists for the specified {@link Class} an {@link IllegalArgumentException} is thrown
     * 
     * @param klass {@link Class} for which to find the {@link PrimitiveType}
     * @return {@link PrimitiveType} for the {@link Class} or {@link IllegalArgumentException} if no such {@link PrimitiveType} exists
     */
    public static PrimitiveType from(Class<?> klass) {
        for (PrimitiveType type : PrimitiveType.values()) {
            if (type.isCorrecTypeForClass(klass))
                return type;
        }

        throw new IllegalArgumentException("Invalid Primitive: " + klass.getName());
    }

    /** Class where the object version of the primitive is defined */
    private final Class<?> objectClass;
    /** Class where the primitive version of the primitive if defined */
    private final Class<?> primitiveClass;

    /**
     * CTOR
     * 
     * @param objectClass    {@link Class} where the object version of the primitive is defined
     * @param primitiveClass {@link Class} where the primitive version of the primitive if defined
     */
    private PrimitiveType(Class<?> objectClass, Class<?> primitiveClass) {
        this.objectClass = objectClass;
        this.primitiveClass = primitiveClass;
    }

    /**
     * Check whether the provided class  
     * @param desiredClass
     * @return
     */
    private boolean isCorrecTypeForClass(Class<?> desiredClass) {
        return TendrilUtil.oneOfMany(desiredClass, objectClass, primitiveClass);
    }

    /**
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return getSimpleName();
    }

    /**
     * @see tendril.codegen.field.type.Type#isAssignableFrom(tendril.codegen.field.type.Type)
     */
    @Override
    public boolean isAssignableFrom(Type other) {
        return this == other;
    }

    /**
     * @see tendril.codegen.field.type.Type#isTypeOf(java.lang.Object)
     */
    @Override
    public boolean isTypeOf(Object value) {
        switch (this) {
            case BOOLEAN:
                return value instanceof Boolean;
            case BYTE:
                return value instanceof Byte;
            case CHAR:
                return value instanceof Character;
            case DOUBLE:
                return value instanceof Double;
            case FLOAT:
                return value instanceof Float;
            case INT:
                return value instanceof Integer;
            case LONG:
                return value instanceof Long;
            case SHORT:
                return value instanceof Short;
        }
        
        throw new IllegalArgumentException("Invalid primitive type " + this);
    }

    /**
     * @see tendril.codegen.field.type.Importable#registerImport(java.util.Set)
     */
    @Override
    public void registerImport(Set<ClassType> classImports) {
    }

    /**
     * @see tendril.codegen.field.type.Type#isVoid()
     */
    @Override
    public boolean isVoid() {
        return false;
    }

    /**
     * @see tendril.codegen.field.type.Type#getSimpleName()
     */
    @Override
    public String getSimpleName() {
        return name().toLowerCase();
    }

    /**
     * @see tendril.codegen.field.type.Type#asValue(java.lang.Object)
     */
    @Override
    public JValue<?, ?> asValue(Object value) {
        if (!isTypeOf(value))
            throw new IllegalArgumentException("Invalid value provided. Expected " + this + " but received " + value.getClass());

        return JValueFactory.create(value);
    }
}
