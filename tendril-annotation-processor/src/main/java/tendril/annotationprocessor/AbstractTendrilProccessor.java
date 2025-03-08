/*
 * Copyright 2024 Jaroslav Bosak
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

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;

import org.apache.commons.lang3.tuple.Pair;

import tendril.codegen.JBase;
import tendril.codegen.annotation.JAnnotation;
import tendril.codegen.classes.JClass;
import tendril.codegen.classes.method.JMethod;
import tendril.codegen.field.type.ClassType;

/**
 * Abstract processor which takes care of all of the heavy lifting in terms of finding the annotated elements to process for the current round, loading their details and passing them to the
 * appropriate processing method ({@code processType()} or {@code processMethod()}. The core of this processing is performed by {@code defaultConsumer()}, which triggers the processing of the
 * annotated element as either a Type (class) or method.
 */
public abstract class AbstractTendrilProccessor extends AbstractProcessor {
    /** The type of class that is currently being processed */
    protected ClassType currentClassType;
    /** The class that is currently being processed */
    protected JClass currentClass;
    /** The environment for the current round of annotation processing */
    protected RoundEnvironment roundEnv = null;
    /** Flag for whether the annotated element currently being processed is a method */
    private boolean isProcessingMethod = false;
    
    /**
     * CTOR
     */
    public AbstractTendrilProccessor() {
    }

    /**
     * @see javax.annotation.processing.AbstractProcessor#process(java.util.Set, javax.annotation.processing.RoundEnvironment)
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        roundEnv = env;

        if (env.errorRaised()) {
            errorRaised();
            return false;
        }
        if (env.processingOver()) {
            processingOver();
            return false;
        }

        annotations.forEach(annotation -> {
            findAndProcessElements(annotation);
        });
        return false;
    }

    /**
     * Called when an error was raised in the {@link RoundEnvironment}
     */
    protected void errorRaised() {

    }

    /**
     * Called when the environment for the round is no longer performing any processing
     */
    protected void processingOver() {

    }

    /**
     * Creates the consumer which triggers the processing of an element based on what type of element it is. This will either call prepareAndProcessType() or prepareAndProcessMethod() depending on
     * whether the annotation was placed on a {@link TypeElement} (i.e. class or field) or a method.
     * 
     * @return {@link Consumer} of {@link Element}s
     */
    protected Consumer<? super Element> defaultConsumer() {
        return element -> {
            if (element instanceof TypeElement) {
                isProcessingMethod = false;
                prepareAndProcessType((TypeElement) element);
            } else if (element instanceof ExecutableElement) {
                isProcessingMethod = true;
                prepareAndProcessMethod((ExecutableElement) element);
            } else
                System.err.println("Unknown element type: " + element);
        };
    }

    /**
     * Finds and processing found elements through the {@code defaultConsumer()}.
     * 
     * @param annotation {@link TypeElement} representing the annotation that is being processed
     */
    protected void findAndProcessElements(TypeElement annotation) {
        findAndProcessElements(annotation, defaultConsumer());
    }

    /**
     * Finds and processing found elements through the provider consumer
     * 
     * @param annotation {@link TypeElement} representing the annotation that is being processed
     * @param consume    {@link Consumer} which is to process the discovered {@link Element}s
     */
    protected void findAndProcessElements(TypeElement annotation, Consumer<? super Element> consume) {
        roundEnv.getElementsAnnotatedWith(annotation).forEach(consume);
    }

    /**
     * Prepare the {@link TypeElement} as a Class and trigger its processing
     * 
     * @param element {@link TypeElement} that is to be processed
     */
    private void prepareAndProcessType(TypeElement element) {
        // Ensure that the element is supposed to be processed before doing anything else
        validateType(element);
        
        // Load the full details of the element
        currentClass = ElementLoader.loadClassDetails(element);
        currentClassType = currentClass.getType();
        
        // Process it and save the generated code
        writeCode(processType());
        
        // Reset for the next iteration
        currentClass = null;
        currentClassType = null;
    }

    /**
     * Prepare the {@link ExecutableElement} as a method and trigger its processing
     * 
     * @param element {@link ExecutableElement} of the method
     */
    private void prepareAndProcessMethod(ExecutableElement element) {
        // TODO This should load the full enclosing class
        Pair<ClassType, JMethod<?>> methodDetails = ElementLoader.loadMethodDetails(element);
        writeCode(processMethod(methodDetails.getLeft(), methodDetails.getRight()));
    }

    /**
     * Validate that the {@link TypeElement} to which the annotation is applied is appropriate for the annotation. By default no check is performed (all {@link TypeElement}s can be used), override to
     * perform whichever checks are appropriate. Throw an appropriate exception if validation fails.
     * 
     * @param type {@link TypeElement} on which the annotation was applied
     */
    protected void validateType(TypeElement type) {
        // Do nothing by default
    }

    /**
     * Perform a check to verify whether or not the indicated {@link TypeElement} implements or extends the indicated desired type.
     * 
     * @param toCheck     {@link TypeElement} that is to be checked
     * @param desiredType {@link Class} that is to be in the type hierarchy
     * 
     * @return boolean true if the desired type is extended or implemented by the {@link TypeElement}
     */
    protected boolean isTypeOf(TypeElement toCheck, Class<?> desiredType) {
        TypeMirror desired = processingEnv.getElementUtils().getTypeElement(desiredType.getName()).asType();
        return processingEnv.getTypeUtils().isAssignable(toCheck.asType(), desired);
    }
    
    /**
     * Get all instances of the desired annotation which are applied to the element which is currently being processed
     * 
     * @param <ANNOTATION> indicating the type of annotation that is desired
     * 
     * @param annotation {@link Class} of the desired annotation
     * 
     * @return {@link List} of {@link JAnnotation} representing all of the annotations that have been applied to the element being processed
     */
    protected <ANNOTATION extends Annotation> List<JAnnotation> getElementAnnotations(Class<ANNOTATION> annotation) {
        if (!isProcessingMethod)
            return getElementAnnotations(currentClass, annotation);
        
        return Collections.emptyList();
    }
    
    /**
     * Get all instances of the desired annotation which are applied to the specified element
     * 
     * @param <ANNOTATION> indicating the type of annotation that is desired
     * 
     * @param element {@link JBase} on which to look for the annotation
     * @param annotation {@link Class} of the desired annotation
     * 
     * @return {@link List} of {@link JAnnotation} representing all of the annotations that have been applied to the specified element
     */
    protected <ANNOTATION extends Annotation> List<JAnnotation> getElementAnnotations(JBase element, Class<ANNOTATION> annotation) {
        ClassType annotationType = new ClassType(annotation);
        
        // Find all annotations on the element of this type
        List<JAnnotation> instances = new ArrayList<>();
        for (JAnnotation a: element.getAnnotations()) {
            if (a.getType().equals(annotationType))
                instances.add(a);
        }
        
        return instances;
    }

    /**
     * Write out the generated class.
     * 
     * @param def {@link ClassDefinition} defining the generated class
     */
    protected void writeCode(ClassDefinition def) {
        if (def == null)
            return;

        try {
            JavaFileObject builderFile = processingEnv.getFiler().createSourceFile(def.getType().getFullyQualifiedName());
            try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
                out.print(def.getCode());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create a resource file at the indicated path (relative within the directory where resources are generated) containing a single line of content
     * 
     * @param resourcePath {@link String} relative path within the resources where the file is to be created
     * @param contents     {@link String} the text that the file is to contain
     */
    protected void writeResourceFile(String resourcePath, String contents) {
        writeResourceFile(resourcePath, Collections.singletonList(contents));
    }

    /**
     * Create a resource file at the indicated path (relative within the directory where resources are generated) containing an arbitrary number of lines of content
     * 
     * @param resourcePath {@link String} relative path within the resources where the file is to be created
     * @param contents     {@link List} of {@link String}s containing the lines that the generated file is to contain
     */
    protected void writeResourceFile(String resourcePath, List<String> contents) {
        try {
            FileObject fileObject = processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", resourcePath);
            try (OutputStream out = fileObject.openOutputStream()) {
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, UTF_8));
                for (String line : contents) {
                    writer.write(line);
                    writer.newLine();
                }
                writer.flush();
            }
        } catch (IOException e) {
            System.err.println("Unable to create file " + resourcePath);
        }

    }

    /**
     * Perform the necessary processing of the indicated class, which was found to have been annotated with the required annotation. An empty implementation is provided by default, leaving it up to
     * the subclass to provide the necessary concrete implementation.
     * 
     * @return {@link ClassDefinition} defining the generated type (null if nothing is to be generated)
     */
    protected abstract ClassDefinition processType();

    /**
     * Perform the necessary processing of the indicated method, which was found to have been annotated with the required annotation. An empty implementation is provided by default, leaving it up to
     * the subclass to provide the necessary concrete implementation.
     * 
     * @param classData  {@link ClassType} representing class in which the method is located
     * @param methodData {@link JMethod} containing the details of the method
     * @return {@link ClassDefinition} defining the generated type (null if nothing is to be generated)
     */
    protected abstract ClassDefinition processMethod(ClassType classData, JMethod<?> methodData);
}
