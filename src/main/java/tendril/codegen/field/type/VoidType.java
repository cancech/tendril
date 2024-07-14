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

/**
 * Type that represents a void value. Intended to be only used with methods.
 */
public class VoidType implements Type {

    /** Singleton instance that should be used to avoid creating needless copies */
    public static final VoidType INSTANCE = new VoidType();

    /**
     * CTOR hidden so that it is only used as a singleton (no need to have more than one instance floating around).
     */
    private VoidType() {
    }

    /**
     * 
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof VoidType;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getSimpleName();
    }

    /**
     * Always returns false, as void cannot be assigned to/from anything
     * 
     * @see tendril.codegen.field.type.Type#isAssignableTo(tendril.codegen.field.type.Type)
     */
    @Override
    public boolean isAssignableTo(Type other) {
        return false;
    }

    /**
     * @see tendril.codegen.field.type.Type#isTypeOf(java.lang.Object)
     */
    @Override
    public boolean isTypeOf(Object value) {
        return false;
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
        return true;
    }

    /**
     * @see tendril.codegen.field.type.Type#getSimpleName()
     */
    @Override
    public String getSimpleName() {
        return "void";
    }

    /**
     * @see tendril.codegen.field.type.Type#asValue(java.lang.Object)
     */
    @Override
    public JValue<?, ?> asValue(Object value) {
        throw new IllegalArgumentException("A void type cannot hold any value");
    }
}
