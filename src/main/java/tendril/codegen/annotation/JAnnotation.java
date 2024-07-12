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

import java.lang.annotation.Annotation;
import java.util.Set;

import tendril.codegen.BaseElement;
import tendril.codegen.CodeBuilder;
import tendril.dom.type.core.ClassType;

/**
 * Abstract annotation that covers the common features for all types of annotations
 */
public abstract class JAnnotation extends BaseElement {
    /** The class that is to be imported for this annotation */
    private final ClassType annotationClass;

    /**
     * CTOR
     * 
     * @param klass {@link Class} extending {@link Annotation} defining the annotation
     */
    public JAnnotation(Class<? extends Annotation> klass) {
        super("@" + klass.getSimpleName());
        this.annotationClass = new ClassType(klass);
    }
    
    /**
     * @see tendril.codegen.BaseElement#generate(tendril.codegen.CodeBuilder, java.util.Set)
     */
    @Override
    public void generate(CodeBuilder builder, Set<ClassType> classImports) {
        classImports.add(annotationClass);
        generateSelf(builder, classImports);
    }
}
