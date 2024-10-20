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
package tendril.codegen;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import tendril.codegen.annotation.JAnnotation;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.generics.GenericFactory;
import tendril.codegen.generics.GenericType;

/**
 * The base of any element that is to be part of the generated code.
 */
public abstract class JBase {
    /** The name of the element */
    protected final String name;
    /** List of annotations that are applied to the element */
    private final List<JAnnotation> annotations = new ArrayList<>();
    /** Flag for whether the element is final */
    private boolean isFinal = false;
    private GenericType generic = GenericFactory.createNoGeneric(); 

    /**
     * CTOR
     * 
     * @param name {@link String} the name of the element
     */
    protected JBase(String name) {
        this.name = name;
    }

    /**
     * Mark the element as final
     * 
     * @param isFinal boolean true if it should be final
     */
    public void setFinal(boolean isFinal) {
        this.isFinal = isFinal;
    }

    /**
     * Check if the element is final
     * 
     * @return boolean true if it is final
     */
    public boolean isFinal() {
        return isFinal;
    }

    /**
     * Get the name of the element
     * 
     * @return {@link String}
     */
    public String getName() {
        return name;
    }

    /**
     * Add an annotation to the element
     * 
     * @param annotation {@link JAnnotation} representing the annotation to apply
     */
    public void addAnnotation(JAnnotation annotation) {
        annotations.add(annotation);
    }

    /**
     * Get all applied annotations
     * 
     * @return {@link List} of {@link JAnnotation} that have been applied to the item
     */
    public List<JAnnotation> getAnnotations() {
        return annotations;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }
    
    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof JBase))
            return false;

        return name.equals(((JBase) obj).name);
    }

    /**
     * Generate the code for the element. Performs the common code generation, relying on {@code generateSelf()} to perform the specific code generation for this specific element.
     * 
     * @param builder      {@link CodeBuilder} which is assembling/building the code
     * @param classImports {@link Set} of {@link ClassType}s representing the imports for the code
     */
    public void generate(CodeBuilder builder, Set<ClassType> classImports) {
        for (JAnnotation annon : annotations)
            annon.generate(builder, classImports);
        appendSelf(builder, classImports);
    }

    /**
     * Generate the appropriate code that is specific and unique to this element. {@code generate()} takes care of the common portions of code generation, with this method performing what is unique to
     * this particular element. Can be thought of an as wrapper for generating the code and appending it directly to the larger code.
     * 
     * @param builder      {@link CodeBuilder} which is assembling/building the code
     * @param classImports {@link Set} of {@link ClassType}s representing the imports for the code
     */
    protected abstract void appendSelf(CodeBuilder builder, Set<ClassType> classImports);

    /**
     * Generate the appropriate code that is specific and unique to this element. Regardless of where this elements fits into the larger code, produce a {@link String} which is representative of this
     * element.
     * 
     * @param classImports {@link Set} of {@link ClassType}s representing the imports for the code
     * @return {@link String} code representing this element
     */
    public abstract String generateSelf(Set<ClassType> classImports);
}
