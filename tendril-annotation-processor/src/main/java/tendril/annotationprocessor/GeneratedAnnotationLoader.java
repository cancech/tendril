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

import javax.annotation.processing.AbstractProcessor;
import javax.lang.model.element.AnnotationMirror;

import tendril.codegen.annotation.JAnnotation;
import tendril.codegen.field.type.ClassType;

/**
 * A loader (presumably in the form of an {@link AbstractProcessor}) which loads (new) annotations. This provides the means to both
 * try and retrieve annotations which may not (yet) be present in the classpath (i.e.: generated at some point during annotation
 * processing) and allow for the notification of listeners when an annotation is generated. 
 */
public interface GeneratedAnnotationLoader {
    /**
     * Try to get an annotation instance of the specified type, which derives from the {@link AnnotationMirror}. In this manner
     * the {@link ClassType} can be used to determine what form/shape the {@link JAnnotation} should take and the {@link AnnotationMirror}
     * the fields and like that are to be present.
     * 
     * @param type {@link ClassType} indicating the class of the annotation instance to be loaded
     * @param mirror {@link AnnotationMirror} containing the details of the instance
     * @return {@link JAnnotation} containing the full details of the annotation instance
     */
    JAnnotation getAnnotationInstance(ClassType type, AnnotationMirror mirror);
    
    /**
     * Add a listener to be notified when an annotation is generated
     * 
     * @param listener {@link AnnotationGeneratedListener}
     */
    void addListener(AnnotationGeneratedListener listener);
}
