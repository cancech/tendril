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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import org.apache.commons.lang3.StringUtils;

import com.google.auto.common.AnnotationValues;

import tendril.annotationprocessor.exception.DataMismatchException;
import tendril.annotationprocessor.exception.InvalidTypeException;
import tendril.annotationprocessor.exception.MissingAnnotationException;
import tendril.annotationprocessor.exception.TendrilException;
import tendril.codegen.BaseBuilder;
import tendril.codegen.DefinitionException;
import tendril.codegen.JBase;
import tendril.codegen.VisibilityType;
import tendril.codegen.annotation.JAnnotation;
import tendril.codegen.annotation.JAnnotationFactory;
import tendril.codegen.classes.ClassBuilder;
import tendril.codegen.classes.EnumerationEntry;
import tendril.codegen.classes.FieldBuilder;
import tendril.codegen.classes.JClass;
import tendril.codegen.classes.MethodBuilder;
import tendril.codegen.classes.NestedClassMethodElementBuilder;
import tendril.codegen.classes.ParameterBuilder;
import tendril.codegen.field.VisibileTypeBuilder;
import tendril.codegen.field.type.ArrayType;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.Type;
import tendril.codegen.field.type.TypeFactory;
import tendril.codegen.field.value.JValue;
import tendril.codegen.field.value.JValueFactory;

/**
 * Handles the conversion of items from the Annotation Processing {@link Element}s into {@link JBase} representations
 */
public class ClassConverter {

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
     * @throws TendrilException if an issues is encountered
     */
    void loadClassDetails(TypeElement element) throws MissingAnnotationException, TendrilException {
        // Populate the builder with the details of the class itself
        ClassBuilder builder = builderForClass(element);
        loadParentClass(builder, element);
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
     * Load the details of the parent class
     * 
     * @param builder {@link ClassBuilder} in which the current class is being defined/built
     * @param element {@link TypeElement} representing the class
     * 
     * @throws TendrilException if an issue loading the details of the parent is encountered
     */
    private void loadParentClass(ClassBuilder builder, TypeElement element) throws TendrilException {
        // Make sure that there's a valid parent
        TypeMirror m = element.getSuperclass();
        if (m == null || m.getKind() == TypeKind.NONE)
            return;
        
        // Make sure that it's something we need to explicitely extend
        TypeElement parentElement = (TypeElement) ((DeclaredType) m).asElement();
        String parentName = parentElement.getQualifiedName().toString();
        if ("java.lang.Object".equals(parentName) || "java.lang.Enum".equals(parentName))
            return;
        
        try {
            builder.extendsClass(ElementLoader.retrieveClass(parentElement));
        } catch (MissingAnnotationException | TendrilException e) {
            throw new TendrilException("Failed to determine parent for " + element, e);
        }
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
        return TypeFactory.createClassType(packageName, typeName);
    }
    
    /**
     * Get the appropriate builder for the type of class/element
     * 
     * @param element {@link TypeElement} representing the class
     * 
     * @return {@link ClassBuilder} appropriate for the type of class that it is
     * @throws TendrilException if an unknown/invalid class type is encountered
     */
    private ClassBuilder builderForClass(TypeElement element) throws TendrilException {
        ClassType type = deriveClassData(element);
        ElementKind kind = element.getKind();
        if (kind == ElementKind.INTERFACE)
            return ClassBuilder.forInterface(type);
        else if (kind == ElementKind.ANNOTATION_TYPE)
            return ClassBuilder.forAnnotation(type);
        else if (kind == ElementKind.ENUM) {
            ClassBuilder builder = ClassBuilder.forEnum(type);
            // Populate the enum values
            for(Element e: element.getEnclosedElements()) {
                if (e.getKind() == ElementKind.ENUM_CONSTANT) {
                    builder.buildEnumeration(e.getSimpleName().toString()).build();
                }
            }
            
            return builder;
        } else if (kind == ElementKind.CLASS) {
            if (element.getModifiers().contains(Modifier.ABSTRACT))
                return ClassBuilder.forAbstractClass(type);
            
            return ClassBuilder.forConcreteClass(type);
        }
        
        // Didn't find a builder...
        throw new InvalidTypeException(element.getQualifiedName() + " [" + kind + "] is not a valid class type");
    }
    
    /**
     * Load the details of the method/constructor from the executable element and into the specified builder.
     * 
     * @param builder {@link NestedClassMethodElementBuilder} that is used to create the method/constructor
     * @param enclosingClass {@link TypeElement} in which the element is contained
     * @param element {@link ExecutableElement} which is to be loaded
     * @throws MissingAnnotationException if attempting to load a class which is making use of an annotation that does not (yet) exist
     * @throws DataMismatchException if there is an issue loading data and/or values
     */
    private void loadExecutableElementDetails(NestedClassMethodElementBuilder<?, ?, ?> builder, TypeElement enclosingClass, ExecutableElement element) throws MissingAnnotationException, DataMismatchException {
        // Load the general information
        loadElementFinality(builder, element);
        loadElementMods(builder, element);
        loadAnnotations(builder, element);
        
        // Load the parameters
        List<? extends TypeMirror> parameterTypes = ((ExecutableType) element.asType()).getParameterTypes();
        List<? extends VariableElement> parameters = element.getParameters();
        if (parameterTypes.size() != parameters.size())
            throw new DataMismatchException(element + " mismatch between number of parameters and parameter types");
     
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
     * @throws DataMismatchException if there is an issue loading data and/or values
     */
    private void loadFieldDetails(FieldBuilder<?> builder, TypeElement enclosingClass, VariableElement varElement) throws MissingAnnotationException, DataMismatchException {
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
     * @throws DataMismatchException if there is an issue processing annotation values
     */
    private void loadAnnotations(BaseBuilder<?, ?> builder, Element element) throws MissingAnnotationException, DataMismatchException {
        for (AnnotationMirror m : element.getAnnotationMirrors()) {
            TypeElement annonElement = (TypeElement) m.getAnnotationType().asElement();
            try {
                builder.addAnnotation(buildAnnotation(deriveClassData(annonElement), m, annonElement, new ArrayList<>()));
            } catch (DefinitionException ex) {
                throw new MissingAnnotationException(m.getAnnotationType(), element);
            }
        }
    }
    
    /**
     * Build the annotation instance from the specified information. This is a recursive mechanism which build the specified annotation, as well as all annotations which are
     * applied to it.
     *  
     * @param annonType {@link ClassType} representing the type of annotation
     * @param mirror {@link AnnotationMirror} representing the current instance of the annotation
     * @param element {@link TypeElement} representing the definition of the annotation
     * @param hierarchy {@link List} of {@link ClassType}s that have already been encountered during this iterative build process
     * @return {@link JAnnotation} representing the annotation instance
     * @throws DataMismatchException if there is an issue processing annotation values
     */
    private JAnnotation buildAnnotation(ClassType annonType, AnnotationMirror mirror, TypeElement element, List<ClassType> hierarchy) throws DataMismatchException {
        hierarchy.add(annonType);
        
        // Process all values
        Map<String, JValue<?, ?>> attributes = new HashMap<>();
        Map<? extends ExecutableElement, ? extends AnnotationValue> mirrorAttributes = mirror.getElementValues();
        for (ExecutableElement attr: mirror.getElementValues().keySet()) {
            JValue<?, ?> jvalue = createValue(TypeFactory.create(attr.getReturnType()), mirrorAttributes.get(attr).getValue());
            attributes.put(attr.getSimpleName().toString(), jvalue);
            
        }
        
        // Create the annotation
        JAnnotation annon = null;
        if (attributes.isEmpty())
            annon = JAnnotationFactory.create(annonType);
        else if (attributes.size() == 1 && attributes.containsKey("value"))
            annon = JAnnotationFactory.create(annonType, attributes.get("value"));
        else {
            annon = JAnnotationFactory.create(annonType, attributes);
        }
        
        // Apply all annotations
        for (AnnotationMirror m: element.getAnnotationMirrors()) {
            TypeElement annonElement = (TypeElement)m.getAnnotationType().asElement();
            ClassType appliedAnnonType = deriveClassData(annonElement);
            if (!hierarchy.contains(appliedAnnonType))
                annon.add(buildAnnotation(appliedAnnonType, m, annonElement, hierarchy));
        }
        
        return annon;
    }
    
    /**
     * Convert the assigned "real" value to its representative {@link JValue}
     * 
     * @param desiredType {@link Type} indicating what type is expected by the attribute
     * @param value {@link Object} containing the "real" value
     * @return {@link JValue} representation
     * @throws DataMismatchException if the value does not match the desired type
     */
    private JValue<?,?> createValue(Type desiredType, Object value) throws DataMismatchException {
        // Enums are not seen as the instance, but rather just described. As such they must be treated separately
        if (value instanceof VariableElement)
            return JValueFactory.create(createEnumEntry(desiredType, (VariableElement) value));
        
        Object valueToUse = value;
        if (value instanceof List) {
            if (!(desiredType instanceof ArrayType))
                throw new DataMismatchException(desiredType, value.getClass().getName() + "[]");
            
            @SuppressWarnings("unchecked")
            List<AnnotationValue> listVal = (List<AnnotationValue>) value;
            Object av = ((AnnotationValue) listVal.get(0)).getValue();
            
            // Enum is a special case
            if (av instanceof VariableElement) {
                EnumerationEntry[] entries = new EnumerationEntry[listVal.size()];
                for (int i = 0; i < entries.length; i++)
                    entries[i] = createEnumEntry(((ArrayType<?>) desiredType).getContainedType(), (VariableElement) (listVal.get(i)).getValue());
                return JValueFactory.createArray(entries);
            }
            
            // Otherwise just perform a direct cast
            valueToUse = annotationValueAsBaseType(listVal, av.getClass());
        }

        return desiredType.asValue(valueToUse);
    }
    
    /**
     * Convert the list of annotation values to an array of the indicated type. The conversion to the indicated type is done via casting.
     * 
     * @param <T> the value that is contained within the {@link AnnotationValue} {@link List}
     * @param values {@link List} of {@link AnnotationValues} which were applied to the annotation
     * @param type {@link Class} of what is contained within the value list
     * @return T[] conversion of the {@link AnnotationValue}s
     */
    @SuppressWarnings("unchecked")
    private <T> T[] annotationValueAsBaseType(List<AnnotationValue> values, Class<T> type) {
        T[] entries = (T[]) Array.newInstance(type, values.size());
        for (int i = 0; i < entries.length; i++)
            entries[i] = (T) ((AnnotationValue)values.get(i)).getValue();
        return entries;
    }
    
    /**
     * Create an {@link EnumerationEntry} representation of an enum that is defined by a {@link VariableElement}. Verification is performed to ensure that the element
     * matches the desired type, with a {@link TendrilException} thrown is that is not the case.
     * 
     * @param desiredType {@link Type} indicating the type of enum which is desired/expected
     * @param value {@link VariableElement} representing the specific enum entry
     * @return {@link EnumerationEntry} for the enum
     * @throws DataMismatchException if the value does not match the desired type
     */
    private EnumerationEntry createEnumEntry(Type desiredType, VariableElement value) throws DataMismatchException {
        ClassType valueType = TypeFactory.createClassType(value.asType().toString());
        if (!desiredType.equals(valueType)) {
            throw new DataMismatchException(desiredType, valueType);
        }
        return EnumerationEntry.from(valueType, value.getSimpleName().toString());
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
