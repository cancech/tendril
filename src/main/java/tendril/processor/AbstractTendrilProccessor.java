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
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import tendril.metadata.MethodData;
import tendril.metadata.ParameterData;
import tendril.metadata.classes.AnnotationData;
import tendril.metadata.classes.ClassData;
import tendril.metadata.field.ValueData;
import tendril.metadata.field.type.PoDType;
import tendril.metadata.field.type.TypeData;
import tendril.metadata.field.type.TypeDataFactory;

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
        Pair<ClassData, MethodData<?>> methodDetails = loadMethodDetails(element);
        processMethod(methodDetails.getLeft(), methodDetails.getRight());
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Pair<ClassData, MethodData<?>> loadMethodDetails(ExecutableElement element) {
        ClassData classData = deriveClassData((TypeElement) element.getEnclosingElement());
        List<? extends TypeMirror> parameterTypes = ((ExecutableType) element.asType()).getParameterTypes();
        List<? extends VariableElement> parameters = element.getParameters();
        if (parameterTypes.size() != parameters.size())
            throw new IllegalStateException(element + " mismatch between number of parameters and parameter types");

        MethodData<?> method = new MethodData<>(deriveType(element.getReturnType()), element.getSimpleName().toString());
        for (int i = 0; i < parameters.size(); i++) {
            VariableElement varElement = parameters.get(i);
            ParameterData paramData = new ParameterData(deriveType(parameterTypes.get(i)), varElement.getSimpleName().toString());
            for (AnnotationMirror m : varElement.getAnnotationMirrors()) {
                AnnotationData annonData = new AnnotationData(deriveClassData((TypeElement)m.getAnnotationType().asElement()));
                for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : m.getElementValues().entrySet()) {
                    Pair<ClassData, MethodData<?>> details = loadMethodDetails(entry.getKey());
                    ValueData<?,?> value = details.getRight().getType().asValue(entry.getValue().getValue());
                    annonData.addParameter(details.getRight(), value);
                }
                paramData.addAnnotation(annonData);
            }
            
            method.addParameter(paramData);
        }

        return Pair.of(classData, method);
    }

    private ClassData deriveClassData(TypeElement type) {
        String typeName = type.getSimpleName().toString();
        String packageName = StringUtils.removeEnd(StringUtils.removeEnd(type.getQualifiedName().toString(), typeName), ".");
        return new ClassData(packageName, typeName);
    }

    private TypeData<?> deriveType(TypeMirror mirror) {
        TypeKind kind = mirror.getKind();
        if (TypeKind.VOID == kind)
            return TypeDataFactory.create();
        if (TypeKind.DECLARED == kind)
            return TypeDataFactory.create(new ClassData(mirror.toString()));
        if (kind.isPrimitive())
            return TypeDataFactory.create(PoDType.valueOf(kind.toString()));

        throw new IllegalArgumentException("Unknown type: " + mirror + "[" + kind + "]");
    }
    
    protected void processType(ClassData data) {
    }

    protected void processMethod(ClassData classData, MethodData<?> methodData) {
    }
}
