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

import java.lang.reflect.Array;
import java.util.Set;

import tendril.codegen.DefinitionException;
import tendril.codegen.field.value.JValue;
import tendril.codegen.field.value.JValueFactory;
import tendril.util.ArrayConverter;

/**
 * Type for representing an array of any {@link Type}
 * 
 * @param <DATA_TYPE> extending {@link Type} indicating what types of elements are to be stored in the array
 */
public class ArrayType<DATA_TYPE extends Type> implements Type {
    /** The type of the stored elements */
    private final DATA_TYPE containedType;

    /**
     * CTOR
     * 
     * @param containedType DATA_TYPE to be stored in the array
     */
    public ArrayType(DATA_TYPE containedType) {
        this.containedType = containedType;
    }
    
    /**
     * Get the data type of the elements contained within the array
     * 
     * @return DATA_TYPE
     */
    public DATA_TYPE getContainedType() {
        return containedType;
    }

    /**
     * @see tendril.codegen.field.type.Importable#registerImport(java.util.Set)
     */
    @Override
    public void registerImport(Set<ClassType> classImports) {
        containedType.registerImport(classImports);
    }

    /**
     * @see tendril.codegen.field.type.Type#isVoid()
     */
    @Override
    public boolean isVoid() {
        return containedType.isVoid();
    }

    /**
     * @see tendril.codegen.field.type.Type#getSimpleName()
     */
    @Override
    public String getSimpleName() {
        return containedType.getSimpleName() + "[]";
    }

    /**
     * @see tendril.codegen.field.type.Type#isTypeOf(java.lang.Object)
     */
    @Override
    public boolean isTypeOf(Object value) {
        Class<?> valueClass = value.getClass();
        if (!valueClass.isArray())
            return false;

        return containedType.isAssignableFrom(TypeFactory.create(valueClass.getComponentType()));
    }

    /**
     * @see tendril.codegen.field.type.Type#isAssignableFrom(tendril.codegen.field.type.Type)
     */
    @Override
    public boolean isAssignableFrom(Type other) {
        if (!(other instanceof ArrayType))
            return containedType.isAssignableFrom(other);

        return containedType.isAssignableFrom(((ArrayType<?>)other).getContainedType());
    }

    /**
     * @see tendril.codegen.field.type.Type#asValue(java.lang.Object)
     */
    @Override
    public JValue<?, ?> asValue(Object value) {
        if (isTypeOf(value))
            return JValueFactory.createArray(ArrayConverter.toObjectArray(value));
        
        throw new DefinitionException(containedType, "Incompatible value, expected " + getSimpleName() + " but received " + value);
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ArrayType))
            return false;
        
        return containedType.equals(((ArrayType<?>) obj).containedType);
    }

    /**
     * @see tendril.codegen.field.type.Type#asClassType()
     */
    @Override
    public ClassType asClassType() {
        return new ClassType(Array.class);
    }
    
}
