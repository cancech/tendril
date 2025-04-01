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

import java.util.ArrayList;
import java.util.List;

import tendril.annotationprocessor.element.ClassConverter;
import tendril.annotationprocessor.element.ElementLoader;
import tendril.annotationprocessor.exception.ProcessingException;

/**
 * A processor whose role is not to generate any code, but to load details of annotations which have been generated elsewhere within the processor.
 * The concept is that generated annotations have an annotation applied to them, which can then be "waited" upon by a concrete implementation of this
 * processor. Once it has been loaded, notify that the annotation has been made available, and then allow for processing on said annotation to go
 * ahead.
 * 
 * Thus, the concrete processor (extending this) is to process the annotation which is applied to the generated annotation. The details of the annotation are
 * then loaded and can be accessed via {@code getAnnotationInstance()}. {@link ClassConverter} will make this call automatically, when there is an annotation
 * which cannot be understood (i.e.: it has no immediate implementation). It will assume that any unknown annotation will be generated later, with a concrete
 * AnnotationLoaderProcessor at hand to handle the generated annotation.
 */
public abstract class AnnotationLoaderProcessor extends AbstractTendrilProccessor implements GeneratedAnnotationLoader {
    /** Listeners to notify when an annotation has been generated */
    private final List<AnnotationGeneratedListener> listeners = new ArrayList<>();

    /**
     * CTOR
     */
    public AnnotationLoaderProcessor() {
        ElementLoader.getGeneratedAnnotationHandler().registerLoader(this);
    }
    
    /**
     * @see tendril.annotationprocessor.AbstractTendrilProccessor#processType()
     */
    @Override
    protected ClassDefinition processType() {
        listeners.forEach(listener -> listener.annotationGenerated(currentClassType));
        return null;
    }

    /**
     * @see tendril.annotationprocessor.AbstractTendrilProccessor#processMethod()
     */
    @Override
    protected ClassDefinition processMethod() {
        throw new ProcessingException("An annotation cannot be a method");
    }

    /**
     * @see tendril.annotationprocessor.GeneratedAnnotationLoader#addListener(tendril.annotationprocessor.AnnotationGeneratedListener)
     */
    @Override
    public void addListener(AnnotationGeneratedListener listener) {
        listeners.add(listener);
    }
}
