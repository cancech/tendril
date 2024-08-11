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

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import tendril.codegen.annotation.JAnnotation;
import tendril.codegen.classes.ParameterBuilder;
import tendril.codegen.classes.method.AnonymousMethod;
import tendril.codegen.classes.method.JMethod;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.TypeFactory;
import tendril.codegen.field.value.JValue;

/**
 * Abstract processor which takes care of all of the heavy lifting in terms of finding the annotated elements to process for the current round, loading their details and passing them to the
 * appropriate processing method ({@code processType()} or {@code processMethod()}. The core of this processing is performed by {@code defaultConsumer()}, which triggers the processing of the
 * annotated element as either a Type (class) or method.
 */
public abstract class AbstractTendrilProccessor extends AbstractProcessor {

    /**
     * @see javax.annotation.processing.AbstractProcessor#process(java.util.Set, javax.annotation.processing.RoundEnvironment)
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        if (env.errorRaised() || env.processingOver())
            return false;

        annotations.forEach(annotation -> {
            findAndProcessElements(annotation, env);
        });
        return false;
    }

    /**
     * Creates the consumer which triggers the processing of an element based on what type of element it is.
     * 
     * @return {@link Consumer} of {@link Element}s
     */
    protected Consumer<? super Element> defaultConsumer() {
        return element -> {
            if (element instanceof TypeElement)
                prepareAndProcessType((TypeElement) element);
            else if (element instanceof ExecutableElement)
                prepareAndProcessMethod((ExecutableElement) element);
            else
                System.err.println("Unknown element type: " + element);
        };
    }

    /**
     * Finds and processing found elements through the {@code defaultConsumer()}.
     * 
     * @param annotation {@link TypeElement} representing the annotation that is being processed
     * @param env        {@link RoundEnvironment} containing the code base to process within
     */
    protected void findAndProcessElements(TypeElement annotation, RoundEnvironment env) {
        findAndProcessElements(annotation, env, defaultConsumer());
    }

    /**
     * Finds and processing found elements through the provider consumer
     * 
     * @param annotation {@link TypeElement} representing the annotation that is being processed
     * @param env        {@link RoundEnvironment} containing the code base to process within
     * @param consume    {@link Consumer} which is to process the discovered {@link Element}s
     */
    protected void findAndProcessElements(TypeElement annotation, RoundEnvironment env, Consumer<? super Element> consume) {
        env.getElementsAnnotatedWith(annotation).forEach(consume);
    }

    /**
     * Prepare the {@link TypeElement} as a Class and trigger its processing
     * 
     * @param type {@link TypeElement} of the Class
     */
    private void prepareAndProcessType(TypeElement type) {
        processType(deriveClassData(type));
    }

    /**
     * Prepare the {@link ExecutableElement} as a method and trigger its processing
     * 
     * @param element {@link ExecutableElement} of the method
     */
    private void prepareAndProcessMethod(ExecutableElement element) {
        Pair<ClassType, JMethod<?>> methodDetails = loadMethodDetails(element);
        processMethod(methodDetails.getLeft(), methodDetails.getRight());
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
    private ClassType deriveClassData(TypeElement type) {
        String typeName = type.getSimpleName().toString();
        String packageName = StringUtils.removeEnd(StringUtils.removeEnd(type.getQualifiedName().toString(), typeName), ".");
        return new ClassType(packageName, typeName);
    }

    /**
     * Perform the necessary processing of the indicated class, which was found to have been annotated with the required annotation. An empty implementation is provided by default, leaving it up to
     * the subclass to provide the necessary concrete implementation.
     * 
     * @param data {@link ClassType} representing the annotated class
     */
    protected void processType(ClassType data) {
    }

    /**
     * Perform the necessary processing of the indicated method, which was found to have been annotated with the required annotation. An empty implementation is provided by default, leaving it up to
     * the subclass to provide the necessary concrete implementation.
     * 
     * @param classData {@link ClassType} representing class in which the method is located
     * @param methodData {@link JMethod} containing the details of the method
     */
    protected void processMethod(ClassType classData, JMethod<?> methodData) {
    }
}
