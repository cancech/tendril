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

import tendril.dom.type.core.ClassType;
import tendril.dom.type.core.PoDType;
import tendril.dom.type.core.VoidType;

/**
 * Factory to facilitate the creation of {@link TypeData} instances
 */
public class TypeDataFactory {

    /**
     * Creates a void {@link TypeData}
     * 
     * @return {@link TypeData}
     */
    public static TypeData<VoidType> create() {
        return new TypeDataVoid();
    }

    /**
     * Creates a {@link TypeData} for a class or other declared type
     * 
     * @param type {@link Class} representing the declared type
     * @return {@link TypeData}
     */
    public static TypeData<ClassType> create(Class<?> type) {
        return new TypeDataDeclared(type);
    }

    /**
     * Creates a {@link TypeData} for a class or other declared type
     * 
     * @param type {@link ClassType} representing the declared type
     * @return {@link TypeData}
     */
    public static TypeData<ClassType> create(ClassType type) {
        return new TypeDataDeclared(type);
    }

    /**
     * Creates a {@link TypeData} for a plain ol' data type
     * 
     * @param type {@link PoDType} representing the POD type
     * @return {@link TypeData}
     */
    public static TypeData<PoDType> create(PoDType type) {
        return new TypeDataPoD(type);
    }
}
