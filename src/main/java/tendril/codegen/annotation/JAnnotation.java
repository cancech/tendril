package tendril.codegen.annotation;

import java.lang.annotation.Annotation;
import java.util.Set;

import tendril.codegen.BaseElement;
import tendril.codegen.CodeBuilder;
import tendril.dom.type.core.ClassType;

public abstract class JAnnotation extends BaseElement {
    
    private final ClassType annotationClass;

    public JAnnotation(Class<? extends Annotation> klass) {
        super("@" + klass.getSimpleName());
        this.annotationClass = new ClassType(klass);
    }
    
    @Override
    public void generate(CodeBuilder builder, Set<ClassType> classImports) {
        classImports.add(annotationClass);
        generateSelf(builder, classImports);
    }
}
