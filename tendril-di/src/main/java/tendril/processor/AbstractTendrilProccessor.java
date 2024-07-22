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
import tendril.codegen.classes.method.AnonymousMethod;
import tendril.codegen.classes.method.JMethod;
import tendril.codegen.field.NamedType;
import tendril.codegen.field.ParameterType;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.TypeFactory;
import tendril.codegen.field.value.JValue;

public abstract class AbstractTendrilProccessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        if (env.errorRaised() || env.processingOver())
            return false;

        annotations.forEach(annotation -> {
            findAndProcessElements(annotation, env);
        });
        return false;
    }

    protected Consumer<? super Element> defaultConsumer() {
        return element -> {
            if (element instanceof TypeElement)
                prepareType((TypeElement) element);
            else if (element instanceof ExecutableElement)
                prepareMethod((ExecutableElement) element);
            else
                System.err.println("Unknown element type: " + element);
        };
    }

    protected void findAndProcessElements(TypeElement annotation, RoundEnvironment env) {
        findAndProcessElements(annotation, env, defaultConsumer());
    }

    protected void findAndProcessElements(TypeElement annotation, RoundEnvironment env, Consumer<? super Element> consume) {
        env.getElementsAnnotatedWith(annotation).forEach(consume);
    }

    private void prepareType(TypeElement type) {
        processType(deriveClassData(type));
    }

    private void prepareMethod(ExecutableElement element) {
        Pair<ClassType, JMethod<?>> methodDetails = loadMethodDetails(element);
        processMethod(methodDetails.getLeft(), methodDetails.getRight());
    }

    private Pair<ClassType, JMethod<?>> loadMethodDetails(ExecutableElement element) {
        ClassType classData = deriveClassData((TypeElement) element.getEnclosingElement());
        List<? extends TypeMirror> parameterTypes = ((ExecutableType) element.asType()).getParameterTypes();
        List<? extends VariableElement> parameters = element.getParameters();
        if (parameterTypes.size() != parameters.size())
            throw new IllegalStateException(element + " mismatch between number of parameters and parameter types");

        JMethod<?> method = new AnonymousMethod<>(TypeFactory.create(element.getReturnType()), element.getSimpleName().toString());
        for (int i = 0; i < parameters.size(); i++) {
            VariableElement varElement = parameters.get(i);
            NamedType<?> paramData = new ParameterType<>(TypeFactory.create(parameterTypes.get(i)), varElement.getSimpleName().toString());
            for (AnnotationMirror m : varElement.getAnnotationMirrors()) {
                JAnnotation annonData = new JAnnotation(deriveClassData((TypeElement)m.getAnnotationType().asElement()));
                for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : m.getElementValues().entrySet()) {
                    Pair<ClassType, JMethod<?>> details = loadMethodDetails(entry.getKey());
                    JValue<?,?> value = details.getRight().getType().asValue(entry.getValue().getValue());
                    annonData.addAttribute(details.getRight(), value);
                }
                paramData.annotate(annonData);
            }
            
            method.addParameter(paramData);
        }

        return Pair.of(classData, method);
    }

    private ClassType deriveClassData(TypeElement type) {
        String typeName = type.getSimpleName().toString();
        String packageName = StringUtils.removeEnd(StringUtils.removeEnd(type.getQualifiedName().toString(), typeName), ".");
        return new ClassType(packageName, typeName);
    }

    protected void processType(ClassType data) {
    }

    protected void processMethod(ClassType classData, JMethod<?> methodData) {
    }
}
