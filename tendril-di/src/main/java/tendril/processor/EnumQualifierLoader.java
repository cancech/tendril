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
package tendril.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.Processor;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.VariableElement;

import com.google.auto.service.AutoService;

import tendril.annotationprocessor.AbstractTendrilProccessor;
import tendril.annotationprocessor.AnnotationGeneratedListener;
import tendril.annotationprocessor.ClassDefinition;
import tendril.annotationprocessor.ElementLoader;
import tendril.annotationprocessor.GeneratedAnnotationLoader;
import tendril.annotationprocessor.ProcessingException;
import tendril.bean.qualifier.EnumQualifier;
import tendril.codegen.annotation.JAnnotation;
import tendril.codegen.annotation.JAnnotationFactory;
import tendril.codegen.classes.EnumerationEntry;
import tendril.codegen.classes.JClass;
import tendril.codegen.classes.JClassAnnotation;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.value.JValue;
import tendril.codegen.field.value.JValueFactory;

/**
 * Processor which acts as a loader to track when {@link EnumQualifier} annotated annotations are generated. It does nothing with said annotations
 * other than to track them and allow for them to be used elsewhere.
 */
@SupportedAnnotationTypes("tendril.bean.qualifier.EnumQualifier")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
@AutoService(Processor.class)
public class EnumQualifierLoader extends AbstractTendrilProccessor implements GeneratedAnnotationLoader {
    // TODO refactor an abstract base class from this
    /** Mapping of the annotated classes that have been generated/detected */
    private final Map<ClassType, JClass> annotatedEnums = new HashMap<>();
    /** Listeners to notify when an annotation has been generated */
    private final List<AnnotationGeneratedListener> listeners = new ArrayList<>();

    /**
     * CTOR
     */
    public EnumQualifierLoader() {
        ElementLoader.addGeneratedAnnotationLoader(this);
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
        JClass klass = annotatedEnums.get(type);
        if (klass == null)
            return null;
        if (!(klass instanceof JClassAnnotation))
            return null;

        // Load the attributes that are applied to the instance
        Map<String, JValue<?, ?>> attributes = new HashMap<>();
        processingEnv.getElementUtils().getElementValuesWithDefaults(mirror).forEach((element, annonValue) -> {
            VariableElement attrValue = (VariableElement) annonValue.getValue();
            EnumerationEntry entry = EnumerationEntry.from(new ClassType(attrValue.asType().toString()), attrValue.getSimpleName().toString());
            attributes.put(element.getSimpleName().toString(), JValueFactory.create(entry));
        });
        
        // Put it all together
        JAnnotation instance = null;
        // TODO Should this be dictated by the JClass or attributes?
        if (attributes.isEmpty())
            instance = JAnnotationFactory.create(type);
        else if (attributes.size() == 1 && attributes.containsKey("value"))
            instance = JAnnotationFactory.create(type, attributes.get("value"));
        else {
            instance = JAnnotationFactory.create(type, attributes);
        }
        
        // Make sure that the annotations from the definition are applied to the instance as well
        for (JAnnotation a: klass.getAnnotations())
            instance.add(a);
        return instance;
    }
    
    /**
     * @see tendril.annotationprocessor.AbstractTendrilProccessor#processType()
     */
    @Override
    protected ClassDefinition processType() {
        annotatedEnums.put(currentClassType, currentClass);
        listeners.forEach(listener -> listener.annotationGenerated(currentClassType));
        return null;
    }

    /**
     * @see tendril.annotationprocessor.AbstractTendrilProccessor#processMethod()
     */
    @Override
    protected ClassDefinition processMethod() {
        throw new ProcessingException("@" + EnumQualifier.class.getName() + " cannot be applied to a method");
    }
}
