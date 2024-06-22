package tendril.processor;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import org.apache.commons.lang3.StringUtils;

import tendril.bean.TempEnum;
import tendril.codegen.PoDType;
import tendril.metadata.ClassData;
import tendril.metadata.MethodData;
import tendril.metadata.ParameterData;
import tendril.metadata.TypeData;

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
                prepareMethod((ExecutableElement)element);
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
        ClassData classData = deriveClassData((TypeElement) element.getEnclosingElement());
        List<? extends TypeMirror> parameterTypes = ((ExecutableType)element.asType()).getParameterTypes();
        List<? extends VariableElement> parameters = element.getParameters();
        if (parameterTypes.size() != parameters.size())
            throw new IllegalStateException(element + " mismatch between number of parameters and parameter types");
        
        MethodData method = new MethodData(deriveType(element.getReturnType()), element.getSimpleName().toString());
        for (int i = 0; i < parameters.size(); i++) {
            method.addParameter(new ParameterData(deriveType(parameterTypes.get(i)), parameters.get(i).getSimpleName().toString()));
        }
        
//        for (VariableElement vel: parameters) {
//            System.out.println("Vel: " + vel);
//            System.out.println("Vel kind: " + vel.getKind());
//            System.out.println("Vel annotation: " + vel.getAnnotationsByType(TempEnum.class).length);
//        }
        processMethod(classData, method);
    }
    
    private ClassData deriveClassData(TypeElement type) {
        String typeName = type.getSimpleName().toString();
        String packageName = StringUtils.removeEnd(StringUtils.removeEnd(type.getQualifiedName().toString(), typeName), ".");
        return new ClassData(packageName, typeName);
    }
    
    private TypeData deriveType(TypeMirror mirror) {
        TypeKind kind = mirror.getKind();
        if (TypeKind.VOID == kind)
            return new TypeData();
        if (TypeKind.DECLARED == kind)
            return new TypeData(new ClassData(mirror.toString()));
        if (kind.isPrimitive())
            return new TypeData(PoDType.valueOf(kind.toString()));
        
        throw new IllegalArgumentException("Unknown type: " + mirror + "[" + kind + "]");
    }
    
    protected void processType(ClassData data) {}
    
    protected void processMethod(ClassData classData, MethodData methodData) {}
}
