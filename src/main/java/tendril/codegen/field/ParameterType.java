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

import tendril.codegen.CodeBuilder;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.Type;

/**
 * Representation of parameters (i.e.: type with a name)
 * 
 * @param <DATA_TYPE> representing the {@link Type} of the parameter
 */
public class ParameterType<DATA_TYPE extends Type> extends NamedType<DATA_TYPE> {

    /**
     * CTOR
     * 
     * @param type DATA_TYPE indicating the type of value stored in the parameter
     * @param name {@link String} the name of the parameter
     */
    public ParameterType(DATA_TYPE type, String name) {
        super(type, name);
    }

    /**
     * @see tendril.codegen.JBase#generateSelf(tendril.codegen.CodeBuilder, java.util.Set)
     */
    @Override
    protected void generateSelf(CodeBuilder builder, Set<ClassType> classImports) {
        type.registerImport(classImports);
        builder.append(type.getSimpleName() + " " + name);
    }

}
