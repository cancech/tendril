/*
 * Copyright 2025 Jaroslav Bosak
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

import java.util.ArrayList;
import java.util.List;

import tendril.codegen.field.value.JValue;

/**
 * Builder for creating {@link Enum} enumerations. Intended to be used hand-in-hand with {@link EnumClassBuilder}.
 */
public class EnumerationBuilder {
    /** The name of the enumeration */
    private final String name;
    /** List of parameters the enumeration is to take */
    protected final List<JValue<?, ?>> parameters = new ArrayList<>();
    /** The classbuilder for which the enumeration is being built */
    private final ClassBuilder classBuilder;

    /**
     * CTOR
     * 
     * @param classBuilder {@link ClassBuilder} for which the enumeration is being built
     * @param name {@link String} the name of the enumeration
     */
    EnumerationBuilder(ClassBuilder classBuilder, String name) {
        this.classBuilder = classBuilder;
        this.name = name;
    }
    
    /**
     * Add a parameter to the enumeration
     * 
     * @param values {@link JValue}... of values to add to the initialization of the enumeration
     * @return {@link EnumerationBuilder}
     */
    public EnumerationBuilder addParameter(JValue<?, ?>...values) {
        for (JValue<?, ?> v: values)
            parameters.add(v);
        return this;
    }
    
    /**
     * Build the enumeration and add it to the {@link ClassBuilder}.
     * 
     * @return {@link ClassBuilder}
     */
    public ClassBuilder build() {
        classBuilder.add(new EnumerationEntry(name, parameters));
        return classBuilder;
    }
}
