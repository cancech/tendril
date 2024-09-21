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
import tendril.codegen.classes.JParameter;
import tendril.codegen.field.JType;
import tendril.codegen.field.JVisibleType;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.Type;
import tendril.util.TendrilStringUtil;

/**
 * Abstract representation of method-like elements that can appear in a class (i.e.: methods and constructors).
 */
public abstract class JAbstractMethodElement<RETURN_TYPE extends Type> extends JVisibleType<RETURN_TYPE> {

    /** List of parameters that the method takes */
    private final List<JParameter<?>> parameters = new ArrayList<>();

    /** The lines of code that build up the implementation of the method */
    private final List<String> implementation;
    
    /**
     * CTOR
     * 
     * @param returnType     RETURN_TYPE representing what the method returns
     * @param name           {@link String} the name of the method
     * @param implementation {@link List} of {@link String} lines of code with the implementation of the method
     */
    protected JAbstractMethodElement(RETURN_TYPE returnType, String name, List<String> implementation) {
        super(returnType, name);
        this.implementation = implementation;
    }

    /**
     * Add a parameter to the method. Parameters are expected to be added in the order they appear in the method.
     * 
     * @param parameter {@link JType} representing the method parameter
     */
    public void addParameter(JParameter<?> parameter) {
        parameters.add(parameter);
    }

    /**
     * Get all of the parameters of the method
     * 
     * @return {@link List} of {@link JType}s representing the parameters
     */
    public List<JParameter<?>> getParameters() {
        return parameters;
    }

    /**
     * @see tendril.codegen.JBase#appendSelf(tendril.codegen.CodeBuilder, java.util.Set)
     */
    @Override
    protected void appendSelf(CodeBuilder builder, Set<ClassType> classImports) {
        builder.appendMultiLine(generateSelf(classImports));
    }

    /**
     * @see tendril.codegen.JBase#generateSelf(java.util.Set)
     */
    @Override
    public String generateSelf(Set<ClassType> classImports) {
        CodeBuilder builder = new CodeBuilder();
        getType().registerImport(classImports);

        boolean hasImplementation = implementation != null;
        builder.append(generateSignature(classImports, hasImplementation));

        if (hasImplementation) {
            builder.indent();
            for (String s : implementation)
                builder.append(s);
            builder.deIndent();
            builder.append("}");
        }
        return builder.get();
    }
    
    /**
     * Generate the full method signature
     * 
     * @param hasImplementation boolean true if the method has an implementation provided
     * @return {@link String}
     */
    protected abstract String generateSignature(Set<ClassType> classImports, boolean hasImplementation);

    /**
     * Generate the code for the parameters of the method
     * 
     * @return {@link String} containing the details of the parameters
     */
    protected String generateParameters(Set<ClassType> classImports) {
        return TendrilStringUtil.join(parameters, param -> param.generateSelf(classImports));
    }
    
    /**
     * @see tendril.codegen.field.JVisibleType#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof JAbstractMethodElement))
            return false;
        
        JAbstractMethodElement<?> other = (JAbstractMethodElement<?>) obj;
        return super.equals(obj) && parameters.equals(other.parameters);
    }
}
