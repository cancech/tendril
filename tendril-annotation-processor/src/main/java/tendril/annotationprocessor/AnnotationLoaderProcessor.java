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

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.VariableElement;

import tendril.annotationprocessor.element.ClassConverter;
import tendril.annotationprocessor.element.ElementLoader;
import tendril.annotationprocessor.exception.ProcessingException;
import tendril.codegen.annotation.JAnnotation;
import tendril.codegen.annotation.JAnnotationFactory;
import tendril.codegen.classes.EnumerationEntry;
import tendril.codegen.classes.JClass;
import tendril.codegen.classes.JClassAnnotation;
import tendril.codegen.classes.method.JMethod;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.Type;
import tendril.codegen.field.value.JValue;
import tendril.codegen.field.value.JValueFactory;

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
    /** Mapping of the annotated classes that have been generated/detected */
    private final Map<ClassType, JClass> foundClasses = new HashMap<>();
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
        foundClasses.put(currentClassType, currentClass);
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

    
    /**
     * @see tendril.annotationprocessor.GeneratedAnnotationLoader#getAnnotationInstance(tendril.codegen.field.type.ClassType, javax.lang.model.element.AnnotationMirror)
     */
    @Override
    public JAnnotation getAnnotationInstance(ClassType type, AnnotationMirror mirror) {
        // Make sure that the annotation is actually present
        JClass klass = foundClasses.get(type);
        if (klass == null)
            return null;
        if (!(klass instanceof JClassAnnotation))
            return null;

        JAnnotation instance = createInstance(type, (JClassAnnotation) klass, mirror);
        for (JAnnotation a: klass.getAnnotations())
            instance.add(a);
        return instance;
    }
    
    /**
     * Create the JAnnotation instance with the appropriate attributes
     * 
     * @param type {@link ClassType} indicating the class of the annotation instance to be loaded
     * @param definition {@link JClassAnnotation} which defines the annotation
     * @param mirror {@link AnnotationMirror} containing the details of the instance
     * @return {@link JAnnotation} instance with the appropriate attributes
     */
    private JAnnotation createInstance(ClassType type, JClassAnnotation definition, AnnotationMirror mirror) {
        Map<String, JValue<?, ?>> attributes = processAttributes(definition, mirror);

        JAnnotation instance = null;
        if (attributes.isEmpty())
            instance = JAnnotationFactory.create(type);
        else if (attributes.size() == 1 && attributes.containsKey("value"))
            instance = JAnnotationFactory.create(type, attributes.get("value"));
        else {
            instance = JAnnotationFactory.create(type, attributes);
        }
        
        return instance;
    }
    
    /**
     * Process the attributes placed on an annotation, loading them from the {@link AnnotationMirror} describing the instance and correlating it with the
     * {@link JClassAnnotation} definition.
     * 
     * @param definition {@link JClassAnnotation} which defined the annotation class
     * @param mirror {@link AnnotationMirror} with the details of the annotation instance
     * @return {@link Map} of {@link String} attribute names to their assigned {@link JValue}s
     */
    private Map<String, JValue<?, ?>> processAttributes(JClassAnnotation definition, AnnotationMirror mirror) {
        // Get a mapping of all attributes that are expected
        Map<String, JMethod<?>> attributes = new HashMap<>();
        definition.getMethods().forEach(m -> attributes.put(m.getName(), m));
        
        Map<String, JValue<?, ?>> attributeValues = new HashMap<>();
        mirror.getElementValues().forEach((element, value) -> {
            String name = element.getSimpleName().toString();
            // Make sure that the attribute exists in the annotation
            if (!attributes.containsKey(name))
                throw new ProcessingException(definition.getType().getFullyQualifiedName() + " does not contain an attribute " + name);
            
            attributeValues.put(name, createValue(attributes.get(name).getType(), value.getValue()));
        });
        return attributeValues;
    }
    
    /**
     * Convert the assigned "real" value to its representative {@link JValue}
     * 
     * @param desiredType {@link Type} indicating what type is expected by the attribute
     * @param value {@link Object} containing the "real" value
     * @return {@link JValue} representation
     */
    private JValue<?,?> createValue(Type desiredType, Object value) {
        // Enums are not seen as the instance, but rather just described
        if (value instanceof VariableElement) {
            VariableElement varValue = (VariableElement) value;
            ClassType valueType = new ClassType(varValue.asType().toString());
            if (!desiredType.equals(valueType)) {
                String expectedType = (desiredType instanceof ClassType) ? ((ClassType)desiredType).getFullyQualifiedName() : desiredType.getSimpleName();
                throw new ProcessingException("Invalid type, expected " + expectedType + " but received " + valueType.getFullyQualifiedName());
            }
            EnumerationEntry entry = EnumerationEntry.from(valueType, varValue.getSimpleName().toString());
            return JValueFactory.create(entry);
        }
        
        return desiredType.asValue(value);
    }
}
