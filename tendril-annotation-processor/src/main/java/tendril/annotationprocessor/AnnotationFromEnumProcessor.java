/*
 * Copyright 2025 Jaroslav Bosak
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
package tendril.annotationprocessor;

import java.lang.annotation.Annotation;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

import tendril.annotationprocessor.exception.InvalidConfigurationException;
import tendril.annotationprocessor.exception.InvalidTypeException;
import tendril.annotationprocessor.exception.TendrilException;

/**
 * Base processor which can be used to generate annotations from values defined in an {@link Enum} 
 */
public abstract class AnnotationFromEnumProcessor extends AbstractTendrilProccessor {
    /** The name of the annotation which the processor is looking for */
    private final String annotationName;

    /**
     * CTOR
     * 
     * @param processorAnnotation {@link Class} of the {@link Annotation} that the processor is operating on
     */
    public AnnotationFromEnumProcessor(Class<? extends Annotation> processorAnnotation) {
        this.annotationName = processorAnnotation.getName();
    }

    /**
     * The annotated {@link TypeElement} must be an {@link Enum}
     * 
     * @see tendril.annotationprocessor.AbstractTendrilProccessor#validateType(javax.lang.model.element.TypeElement)
     */
    @Override
    protected void validateType(TypeElement type) throws TendrilException {
        if (type.getKind() != ElementKind.ENUM)
            throw new InvalidTypeException("Unable to use " + type.getQualifiedName() + " - Must be an enum");
    }
    
    /**
     * @see tendril.annotationprocessor.AbstractTendrilProccessor#processMethod()
     */
    @Override
    protected ClassDefinition processMethod() throws TendrilException {
        throw new InvalidConfigurationException(currentClassType.getFullyQualifiedName() + "::" + currentMethod.getName() +
                annotationName + " - cannot be applied to a method");
    }
    
}
