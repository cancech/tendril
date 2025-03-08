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

import java.lang.reflect.Executable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Generated;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import tendril.codegen.BaseBuilder;
import tendril.codegen.JBase;
import tendril.codegen.VisibilityType;
import tendril.codegen.annotation.JAnnotation;
import tendril.codegen.classes.ClassBuilder;
import tendril.codegen.classes.FieldBuilder;
import tendril.codegen.classes.JClass;
import tendril.codegen.classes.MethodBuilder;
import tendril.codegen.classes.ParameterBuilder;
import tendril.codegen.classes.method.AnonymousMethod;
import tendril.codegen.classes.method.JMethod;
import tendril.codegen.field.VisibileTypeBuilder;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.Type;
import tendril.codegen.field.type.TypeFactory;
import tendril.codegen.field.value.JValue;

/**
 * Handles the loading of items from the Annotation Processing {@link Element}s into {@link JBase} representations
 */
public abstract class ElementLoader {
    /** Cache to save/store processed element, to avoid needing to reprocess them in the future */
    private static final Map<Element, JBase> cache = new HashMap<>();
    
    /**
     * Hidden CTOR - no need to create instances of this class
     */
    private ElementLoader() {
        // Intentionally left blank
    }

    /**
     * Load the full class details of the specified Class element
     * 
     * @param element {@link TypeElement} of the Class to load
     * 
     * @return {@link JClass} representing the element
     */
    public static JClass loadClassDetails(TypeElement element) {
        // First check if it's not a previously loaded element
        if (cache.containsKey(element))
            return (JClass) cache.get(element);
        
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
                loadFieldDetails(fieldBuilder, (VariableElement) e);
            } else if (kind == ElementKind.METHOD) {
                // Load all methods that are present
                ExecutableElement m = (ExecutableElement) e;
                MethodBuilder<?> methodBuilder = builder.buildMethod(TypeFactory.create(m.getReturnType()), e.getSimpleName().toString());
                loadMethodDetails(methodBuilder, m);
            }
            
        }
        
        // Build and cache the generated class representation
        JClass klass = builder.build();
        cache.put(element, klass);
        return klass;
    }

    /**
     * Load the details of the method from the element
     * 
     * @param element {@link ExecutableElement} containing the details of the method
     * @return {@link Pair} of {@link JClass} of the enclosing class and {@link JMethod} representing the full details of the method
     * @throws ProcessingException if there is an issue loading the details of the method
     */
    public static Pair<JClass, JMethod<?>> loadMethodDetails(ExecutableElement element) {
        return Pair.of(loadClassDetails((TypeElement) element.getEnclosingElement()), (JMethod<?>)cache.get(element));
    }

    /**
     * Determine the details of the class represented by the element
     * 
     * @param type {@link TypeElement} of the class
     * @return {@link ClassType} with the class details
     */
    private static ClassType deriveClassData(TypeElement type) {
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
    private static ClassBuilder builderForClass(TypeElement element) {
        ClassType type = deriveClassData(element);
        ElementKind kind = element.getKind();
        if (kind == ElementKind.INTERFACE)
            return ClassBuilder.forInterface(type);
        if (kind == ElementKind.ANNOTATION_TYPE)
            return ClassBuilder.forAnnotation(type);
        if (kind == ElementKind.CLASS || kind == ElementKind.ENUM) {
            // TODO have a separate Enum builder?
            if (element.getModifiers().contains(Modifier.ABSTRACT))
                return ClassBuilder.forAbstractClass(type);
            
            return ClassBuilder.forConcreteClass(type);
        }
        
        // Didn't find a builder...
        throw new ProcessingException(element.getQualifiedName() + " [" + kind + "] is not a valid class type");
    }
    
    /**
     * Load the details of the method from the element and into the specified builder.
     * 
     * @param builder {@link MethodBuilder} that is used to create the method
     * @param element {@link Executable}
     */
    private static void loadMethodDetails(MethodBuilder<?> builder, ExecutableElement element) {
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
        
        cache.put(element, builder.build());
    }
    
    /**
     * Load the details of the field into the builder
     * 
     * @param builder {@link FieldBuilder} which is building the field
     * @param varElement {@link VariableElement} defining the field
     */
    private static void loadFieldDetails(FieldBuilder<?> builder, VariableElement varElement) {
        loadElementFinality(builder, varElement);
        loadElementMods(builder, varElement);
        loadAnnotations(builder, varElement);
        cache.put(varElement, builder.build());
    }
    
    /**
     * Load annotations from the element into the builder
     * 
     * @param builder {@link BaseBuilder} with which the representative element is being built
     * @param element {@link Element} from which to load the annotations
     */
    private static void loadAnnotations(BaseBuilder<?, ?> builder, Element element) {
        for (AnnotationMirror m : element.getAnnotationMirrors()) {
            ClassType annonType = deriveClassData((TypeElement) m.getAnnotationType().asElement());
            // TODO shouldn't need to skip Generated....
            if (annonType.equals(new ClassType(Generated.class)))
                continue;
            
            JAnnotation annonData = new JAnnotation(annonType);
            for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : m.getElementValues().entrySet()) {
                // Cannot rely on loadMethodDetails as some annotations annotate themselves (i.e.: @Retention, @Target)
                ExecutableElement attribute = entry.getKey();
                Type attributeType = TypeFactory.create(attribute.getReturnType());
                String attributeName = attribute.getSimpleName().toString();
                JValue<?, ?> value = attributeType.asValue(entry.getValue().getValue());
                annonData.addAttribute(new AnonymousMethod<Type>(attributeType, attributeName), value);
            }
            builder.addAnnotation(annonData);
        }
    }
    
    /**
     * Load the final state of the element into the builder
     * 
     * @param builder {@link BaseBuilder} with which the representative element is being built
     * @param element {@link Element} from which to load the details
     */
    private static void loadElementFinality(BaseBuilder<?, ?> builder, Element element) {
        builder.setFinal(element.getModifiers().contains(Modifier.FINAL));
    }
    
    /**
     * Load the visibility type modifiers of the element into the builder
     * 
     * @param builder {@link VisibileTypeBuilder} with which the representative element is being built
     * @param element {@link Element} from which to load the details
     */
    private static void loadElementMods(VisibileTypeBuilder<?, ?, ?> builder, Element element) {
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
