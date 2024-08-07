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
package tendril.codegen.classes;

import java.util.List;
import java.util.Set;

import tendril.codegen.CodeBuilder;
import tendril.codegen.annotation.JAnnotation;
import tendril.codegen.field.JType;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.Type;
import tendril.util.TendrilStringUtil;

/**
 * Representation of parameters (i.e.: type with a name). Use {@link ParameterBuilder} to create instances.
 * 
 * @param <DATA_TYPE> representing the {@link Type} of the parameter
 */
public class JParameter<DATA_TYPE extends Type> extends JType<DATA_TYPE> {

    /**
     * CTOR
     * 
     * @param type DATA_TYPE indicating the type of value stored in the parameter
     * @param name {@link String} the name of the parameter
     */
    JParameter(DATA_TYPE type, String name) {
        super(type, name);
    }
    
    /**
     * @see tendril.codegen.JBase#generate(tendril.codegen.CodeBuilder, java.util.Set)
     */
    @Override
    public void generate(CodeBuilder builder, Set<ClassType> classImports) {
        // Parameters place annotations as a prefix on the same line, meaning that they are included in generateSelf
        appendSelf(builder, classImports);
    }

    /**
     * @see tendril.codegen.JBase#appendSelf(tendril.codegen.CodeBuilder, java.util.Set)
     */
    @Override
    protected void appendSelf(CodeBuilder builder, Set<ClassType> classImports) {
        builder.append(generateSelf(classImports));
    }
    
    /**
     * @see tendril.codegen.JBase#generateSelf(java.util.Set)
     */
    @Override
    public String generateSelf(Set<ClassType> classImports) {
        type.registerImport(classImports);
        
        List<JAnnotation> appliedAnnotations = getAnnotations();
        String prefix = "";
        if (!appliedAnnotations.isEmpty()) {
            prefix = TendrilStringUtil.join(appliedAnnotations, " ", anno -> anno.generateSelf(classImports));
            prefix += " ";
        }
            
        return prefix + type.getSimpleName() + " " + name;
    }

}
