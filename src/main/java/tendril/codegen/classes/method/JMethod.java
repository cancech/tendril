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
package tendril.codegen.classes.method;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import tendril.codegen.CodeBuilder;
import tendril.codegen.VisibilityType;
import tendril.codegen.field.NamedType;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.Type;
import tendril.util.TendrilStringUtil;

/**
 * Representation of a method
 * 
 * @param <RETURN_TYPE> the {@link Type} representing what the method returns
 */
public abstract class JMethod<RETURN_TYPE extends Type> extends NamedType<Type> {

    /** List of parameters that the method takes */
    private final List<NamedType<?>> parameters = new ArrayList<>();

    /** The visibility of the method */
    protected final VisibilityType visibility;
    /** The lines of code that build up the implementation of the method */
    protected final List<String> implementation;

    /**
     * CTOR
     * 
     * @param visibility     {@link VisibilityType} indicating the desired visibility of the method
     * @param returnType     RETURN_TYPE representing what the method returns
     * @param name           {@link String} the name of the method
     * @param implementation {@link List} of {@link String} lines of code with the implementation of the method
     */
    protected JMethod(VisibilityType visibility, RETURN_TYPE returnType, String name, List<String> implementation) {
        super(returnType, name);
        this.visibility = visibility;
        this.implementation = implementation;
    }

    /**
     * Add a parameter to the method. Parameters are expected to be added in the order they appear in the method.
     * 
     * @param parameter {@link NamedType} representing the method parameter
     */
    public void addParameter(NamedType<?> parameter) {
        parameters.add(parameter);
    }

    /**
     * Get all of the parameters of the method
     * 
     * @return {@link List} of {@link NamedType}s representing the parameters
     */
    public List<NamedType<?>> getParameters() {
        return parameters;
    }

    /**
     * @see tendril.codegen.JBase#generateSelf(tendril.codegen.CodeBuilder, java.util.Set)
     */
    @Override
    protected void generateSelf(CodeBuilder builder, Set<ClassType> classImports) {
        getType().registerImport(classImports);

        boolean hasImplementation = implementation != null;
        builder.append(generateSignature(hasImplementation));

        if (hasImplementation) {
            builder.indent();
            for (String s : implementation)
                builder.append(s);
            builder.deIndent();
            builder.append("}");
        }
    }

    /**
     * Generate the full method signature
     * 
     * @param hasImplementation boolean true if the method has an implementation provided
     * @return {@link String}
     */
    private String generateSignature(boolean hasImplementation) {
        StringBuilder signature = new StringBuilder(generateSignatureStart(hasImplementation));
        signature.append(getType().getSimpleName() + " " + getName());
        signature.append("(" + generateParameters() + ")");
        signature.append(hasImplementation ? " {" : ";");
        return signature.toString();
    }

    /**
     * Generate the code for the parameters of the method
     * 
     * @return {@link String} containing the details of the parameters
     */
    private String generateParameters() {
        return TendrilStringUtil.join(parameters, param -> {
            return param.getType().getSimpleName() + " " + param.getName();
        });
    }

    /**
     * Generate the start of the method signature (start until the method return type)
     * 
     * @param hasImplementation boolean true if the method has an implementation present
     * @return {@link String} the code for the start of the method signature.
     */
    protected abstract String generateSignatureStart(boolean hasImplementation);
}
