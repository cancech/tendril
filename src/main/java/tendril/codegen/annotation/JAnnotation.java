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
package tendril.codegen.annotation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import tendril.codegen.CodeBuilder;
import tendril.codegen.JBase;
import tendril.codegen.classes.JClassAnnotation;
import tendril.codegen.classes.method.JMethod;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.value.JValue;
import tendril.util.TendrilStringUtil;

/**
 * Representation of an annotation that is applied to an element in code.
 * 
 * Note: {@link JClassAnnotation} represents the definition of an annotation
 */
public class JAnnotation extends JBase {

    /** The parameters (methods) that have been applied to the annotation */
    private final List<JMethod<?>> parameters = new ArrayList<>();
    /** The values that have been applied to the parameters (methods) of the annotation */
    private final Map<JMethod<?>, JValue<?, ?>> values = new HashMap<>();

    /** The class that is to be imported for this annotation */
    private final ClassType annotationClass;

    /**
     * CTOR
     * 
     * @param classType {@link ClassType} representing the class of the annotation
     */
    public JAnnotation(ClassType classType) {
        super("@" + classType.getClassName());
        this.annotationClass = classType;
    }
    
    /**
     * Get the type of class defining this annotation
     * 
     * @return {@link ClassType}
     */
    public ClassType getType() {
        return annotationClass;
    }

    /**
     * Add the defined parameter with value to the Annotation instance
     * 
     * @param parameter {@link JMethod} representing the parameter method
     * @param value     {@link ValueElement} representing the value applied to the parameter
     */
    public void addParameter(JMethod<?> parameter, JValue<?, ?> value) {
        parameters.add(parameter);
        values.put(parameter, value);
    }

    /**
     * Get all of the parameters specified for the Annotation
     * 
     * @return {@link List} of {@link JMethod} representing the specified parameters
     */
    public List<JMethod<?>> getParameters() {
        return parameters;
    }

    /**
     * Get the value for the indicated parameter
     * 
     * @param parameter {@link JMethod} representing the desired parameter
     * @return {@link ValueElement} containing the applied value
     */
    public JValue<?, ?> getValue(JMethod<?> parameter) {
        return values.get(parameter);
    }

    /**
     * @see tendril.codegen.JBase#generate(tendril.codegen.CodeBuilder, java.util.Set)
     */
    @Override
    public void generate(CodeBuilder builder, Set<ClassType> classImports) {
        classImports.add(annotationClass);
        generateSelf(builder, classImports);
    }

    /**
     * @see tendril.codegen.JBase#generateSelf(tendril.codegen.CodeBuilder, java.util.Set)
     */
    @Override
    protected void generateSelf(CodeBuilder builder, Set<ClassType> classImports) {
        if (parameters.isEmpty())
            generateMarker(builder);
        else if (parameters.size() == 1 && parameters.get(0).getName().equals("value"))
            generateDefaultValue(builder, classImports);
        else
            generateFull(builder, classImports);
    }

    /**
     * Generate the code for a marker annotation (has no values)
     * 
     * @param builder {@link CodeBuilder} where the code is being assembled
     */
    private void generateMarker(CodeBuilder builder) {
        builder.append(name);
    }

    /**
     * Generate the code for an annotation with a single default value
     * 
     * @param builder      {@link CodeBuilder} where the code is being assembled
     * @param classImports {@link Set} of {@link ClassType} where the imports for the overall class are being assembled
     */
    private void generateDefaultValue(CodeBuilder builder, Set<ClassType> classImports) {
        builder.append(name + "(" + values.get(parameters.get(0)).generate(classImports) + ")");
    }

    /**
     * Generate the code for an annotation arbitrary parameters
     * 
     * @param builder      {@link CodeBuilder} where the code is being assembled
     * @param classImports {@link Set} of {@link ClassType} where the imports for the overall class are being assembled
     */
    private void generateFull(CodeBuilder builder, Set<ClassType> classImports) {
        String code = name + "(";
        code += TendrilStringUtil.join(parameters, p -> p.getName() + " = " + values.get(p).generate(classImports));
        builder.append(code + ")");
    }
}
