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
package tendril.processor;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import tendril.codegen.annotation.JAnnotation;
import tendril.codegen.classes.ParameterBuilder;
import tendril.codegen.classes.method.AnonymousMethod;
import tendril.codegen.classes.method.JMethod;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.Type;
import tendril.codegen.field.type.TypeFactory;
import tendril.codegen.field.value.JValue;

/**
 * Abstract processor which takes care of all of the heavy lifting in terms of finding the annotated elements to process for the current round, loading their details and passing them to the
 * appropriate processing method ({@code processType()} or {@code processMethod()}. The core of this processing is performed by {@code defaultConsumer()}, which triggers the processing of the
 * annotated element as either a Type (class) or method.
 */
public abstract class AbstractTendrilProccessor extends AbstractProcessor {

    /** The environment for the current round of annotation processing */
    protected RoundEnvironment roundEnv = null;
    /** The TypeElement that is currently being processed (i.e.: on which the annotation was discovered */
    private TypeElement currentTypeElement = null;

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
                currentTypeElement = (TypeElement) element;
                prepareAndProcessType();
                currentTypeElement = null;
            } else if (element instanceof ExecutableElement)
                prepareAndProcessMethod((ExecutableElement) element);
            else
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
     */
    private void prepareAndProcessType() {
        validateType(currentTypeElement);
        writeCode(processType(deriveClassData(currentTypeElement)));
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
     * @return boolean true if the desired type is extended or implemented by the {@link TypeElement}
     */
    protected boolean isTypeOf(TypeElement toCheck, Class<?> desiredType) {
        TypeMirror desired = processingEnv.getElementUtils().getTypeElement(desiredType.getName()).asType();
        return processingEnv.getTypeUtils().isAssignable(toCheck.asType(), desired);
    }

    /**
     * Get the elements contained within the current TypeElement, which are annotated with the specified annotation
     * 
     * @param annotation {@link Class} of the {@link Annotation} to look for
     * 
     * @return {@link Map} of the {@link ElementKind} (type of element) to the {@link List} of {@link Element}s that were found
     */
    protected Map<ElementKind, List<Element>> getEnclosedElements(Class<? extends Annotation> annotation) {
        if (currentTypeElement == null)
            return new HashMap<>();

        Map<ElementKind, List<Element>> annotatedElements = new HashMap<>();
        for (Element e : currentTypeElement.getEnclosedElements()) {
            if (e.getAnnotation(annotation) != null) {
                ElementKind kind = e.getKind();
                if (!annotatedElements.containsKey(kind))
                    annotatedElements.put(kind, new ArrayList<>());

                annotatedElements.get(e.getKind()).add(e);
            }
        }

        return annotatedElements;
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
     * Prepare the {@link ExecutableElement} as a method and trigger its processing
     * 
     * @param element {@link ExecutableElement} of the method
     */
    private void prepareAndProcessMethod(ExecutableElement element) {
        Pair<ClassType, JMethod<?>> methodDetails = loadMethodDetails(element);
        writeCode(processMethod(methodDetails.getLeft(), methodDetails.getRight()));
    }

    /**
     * Load the details of the method from the element
     * 
     * @param element {@link ExecutableElement} containing the details of the method
     * @return {@link Pair} of {@link ClassType} of the enclosing class and {@link JMethod} representing the full details of the method
     */
    private Pair<ClassType, JMethod<?>> loadMethodDetails(ExecutableElement element) {
        ClassType classData = deriveClassData((TypeElement) element.getEnclosingElement());
        List<? extends TypeMirror> parameterTypes = ((ExecutableType) element.asType()).getParameterTypes();
        List<? extends VariableElement> parameters = element.getParameters();
        if (parameterTypes.size() != parameters.size())
            throw new IllegalStateException(element + " mismatch between number of parameters and parameter types");

        JMethod<?> method = new AnonymousMethod<>(TypeFactory.create(element.getReturnType()), element.getSimpleName().toString());
        for (int i = 0; i < parameters.size(); i++) {
            VariableElement varElement = parameters.get(i);
            ParameterBuilder<?, ?> paramBuilder = new ParameterBuilder<>(TypeFactory.create(parameterTypes.get(i)), varElement.getSimpleName().toString());
            for (AnnotationMirror m : varElement.getAnnotationMirrors()) {
                JAnnotation annonData = new JAnnotation(deriveClassData((TypeElement) m.getAnnotationType().asElement()));
                for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : m.getElementValues().entrySet()) {
                    Pair<ClassType, JMethod<?>> details = loadMethodDetails(entry.getKey());
                    JValue<?, ?> value = details.getRight().getType().asValue(entry.getValue().getValue());
                    annonData.addAttribute(details.getRight(), value);
                }
                paramBuilder.addAnnotation(annonData);
            }

            method.addParameter(paramBuilder.build());
        }

        return Pair.of(classData, method);
    }

    /**
     * Determine the details of the class represented by the element
     * 
     * @param type {@link TypeElement} of the class
     * @return {@link ClassType} with the class details
     */
    protected ClassType deriveClassData(TypeElement type) {
        String typeName = type.getSimpleName().toString();
        String packageName = StringUtils.removeEnd(StringUtils.removeEnd(type.getQualifiedName().toString(), typeName), ".");
        return new ClassType(packageName, typeName);
    }

    /**
     * Get the {@link Type} for the {@link VariableElement}
     * 
     * @param element {@link VariableElement}
     * @return {@link Type}
     */
    protected Type variableType(VariableElement element) {
        TypeElement type = (TypeElement) processingEnv.getTypeUtils().asElement(element.asType());
        return deriveClassData(type);
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
     * @param data {@link ClassType} representing the annotated class
     * @return {@link ClassDefinition} defining the generated type (null if nothing is to be generated)
     */
    protected abstract ClassDefinition processType(ClassType data);

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
