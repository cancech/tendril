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

import tendril.codegen.annotation.Annotatable;
import tendril.codegen.annotation.JAnnotation;
import tendril.codegen.field.type.ClassType;

/**
 * The base of any element that is to be part of the generated code.
 */
public abstract class JBase implements Annotatable {
    /** The name of the element */
    protected final String name;
    /** List of annotations that are applied to the element */
    private final List<JAnnotation> annotations = new ArrayList<>();

    /**
     * CTOR
     * 
     * @param name {@link String} the name of the element
     */
    protected JBase(String name) {
        this.name = name;
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
    @Override
    public void addAnnotation(JAnnotation annotation) {
        annotations.add(annotation);
    }

    /**
     * Get all applied annotations
     * 
     * @return {@link List} of {@link JAnnotation} that have been applied to the item
     */
    @Override
    public List<JAnnotation> getAnnotations() {
        return annotations;
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
        generateSelf(builder, classImports);
    }

    /**
     * Generate the appropriate code that is specific and unique to this element. {@code generate()} takes care of the common portions of code generation, with this method performing what is unique to
     * this particular element.
     * 
     * @param builder      {@link CodeBuilder} which is assembling/building the code
     * @param classImports {@link Set} of {@link ClassType}s representing the imports for the code
     */
    protected abstract void generateSelf(CodeBuilder builder, Set<ClassType> classImports);
}
