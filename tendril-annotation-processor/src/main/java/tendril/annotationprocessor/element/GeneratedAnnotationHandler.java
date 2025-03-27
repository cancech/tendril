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
package tendril.annotationprocessor.element;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.AnnotationMirror;

import tendril.annotationprocessor.AnnotationGeneratedListener;
import tendril.annotationprocessor.GeneratedAnnotationLoader;
import tendril.codegen.annotation.JAnnotation;
import tendril.codegen.field.type.ClassType;

/**
 * Handler for generated annotations. This does not perform the actual generation, but rather acts as a middle-man through which
 * those who perform the generating (or more specifically those who find the new annotations after they have been generated) and
 * those who want to perform processing using said annotations can interface. It "merely" allows this interface to take place. 
 */
public class GeneratedAnnotationHandler {
    /** List of loaders for generated annotations */
    private final List<GeneratedAnnotationLoader> generatedAnnotationLoaders = new ArrayList<>();
    /** List of listeners to notify when an annotation has been generated */
    private final List<AnnotationGeneratedListener> generatedAnnotationListeners = new ArrayList<>();

    /**
     * Hidden CTOR
     */
    GeneratedAnnotationHandler() {
    }
    
    /**
     * Add a new annotation loader
     * 
     * @param loader {@link GeneratedAnnotationLoader} to add
     */
    public void registerLoader(GeneratedAnnotationLoader loader) {
        generatedAnnotationLoaders.add(loader);
        generatedAnnotationListeners.forEach(listener -> loader.addListener(listener));
    }

    /**
     * Add a listener to be notified when an annotation has been generated
     * 
     * @param listener {@link AnnotationGeneratedListener} to add
     */
    public void addListener(AnnotationGeneratedListener listener) {
        generatedAnnotationListeners.add(listener);
        generatedAnnotationLoaders.forEach(loader -> loader.addListener(listener));
    }

    /**
     * Try to get an instance of an annotation via annotations that were generated during annotation processing
     * 
     * @param annonType   {@link ClassType} of the annotation
     * @param annonMirror {@link AnnotationMirror} describing the instance
     * @return {@link JAnnotation} with the details of the annotation instance
     */
    JAnnotation getAnnotationInstance(ClassType annonType, AnnotationMirror annonMirror) {
        for (GeneratedAnnotationLoader l : generatedAnnotationLoaders) {
            JAnnotation annon = l.getAnnotationInstance(annonType, annonMirror);
            if (annon != null) {
                return annon;
            }
        }

        return null;
    }
}
