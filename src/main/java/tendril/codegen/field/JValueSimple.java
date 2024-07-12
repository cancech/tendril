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
 * Representation of simple values (plain ol' data types, {@link String}s), where the value is presented verbatim and at most some decoration (prefix, suffix).
 */
public class JValueSimple<T> extends JValue<T> {
    
    private final String prefix;
    private final String suffix;

    /**
     * @param value
     */
    public JValueSimple(T value) {
        this(value, "", "");
    }
    
    public JValueSimple(T value, String prefix, String suffix) {
        super(value);
        this.prefix = prefix;
        this.suffix = suffix;
    }

    /**
     * @see tendril.codegen.field.JValue#generate(java.util.Set)
     */
    @Override
    public String generate(Set<ClassType> classImports) {
        return prefix + value.toString() + suffix;
    }

}
