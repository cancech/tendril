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
package tendril.codegen.field;

import java.util.Set;

import tendril.dom.type.core.ClassType;

/**
 * Value that contains a specific enum entry
 * 
 * @param <E> {@link Enum} whose entry is to be stored
 */
public class JValueEnum<E extends Enum<E>> extends JValue<E> {

    /**
     * CTOR
     * 
     * @param value E specific enum entry to store
     */
    JValueEnum(E value) {
        super(value);
    }

    /**
     * @see tendril.codegen.field.JValue#generate(java.util.Set)
     */
    @Override
    public String generate(Set<ClassType> classImports) {
        classImports.add(new ClassType(value.getClass()));
        return value.getClass().getSimpleName() + "." + value.name();
    }
}