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

import tendril.codegen.classes.JClass;
import tendril.codegen.classes.method.JMethod;
import tendril.codegen.field.type.ClassType;

/**
 * Abstract processor which takes care of all of the heavy lifting in terms of finding the annotated elements to process for the current round, loading their details and passing them to the
 * appropriate processing method ({@code processType()} or {@code processMethod()}. The core of this processing is performed by {@code defaultConsumer()}, which triggers the processing of the
 * annotated element as either a Type (class) or method.
 */
public abstract class AbstractTendrilProccessor extends AbstractProcessor {
    /** The annotation that has triggered the current iteration of processing */
    protected TypeElement currentAnnotation = null;
    /** The type of class that is currently being processed */
    protected ClassType currentClassType;
    /** The class that is currently being processed */
    protected JClass currentClass;
    /** The method that is currently being processed */
    protected JMethod<?> currentMethod;
    /** The environment for the current round of annotation processing */
    protected RoundEnvironment roundEnv = null;
    
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
     * Finds and processing found elements through the {@code defaultConsumer()}.
     * 
     * @param annotation {@link TypeElement} representing the annotation that is being processed
     */
    protected void findAndProcessElements(TypeElement annotation) {
        findAndProcessElements(annotation, element -> { 
            try {
                processElement(annotation, element);
            } catch (MissingAnnotationException e) {
                throw new ProcessingException("Failure to process " + element + " with annotation " + annotation, e);
            }
        });
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
     * Process the current element. This will ultimately trigger one of:
     * <ol>
     *      <li><b>Class</b>
     *          <ul>
     *              <li>validateType()</li>
     *              <li>validateClass()</li>
     *              <li>processType()</li>
     *          </ul>
     *      </li>
     *      <li><b>Method</b>
     *          <ul>
     *              <li>validateMethod()</li>
     *              <li>processMethod()</li>
     *          </ul>
     *      </li>
     * </ol>
     * 
     * @param annotation {@link TypeElement} which triggered this iteration of processing
     * @param element {@link Element} which is being processed
     * @throws MissingAnnotationException if the element has an annotation applied which is not know (at this time)
     */
    protected void processElement(TypeElement annotation, Element element) throws MissingAnnotationException {
        currentAnnotation = annotation;
        
        if (element instanceof TypeElement) {
            prepareAndProcessType((TypeElement) element);
        } else if (element instanceof ExecutableElement) {
            prepareAndProcessMethod((ExecutableElement) element);
        } else
            processingEnv.getMessager().printError("Unknown element type: " + element);
    }

    /**
     * Prepare the {@link TypeElement} as a Class and trigger its processing
     * 
     * @param element {@link TypeElement} that is to be processed
     * @throws MissingAnnotationException if the element has an annotation applied which is not know (at this time)
     */
    private void prepareAndProcessType(TypeElement element) throws MissingAnnotationException {
        // Ensure that the element is supposed to be processed before doing anything else
        validateType(element);
        
        // Load the full details of the element
        currentClass = ElementLoader.loadClassDetails(element);
        currentClassType = currentClass.getType();
        validateClass();
        
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
     * @throws MissingAnnotationException if the element has an annotation applied which is not know (at this time)
     */
    private void prepareAndProcessMethod(ExecutableElement element) throws MissingAnnotationException {
        // Load the full details of the element
        Pair<JClass, JMethod<?>> methodDetails = ElementLoader.loadMethodDetails(element);
        currentClass = methodDetails.getLeft();
        currentClassType = currentClass.getType();
        currentMethod = methodDetails.getRight();
        validateMethod();

        // Process it and save the generated code
        writeCode(processMethod());
        
        // Reset for the next iteration
        currentClass = null;
        currentClassType = null;
        currentMethod = null;
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
     * Validate that the {@link JClass} to which the annotation is applied is appropriate for the annotation. By default no check is performed (all {@link JClass}es can be used),
     * override to perform whichever checks are appropriate. Throw an appropriate exception if validation fails.
     * 
     * Note that for validation the currentClass instance field should be used.
     */
    protected void validateClass() {
        // Do nothing by default
    }

    /**
     * Validate that the {@link JMethod} to which the annotation is applied is appropriate for the annotation. By default no check is performed (all {@link JMethod}s can be used),
     * override to perform whichever checks are appropriate. Throw an appropriate exception if validation fails.
     * 
     * Note that for validation the currentMethod instance field should be used (currentClass represents the class containing the method)
     */
    protected void validateMethod() {
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
     * @return {@link ClassDefinition} defining the generated type (null if nothing is to be generated)
     */
    protected abstract ClassDefinition processMethod();
}
