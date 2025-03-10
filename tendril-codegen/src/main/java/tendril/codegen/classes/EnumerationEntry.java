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

import java.util.Collections;
import java.util.List;
import java.util.Set;

import tendril.codegen.CodeBuilder;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.value.JValue;
import tendril.util.TendrilStringUtil;

/**
 * Representation of a single entry in an enumeration
 */
public class EnumerationEntry {
    /** The class in which the entry is contained */
    private final ClassType enclosingClass;
    /** The name of the entry (i.e.: how it is displayed) */
    private final String name;
    /** The parameters that it takes for its constructions */
    private final List<JValue<?, ?>> parameters;

    /**
     * Create and entry from an existing enum entry
     * 
     * @param value {@link Enum} entry from an existing enumeration
     * @return {@link EnumerationEntry}
     */
    public static EnumerationEntry from(Enum<?> value) {
        return from(new ClassType(value.getClass()), value);
    }

    /**
     * Create and entry from an existing enum entry
     * 
     * @param type  {@link ClassType} describing where the entry comes from
     * @param value {@link Enum} entry from an existing enumeration
     * @return {@link EnumerationEntry}
     */
    public static EnumerationEntry from(ClassType type, Enum<?> value) {
        return new EnumerationEntry(type, value.name(), Collections.emptyList());
    }

    /**
     * CTOR
     * 
     * @param enclosingClass {@link ClassType} in which the entry is contained
     * @param name           {@link String} the name of the entry
     * @param parameters     {@link List} of {@link JValue}s representing what is passed to the constructor as parameters
     */
    EnumerationEntry(ClassType enclosingClass, String name, List<JValue<?, ?>> parameters) {
        this.enclosingClass = enclosingClass;
        this.name = name;
        this.parameters = parameters;
    }

    /**
     * Get the class in which the entry is contained
     *
     * @return {@link ClassType}
     */
    public ClassType getEnclosingClass() {
        return enclosingClass;
    }

    /**
     * Get the name of the entry
     * 
     * @return {@link String} name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the parameters for the entry
     * 
     * @return {@link List} of {@link JValue} representing the parameter values
     */
    public List<JValue<?, ?>> getParameters() {
        return parameters;
    }

    /**
     * Generate the text for representing the entry in code
     * 
     * @param builder      {@link CodeBuilder} where it is to be appended to
     * @param classImports {@link Set} of {@link ClassType} representing the imports for the code
     * @param terminator   {@link String} to apply as the terminator for the entry
     */
    void generateSelf(CodeBuilder builder, Set<ClassType> classImports, String terminator) {
        String line = name;
        if (!parameters.isEmpty())
            line += "(" + TendrilStringUtil.join(parameters, (p) -> p.generate(classImports)) + ")";
        builder.append(line + terminator);
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof EnumerationEntry))
            return false;

        EnumerationEntry other = (EnumerationEntry) obj;
        return name.equals(other.name) && enclosingClass.equals(other.enclosingClass);
    }
}
