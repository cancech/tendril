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

    /** The attributes (methods) that have been applied to the annotation */
    private final List<JMethod<?>> attributes = new ArrayList<>();
    /** The values that have been applied to the attributes (methods) of the annotation */
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
     * Add the defined attribute with value to the Annotation instance
     * 
     * @param attribute {@link JMethod} representing the attribute method
     * @param value     {@link JValue} representing the value applied to the attribute
     */
    public void addAttribute(JMethod<?> attribute, JValue<?, ?> value) {
        attributes.add(attribute);
        values.put(attribute, value);
    }

    /**
     * Get all of the attributes specified for the Annotation
     * 
     * @return {@link List} of {@link JMethod} representing the specified attributes
     */
    public List<JMethod<?>> getAttributes() {
        return attributes;
    }

    /**
     * Get the value for the indicated attribute
     * 
     * @param attribute {@link JMethod} representing the desired attribute
     * @return {@link JValue} containing the applied value
     */
    public JValue<?, ?> getValue(JMethod<?> attribute) {
        return values.get(attribute);
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
        if (attributes.isEmpty())
            generateMarker(builder);
        else if (attributes.size() == 1 && attributes.get(0).getName().equals("value"))
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
        builder.append(name + "(" + values.get(attributes.get(0)).generate(classImports) + ")");
    }

    /**
     * Generate the code for an annotation with arbitrary attributes
     * 
     * @param builder      {@link CodeBuilder} where the code is being assembled
     * @param classImports {@link Set} of {@link ClassType} where the imports for the overall class are being assembled
     */
    private void generateFull(CodeBuilder builder, Set<ClassType> classImports) {
        String code = name + "(";
        code += TendrilStringUtil.join(attributes, p -> p.getName() + " = " + values.get(p).generate(classImports));
        builder.append(code + ")");
    }
}
