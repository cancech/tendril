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

import tendril.codegen.CodeBuilder;
import tendril.codegen.annotation.JAnnotation;
import tendril.codegen.field.JContainedType;
import tendril.codegen.field.type.Type;
import tendril.util.TendrilStringUtil;

/**
 * Representation of parameters (i.e.: type with a name). Use {@link ParameterBuilder} to create instances.
 * 
 * @param <DATA_TYPE> representing the {@link Type} of the parameter
 */
public class JParameter<DATA_TYPE extends Type> extends JContainedType<DATA_TYPE> {

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
     * @see tendril.codegen.JBase#generate(tendril.codegen.CodeBuilder)
     */
    @Override
    public void generate(CodeBuilder builder) {
        // Parameters place annotations as a prefix on the same line, meaning that they are included in generateSelf
        appendSelf(builder);
    }

    /**
     * @see tendril.codegen.JBase#appendSelf(tendril.codegen.CodeBuilder)
     */
    @Override
    protected void appendSelf(CodeBuilder builder) {
        builder.append(generateSelf());
    }

    /**
     * @see tendril.codegen.JBase#generateSelf()
     */
    @Override
    public String generateSelf() {
        List<JAnnotation> appliedAnnotations = getAnnotations();
        String prefix = "";
        if (!appliedAnnotations.isEmpty()) {
            prefix = TendrilStringUtil.join(appliedAnnotations, " ", anno -> anno.generateSelf());
            prefix += " ";
        }

        return prefix + getFinalKeyword() + type.getCodeName() + getGenericsApplicationKeyword(true) + name;
    }

}
