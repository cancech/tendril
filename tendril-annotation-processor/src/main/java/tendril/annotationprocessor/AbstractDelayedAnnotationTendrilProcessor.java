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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import tendril.annotationprocessor.element.ElementLoader;
import tendril.annotationprocessor.exception.MissingAnnotationException;
import tendril.annotationprocessor.exception.ProcessingException;
import tendril.codegen.field.type.ClassType;

/**
 * Annotation processor which acts upon annotation which are generated as a part of the annotation processing. This processing must be delayed until
 * the desired annotation is made available. This is not to be used for processing triggered by said annotation, but rather when processing something
 * else which also has this annotation applied. Processing will fail if attempting to perform processing on an element which has a generated tag, and
 * said tag does not exist yet. This will "detect" the presence of a non-existant tag and delay processing of affected elements until it is made
 * available.
 */
public abstract class AbstractDelayedAnnotationTendrilProcessor extends AbstractTendrilProccessor implements AnnotationGeneratedListener {
    /** The elements which are waiting for an annotation to become available */
    private final Map<String, List<WaitingElement>> delayedElements = new HashMap<>();
    
    /**
     * CTOR
     */
    public AbstractDelayedAnnotationTendrilProcessor() {
        ElementLoader.addListener(this);
    }

    /**
     * @see tendril.annotationprocessor.AnnotationGeneratedListener#annotationGenerated(tendril.codegen.field.type.ClassType)
     */
    @Override
    public void annotationGenerated(ClassType annotation) {
        // Make sure that there is something waiting for this annotation
        List<WaitingElement> canProcessNow = delayedElements.remove(annotation.getSimpleName());
        if (canProcessNow == null)
            return;
        
        // Try re-processing all elements which were waiting
        for (WaitingElement delayed: canProcessNow) {
            try {
                processElement(delayed.waitingAnnotation, delayed.waitingElement);
            } catch (MissingAnnotationException ex) {
                // This should never be reached...
                throw new ProcessingException("Fatal error encountered", ex);
            }
        }
    }
    
    /**
     * @see tendril.annotationprocessor.AbstractTendrilProccessor#processElement(javax.lang.model.element.TypeElement, javax.lang.model.element.Element)
     */
    @Override
    protected void processElement(TypeElement annotation, Element element) throws MissingAnnotationException {
        try {
            super.processElement(annotation, element);
        } catch (MissingAnnotationException ex) {
            // If processing fails due to a missing annotation, save it, so that it can be re-tried once the annotation is generated
            String missingAnnotation = ex.getMissingAnnotationName();
            if (!delayedElements.containsKey(missingAnnotation))
                delayedElements.put(missingAnnotation, new ArrayList<>());
            delayedElements.get(missingAnnotation).add(new WaitingElement(annotation, element));
        }
    }

    /**
     * Helper for associating an element which must wait for processing, with the annotation which triggered said processing
     */
    private class WaitingElement {
        /** The annotation which triggered processing */
        private final TypeElement waitingAnnotation;
        /** The element which must wait before it can be processed */
        private final Element waitingElement;
        
        /**
         * CTOR
         * 
         * @param waitingAnnotation {@link TypeElement} for the annotation which triggered the processing
         * @param waitingElement {@link Element} which was attempted to be processed, but must now wait
         */
        WaitingElement(TypeElement waitingAnnotation, Element waitingElement) {
            this.waitingAnnotation = waitingAnnotation;
            this.waitingElement = waitingElement;
        }
    }
}
