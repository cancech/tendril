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
    /** The name of the entry (i.e.: how it is displayed)*/
    private final String name;
    /** The parameters that it takes for its constructions */
    private final List<JValue<?, ?>> parameters;
    
    /**
     * CTOR
     * 
     * @param name {@link String} the name of the entry
     * @param parameters {@link List} of {@link JValue}s representing what is passed to the constructor as parameters
     */
    EnumerationEntry(String name, List<JValue<?, ?>> parameters) {
        this.name = name;
        this.parameters = parameters;
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
     * @param builder {@link CodeBuilder} where it is to be appended to
     * @param classImports {@link Set} of {@link ClassType} representing the imports for the code
     * @param terminator {@link String} to apply as the terminator for the entry
     */
    void generateSelf(CodeBuilder builder, Set<ClassType> classImports, String terminator) {
        String line = name;
        if(!parameters.isEmpty())
            line += "(" + TendrilStringUtil.join(parameters, (p) -> p.generate(classImports)) + ")";
        builder.append(line + terminator);
    }
}
