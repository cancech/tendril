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

import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import org.apache.commons.lang3.tuple.Pair;

import tendril.annotationprocessor.exception.MissingAnnotationException;
import tendril.annotationprocessor.exception.ProcessingException;
import tendril.codegen.JBase;
import tendril.codegen.classes.JClass;
import tendril.codegen.classes.method.JMethod;
import tendril.codegen.field.JField;

/**
 * Cache which stores all copies of codegen representations to ensure swift retrieval in subsequent queries
 */
class ElementCache {

    /** Cache to save/store processed element, to avoid needing to reprocess them in the future */
    private final Map<String, JBase> cache = new HashMap<>();
    /** Loader which performs the work of converting the annotation processing elements into codegen ones */
    private final ClassConverter loader;

    /**
     * CTOR
     */
    ElementCache(ClassConverter loader) {
        this.loader = loader;
        loader.setCache(this);
    }

    /**
     * Get the full class details of the specified Class element
     * 
     * @param element {@link TypeElement} of the Class to load
     * 
     * @return {@link JClass} representing the element
     * @throws MissingAnnotationException if attempting to load a class which is making use of an annotation that does not (yet) exist
     */
    JClass retrieveClass(TypeElement element) throws MissingAnnotationException {
        // First check if it's not a previously loaded element
        String elementName = classNameKey(element);
        if (!cache.containsKey(elementName))
            loader.loadClassDetails(element);

        return (JClass) cache.get(elementName);
    }

    /**
     * Retrieve the details of the method from the element
     * 
     * @param element {@link ExecutableElement} containing the details of the method
     * @return {@link Pair} of {@link JClass} of the enclosing class and {@link JMethod} representing the full details of the method
     * @throws MissingAnnotationException if attempting to load a class which is making use of an annotation that does not (yet) exist
     * @throws ProcessingException        if there is an issue loading the details of the method
     */
    Pair<JClass, JMethod<?>> retrieveMethod(ExecutableElement element) throws MissingAnnotationException {
        TypeElement enclosingClass = (TypeElement) element.getEnclosingElement();
        return Pair.of(retrieveClass(enclosingClass), (JMethod<?>) cache.get(nestedName(enclosingClass, element)));
    }

    /**
     * Retrieve the details of the field from the element
     * 
     * @param element {@link VariableElement} containing the details of the field
     * @return {@link Pair} of {@link JClass} of the enclosing class and {@link JField} representing the full details of the field
     * @throws MissingAnnotationException if attempting to load a class which is making use of an annotation that does not (yet) exist
     * @throws ProcessingException        if there is an issue loading the details of the method
     */
    Pair<JClass, JField<?>> retrieveField(VariableElement element) throws MissingAnnotationException {
        TypeElement enclosingClass = (TypeElement) element.getEnclosingElement();
        return Pair.of(retrieveClass(enclosingClass), (JField<?>) cache.get(nestedName(enclosingClass, element)));
    }

    /**
     * Store a class in the cache
     * 
     * @param element {@link TypeElement} representing the class that was loaded
     * @param definition {@link JClass} representation of the class
     */
    void store(TypeElement element, JClass definition) {
        cache.put(classNameKey(element), definition);
    }

    /**
     * Store an element that is nested within a class
     * 
     * @param enclosingClass {@link TypeElement} representing the class containing the element
     * @param element {@link Element} representing the nested element
     * @param nestedItem {@link JBase} representation of the nested element
     */
    void store(TypeElement enclosingClass, Element element, JBase nestedItem) {
        cache.put(nestedName(enclosingClass, element), nestedItem);
    }

    /**
     * Generate the cache key when caching a class
     * 
     * @param element {@link TypeElement} for which to generate the key
     * @return {@link String} the key
     */
    private String classNameKey(TypeElement element) {
        return element.toString();
    }

    /**
     * Generate the cache key when caching a nested element
     * 
     * @param enclosingClass {@link TypeElement} which contains the method
     * @param element        {@link Element} which represents the nested element
     * @return {@link String} the key
     */
    private String nestedName(TypeElement enclosingClass, Element element) {
        return classNameKey(enclosingClass) + "::" + element.toString();
    }
}
