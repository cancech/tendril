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
package tendril.codegen.field.value;

import java.util.Set;

import tendril.codegen.classes.EnumerationEntry;
import tendril.codegen.field.type.ClassType;

/**
 * Value that contains a specific enum entry
 */
public class JValueEnum extends JValue<ClassType, EnumerationEntry> {

    /**
     * CTOR
     * 
     * @param value {@link Enum} to store
     */
    JValueEnum(Enum<?> value) {
        this(EnumerationEntry.from(value));
    }

    /**
     * CTOR
     * 
     * @param classType {@link ClassType} representing the enumeration class
     * @param value {@link Enum} to store
     */
    JValueEnum(ClassType classType, Enum<?> value) {
        this(EnumerationEntry.from(classType, value));
    }
    
    /**
     * CTOR
     * 
     * @param entry {@link EnumerationEntry} to store
     */
    JValueEnum(EnumerationEntry entry) {
        super(entry.getEnclosingClass(), entry);
    }

    /**
     * @see tendril.codegen.field.value.JValue#generate(java.util.Set)
     */
    @Override
    public String generate(Set<ClassType> classImports) {
        classImports.add(type);
        return type.getSimpleName() + "." + value.getName();
    }
}
