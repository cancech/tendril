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

import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.Type;

/**
 * Representation of simple values (primitives, {@link String}s), where the value is presented verbatim and at most some decoration (prefix, suffix).
 * 
 * @param <DATA_TYPE> representing the {@link Type} of the value
 * @param <VALUE_TYPE> the specific (Java) type storing the value
 */
public class JValueSimple<DATA_TYPE extends Type, VALUE_TYPE> extends JValue<DATA_TYPE, VALUE_TYPE> {

    /** The prefix to place in front of the value when generating the code */ 
    private final String prefix;
    /** The suffix to place after the value when generating the code */
    private final String suffix;

    /**
     * CTOR with an empty prefix and suffix
     * 
     * @param dataType DATA_TYPE representing what type of value is stored
     * @param value    T the value to store
     */
    public JValueSimple(DATA_TYPE dataType, VALUE_TYPE value) {
        this(dataType, value, "", "");
    }

    /**
     * CTOR
     * 
     * @param dataType DATA_TYPE representing what type of value is stored
     * @param value    T the value to store
     * @param prefix   {@link String} prefix to add before the value when generating it as code
     * @param suffix   {@link String} suffix to add after the value when generating it as code
     */
    public JValueSimple(DATA_TYPE dataType, VALUE_TYPE value, String prefix, String suffix) {
        super(dataType, value);
        this.prefix = prefix;
        this.suffix = suffix;
    }

    /**
     * @see tendril.codegen.field.value.JValue#generate(java.util.Set)
     */
    @Override
    public String generate(Set<ClassType> classImports) {
        return prefix + value.toString() + suffix;
    }

}
