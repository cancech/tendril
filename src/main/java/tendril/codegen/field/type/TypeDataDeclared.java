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

import tendril.dom.type.core.ClassType;
import tendril.dom.type.value.ValueElement;

/**
 * {@link TypeData} for a declared element (i.e.: class)
 */
class TypeDataDeclared extends TypeData<ClassType> {

    /**
     * CTOR
     * 
     * @param type {@link Class} which the type represents
     */
    TypeDataDeclared(Class<?> type) {
        this(new ClassType(type));
    }

    /**
     * CTOR
     * 
     * @param type {@link ClassType} which the type represents
     */
    protected TypeDataDeclared(ClassType type) {
        super(type, type.getClassName());
    }

    /**
     * @see tendril.codegen.field.type.TypeData#registerImport(java.util.Set)
     */
    @Override
    public void registerImport(Set<ClassType> classImports) {
        classImports.add(getDataType());
    }

    /**
     * @see tendril.codegen.field.type.TypeData#asValue(java.lang.Object)
     */
    @Override
    public ValueElement<ClassType, Object> asValue(Object value) {
        return new ValueElement<>(type, value);
    }
}
