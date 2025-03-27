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

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import org.apache.commons.lang3.tuple.Pair;

import tendril.annotationprocessor.exception.MissingAnnotationException;
import tendril.annotationprocessor.exception.ProcessingException;
import tendril.codegen.classes.JClass;
import tendril.codegen.classes.method.JMethod;
import tendril.codegen.field.JField;

/**
 * Middle-man through which to retrieve codegen representations of class (elements)
 */
public class ElementLoader {
    /** The handler for generated annotations */
    private static GeneratedAnnotationHandler annotationHandler = new GeneratedAnnotationHandler();
    /** The singleton instance of the cache */
    private static ElementCache cache = null;

    /**
     * Hidden CTOR
     */
    private ElementLoader() {
        // Not to be used
    }

    /**
     * Get the singleton instance of the cache
     * 
     * @return {@link ElementCache}
     */
    private static ElementCache getInstance() {
        if (cache == null)
            cache = new ElementCache(new ClassConverter(annotationHandler));

        return cache;
    }

    /**
     * Get the active instance of the handler for generated annotations
     * 
     * @return {@link GeneratedAnnotationHandler}
     */
    public static GeneratedAnnotationHandler getGeneratedAnnotationHandler() {
        return annotationHandler;
    }

    /**
     * Get the full class details of the specified Class element
     * 
     * @param element {@link TypeElement} of the Class to load
     * 
     * @return {@link JClass} representing the element
     * @throws MissingAnnotationException if attempting to load a class which is making use of an annotation that does not (yet) exist
     */
    public static JClass retrieveClass(TypeElement element) throws MissingAnnotationException {
        return getInstance().retrieveClass(element);
    }

    /**
     * Retrieve the details of the method from the element
     * 
     * @param element {@link ExecutableElement} containing the details of the method
     * @return {@link Pair} of {@link JClass} of the enclosing class and {@link JMethod} representing the full details of the method
     * @throws MissingAnnotationException if attempting to load a class which is making use of an annotation that does not (yet) exist
     * @throws ProcessingException        if there is an issue loading the details of the method
     */
    public static Pair<JClass, JMethod<?>> retrieveMethod(ExecutableElement element) throws MissingAnnotationException {
        return getInstance().retrieveMethod(element);
    }

    /**
     * Retrieve the details of the field from the element
     * 
     * @param element {@link VariableElement} containing the details of the field
     * @return {@link Pair} of {@link JClass} of the enclosing class and {@link JField} representing the full details of the field
     * @throws MissingAnnotationException if attempting to load a class which is making use of an annotation that does not (yet) exist
     * @throws ProcessingException        if there is an issue loading the details of the method
     */
    public static Pair<JClass, JField<?>> retrieveField(VariableElement element) throws MissingAnnotationException {
        return getInstance().retrieveField(element);
    }
}
