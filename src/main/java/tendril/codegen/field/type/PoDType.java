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

/**
 * Enumeration of the plain ol' Data Types that can be used in Java
 */
public enum PoDType implements Type {
    BOOLEAN, BYTE, CHAR, DOUBLE, FLOAT, INT, LONG, SHORT;

    /**
     * Provide the appropriate PoDType for the specified {@link Class}. If no PoDType exists for the specified {@link Class} an {@link IllegalArgumentException} is thrown
     * 
     * @param klass {@link Class} for which to find the {@link PoDType}
     * @return {@link PoDType} for the {@link Class} or {@link IllegalArgumentException} if no such {@link PoDType} exists
     */
    public static PoDType from(Class<?> klass) {
        if (Boolean.class.equals(klass))
            return BOOLEAN;
        if (Byte.class.equals(klass))
            return BYTE;
        if (Character.class.equals(klass))
            return CHAR;
        if (Double.class.equals(klass))
            return DOUBLE;
        if (Float.class.equals(klass))
            return FLOAT;
        if (Integer.class.equals(klass))
            return INT;
        if (Long.class.equals(klass))
            return LONG;
        if (Short.class.equals(klass))
            return SHORT;

        throw new IllegalArgumentException("Invalid POD: " + klass.getName());
    }

    /**
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return getSimpleName();
    }

    /**
     * @see tendril.codegen.field.type.Type#isAssignableTo(tendril.codegen.field.type.Type)
     */
    @Override
    public boolean isAssignableTo(Type other) {
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
            default:
                return false;
        }
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
        switch (this) {
            case BOOLEAN:
                return JValueFactory.create((Boolean) value);
            case BYTE:
                return JValueFactory.create((Byte) value);
            case CHAR:
                return JValueFactory.create((Character) value);
            case DOUBLE:
                return JValueFactory.create((Double) value);
            case FLOAT:
                return JValueFactory.create((Float) value);
            case INT:
                return JValueFactory.create((Integer) value);
            case LONG:
                return JValueFactory.create((Long) value);
            case SHORT:
                return JValueFactory.create((Short) value);
        }

        throw new IllegalArgumentException("Invalid type specified: " + getSimpleName());
    }
}
