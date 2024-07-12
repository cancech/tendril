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
 * The base representation of a value
 * 
 * @param <TYPE> indicating the type of value that is stored within
 */
public abstract class JValue<TYPE> {
    /** The specific value */
    protected final TYPE value;

    /**
     * CTOR
     * 
     * @param value TYPE the value to store
     */
    protected JValue(TYPE value) {
        this.value = value;
    }

    /**
     * Generate a code representation of the value
     * 
     * @param classImports {@link Set} of {@link ClassType} where any imports for this value are to be registered
     * @return {@link String} code representing the value
     */
    public abstract String generate(Set<ClassType> classImports);
}
