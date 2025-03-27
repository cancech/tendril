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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;

import org.apache.commons.lang3.StringUtils;

import tendril.annotationprocessor.AnnotationGeneratedListener;
import tendril.annotationprocessor.GeneratedAnnotationLoader;
import tendril.annotationprocessor.exception.MissingAnnotationException;
import tendril.annotationprocessor.exception.ProcessingException;
import tendril.codegen.BaseBuilder;
import tendril.codegen.DefinitionException;
import tendril.codegen.JBase;
import tendril.codegen.VisibilityType;
import tendril.codegen.annotation.JAnnotation;
import tendril.codegen.classes.ClassBuilder;
import tendril.codegen.classes.FieldBuilder;
import tendril.codegen.classes.JClass;
import tendril.codegen.classes.MethodBuilder;
import tendril.codegen.classes.NestedClassMethodElementBuilder;
import tendril.codegen.classes.ParameterBuilder;
import tendril.codegen.classes.method.AnonymousMethod;
import tendril.codegen.field.VisibileTypeBuilder;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.Type;
import tendril.codegen.field.type.TypeFactory;
import tendril.codegen.field.value.JValue;

/**
 * Handles the conversion of items from the Annotation Processing {@link Element}s into {@link JBase} representations
 */
public class ClassConverter {
    // TODO move the loader into a separate class
    /** List of loaders for generated annotations */
    private static final List<GeneratedAnnotationLoader> generatedAnnotationLoaders = new ArrayList<>();
    /** List of listeners to notify when an annotation has been generated */
    private static final List<AnnotationGeneratedListener> generatedAnnotationListeners = new ArrayList<>();
    
    /**
     * Add a new annotation loader
     * 
     * @param loader {@link GeneratedAnnotationLoader} to add
     */
    public static void addGeneratedAnnotationLoader(GeneratedAnnotationLoader loader) {
        generatedAnnotationLoaders.add(loader);
        generatedAnnotationListeners.forEach(listener -> loader.addListener(listener));
    }
    
    /**
     * Add a listener to be notified when an annotation has been generated
     * 
     * @param listener {@link AnnotationGeneratedListener} to add
     */
    public static void addListener(AnnotationGeneratedListener listener) {
        generatedAnnotationListeners.add(listener);
        generatedAnnotationLoaders.forEach(loader -> loader.addListener(listener));
    }
    
    /** The cache where loaded elements are to be stored */
    private ElementCache cache;
    
    /**
     * CTOR
     */
    ClassConverter() {
    }
    
    /**
     * Specify the cache
     * 
     * @param cache {@link ElementCache} where the elements are to be cached
     */
    void setCache(ElementCache cache) {
        this.cache = cache;
    }

    /**
     * Load the full class details of the specified Class element
     * 
     * @param element {@link TypeElement} of the Class to load
     * 
     * @return {@link JClass} representing the element
     * @throws MissingAnnotationException if attempting to load a class which is making use of an annotation that does not (yet) exist
     */
    void loadClassDetails(TypeElement element) throws MissingAnnotationException {
        // Populate the builder with the details of the class itself
        ClassBuilder builder = builderForClass(element);
        loadElementFinality(builder, element);
        loadElementMods(builder, element);
        loadAnnotations(builder, element);
        
        // Load all of the nested elements of the class that we care about
        for (Element e : element.getEnclosedElements()) {
            ElementKind kind = e.getKind();
            
            if (kind == ElementKind.FIELD) {
                // Load all fields that are present
                Type t = TypeFactory.create(e.asType());
                FieldBuilder<Type> fieldBuilder = builder.buildField(t, e.getSimpleName().toString());
                loadFieldDetails(fieldBuilder, element, (VariableElement) e);
            } else if (kind == ElementKind.METHOD) {
                // Load all methods that are present
                ExecutableElement m = (ExecutableElement) e;
                MethodBuilder<?> methodBuilder = builder.buildMethod(TypeFactory.create(m.getReturnType()), e.getSimpleName().toString());
                loadExecutableElementDetails(methodBuilder, element, m);
            } else if (kind == ElementKind.CONSTRUCTOR) {
                // Load all constructors that are present
                loadExecutableElementDetails(builder.buildConstructor(), element, (ExecutableElement) e);
            }
            
        }
        
        cache.store(element, builder.build());
    }

    /**
     * Determine the details of the class represented by the element
     * 
     * @param type {@link TypeElement} of the class
     * @return {@link ClassType} with the class details
     */
    private ClassType deriveClassData(TypeElement type) {
        String typeName = type.getSimpleName().toString();
        String packageName = StringUtils.removeEnd(StringUtils.removeEnd(type.getQualifiedName().toString(), typeName), ".");
        return new ClassType(packageName, typeName);
    }
    
    /**
     * Get the appropriate builder for the type of class/element
     * 
     * @param element {@link TypeElement} representing the class
     * 
     * @return {@link ClassBuilder} appropriate for the type of class that it is
     */
    private ClassBuilder builderForClass(TypeElement element) {
        ClassType type = deriveClassData(element);
        ElementKind kind = element.getKind();
        if (kind == ElementKind.INTERFACE)
            return ClassBuilder.forInterface(type);
        if (kind == ElementKind.ANNOTATION_TYPE)
            return ClassBuilder.forAnnotation(type);
        if (kind == ElementKind.ENUM)
            return ClassBuilder.forEnum(type);
        if (kind == ElementKind.CLASS) {
            if (element.getModifiers().contains(Modifier.ABSTRACT))
                return ClassBuilder.forAbstractClass(type);
            
            return ClassBuilder.forConcreteClass(type);
        }
        
        // Didn't find a builder...
        throw new ProcessingException(element.getQualifiedName() + " [" + kind + "] is not a valid class type");
    }
    
    /**
     * Load the details of the method/constructor from the executable element and into the specified builder.
     * 
     * @param builder {@link NestedClassMethodElementBuilder} that is used to create the method/constructor
     * @param enclosingClass {@link TypeElement} in which the element is contained
     * @param element {@link ExecutableElement} which is to be loaded
     * @throws MissingAnnotationException if attempting to load a class which is making use of an annotation that does not (yet) exist
     */
    private void loadExecutableElementDetails(NestedClassMethodElementBuilder<?, ?, ?> builder, TypeElement enclosingClass, ExecutableElement element) throws MissingAnnotationException {
        // Load the general information
        loadElementFinality(builder, element);
        loadElementMods(builder, element);
        loadAnnotations(builder, element);
        
        // Load the parameters
        List<? extends TypeMirror> parameterTypes = ((ExecutableType) element.asType()).getParameterTypes();
        List<? extends VariableElement> parameters = element.getParameters();
        if (parameterTypes.size() != parameters.size())
            throw new ProcessingException(element + " mismatch between number of parameters and parameter types");
     
        for (int i = 0; i < parameters.size(); i++) {
            VariableElement varElement = parameters.get(i);
            ParameterBuilder<?, ?> paramBuilder = new ParameterBuilder<>(TypeFactory.create(parameterTypes.get(i)), varElement.getSimpleName().toString());
            loadAnnotations(paramBuilder, varElement);
            builder.addParameter(paramBuilder.build());
        }
        
        // Apply some semblance of code (to keep the builder happy)
        if (!element.getModifiers().contains(Modifier.ABSTRACT))
            builder.emptyImplementation();

        cache.store(enclosingClass, element, builder.build());
    }
    
    /**
     * Load the details of the field into the builder
     * 
     * @param builder {@link FieldBuilder} which is building the field
     * @param enclosingClass {@link TypeElement} in which the field is contained
     * @param varElement {@link VariableElement} defining the field
     * @throws MissingAnnotationException if attempting to load a class which is making use of an annotation that does not (yet) exist
     */
    private void loadFieldDetails(FieldBuilder<?> builder, TypeElement enclosingClass, VariableElement varElement) throws MissingAnnotationException {
        loadElementFinality(builder, varElement);
        loadElementMods(builder, varElement);
        loadAnnotations(builder, varElement);
        cache.store(enclosingClass, varElement, builder.build());
    }
    
    /**
     * Load annotations from the element into the builder
     * 
     * @param builder {@link BaseBuilder} with which the representative element is being built
     * @param element {@link Element} from which to load the annotations
     * @throws MissingAnnotationException if attempting to load a class which is making use of an annotation that does not (yet) exist
     */
    private void loadAnnotations(BaseBuilder<?, ?> builder, Element element) throws MissingAnnotationException {
        // Track what annotations have been processed, to avoid duplicates
        List<ClassType> processedTypes = new ArrayList<>();
        
        for (AnnotationMirror m : element.getAnnotationMirrors()) {
            // Determine the type of annotation applied and make sure it's not been processed already
            ClassType annonType = null;
            try {
                annonType = deriveClassData((TypeElement) m.getAnnotationType().asElement());
            } catch (DefinitionException ex) {
                throw new MissingAnnotationException(m.getAnnotationType(), element);
            }
            
            if (processedTypes.contains(annonType))
                continue;
            
            processedTypes.add(annonType);
            try {
                // First try to load the annotation the "normal" way. This is the better way to load it, provided that the class is not generated during annotation processing
                applyAnnotatonByClass(builder, annonType, (TypeElement)m.getAnnotationType().asElement(), element);
            } catch (ClassNotFoundException e) {
                // If the class is not found, that means that the annotation was generated and so, it should be loaded in a separate manner
                applyGeneratedAnnotation(builder, annonType, m, element);
            }
        }
    }
    
    /**
     * Try to apply the specified annotation by loading the annotation as an instance. This requires the class of the annotation to be processed to be known.
     * 
     * @param builder {@link BaseBuilder} where the class for the element being loaded is assembled
     * @param annonType {@link ClassType} indicating the type of the annotation being loaded
     * @param annotationElement {@link TypeElement} representing the annotation
     * @param appliedTo {@link Element} to which the annotation being loaded is applied
     * 
     * @throws ClassNotFoundException if the class of the annotation is not known and cannot be loaded
     */
    private void applyAnnotatonByClass(BaseBuilder<?, ?> builder, ClassType annonType, TypeElement annotationElement, Element appliedTo) throws ClassNotFoundException {
        @SuppressWarnings("unchecked")
        Class<? extends Annotation> annonClass = (Class<? extends Annotation>) Class.forName(annotationElement.getQualifiedName().toString());
        
        // Load all instances of the annotation type applied and determine their attributes
        for (Annotation annon: appliedTo.getAnnotationsByType(annonClass)) {
            JAnnotation annonData = new JAnnotation(annonType);
            
            // Need to use reflection, as this appears to be the only "generic way" to retrieve array values
            for (Method method: annonClass.getDeclaredMethods()) {
                Type attributeType = TypeFactory.create(method.getReturnType());
                String attributeName = method.getName();
                try {
                    JValue<?, ?> value = attributeType.asValue(method.invoke(annon));
                    annonData.addAttribute(new AnonymousMethod<Type>(attributeType, attributeName), value);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            builder.addAnnotation(annonData);
        }
    }
    
    /**
     * Try to apply the specified annotation by loading the definition of the annotation from a loader of generated annotation. This should only be used when the annotation in question
     * is generated during annotation processing and cannot be loaded through other means.
     * 
     * @param builder {@link BaseBuilder} where the class for the element being loaded is assembled
     * @param annonType {@link ClassType} indicating the type of the annotation being loaded
     * @param annonMirror {@link AnnotationMirror} containing the details of the annotation that is applied to the element
     * @param appliedTo {@link Element} to which the annotation being loaded is applied
     * @throws MissingAnnotationException if attempting to load a class which is making use of an annotation that does not (yet) exist
     */
    private void applyGeneratedAnnotation(BaseBuilder<?, ?> builder, ClassType annonType, AnnotationMirror annonMirror, Element appliedTo) throws MissingAnnotationException {
        for (GeneratedAnnotationLoader l: generatedAnnotationLoaders) {
            JAnnotation annon = l.getAnnotationInstance(annonType, annonMirror);
            if (annon != null) {
                builder.addAnnotation(annon);
                return;
            }
        }
        
        throw new MissingAnnotationException(annonMirror.getAnnotationType(), appliedTo);
    }
    
    /**
     * Load the final state of the element into the builder
     * 
     * @param builder {@link BaseBuilder} with which the representative element is being built
     * @param element {@link Element} from which to load the details
     */
    private void loadElementFinality(BaseBuilder<?, ?> builder, Element element) {
        builder.setFinal(element.getModifiers().contains(Modifier.FINAL));
    }
    
    /**
     * Load the visibility type modifiers of the element into the builder
     * 
     * @param builder {@link VisibileTypeBuilder} with which the representative element is being built
     * @param element {@link Element} from which to load the details
     */
    private void loadElementMods(VisibileTypeBuilder<?, ?, ?> builder, Element element) {
        Set<Modifier> mods = element.getModifiers();
        builder.setStatic(mods.contains(Modifier.STATIC));
        if (mods.contains(Modifier.PUBLIC))
            builder.setVisibility(VisibilityType.PUBLIC);
        else if (mods.contains(Modifier.PRIVATE))
            builder.setVisibility(VisibilityType.PRIVATE);
        else if (mods.contains(Modifier.PROTECTED))
            builder.setVisibility(VisibilityType.PROTECTED);
        else
            builder.setVisibility(VisibilityType.PACKAGE_PRIVATE);
    }
}
