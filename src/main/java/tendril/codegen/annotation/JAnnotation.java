package tendril.codegen.annotation;

import java.lang.annotation.Annotation;
import java.util.Set;

import tendril.codegen.BaseElement;
import tendril.codegen.CodeBuilder;
import tendril.metadata.classes.ClassData;

public abstract class JAnnotation extends BaseElement {
    
    private final ClassData annotationClass;

    public JAnnotation(Class<? extends Annotation> klass) {
        super("@" + klass.getSimpleName());
        this.annotationClass = new ClassData(klass);
    }
    
    @Override
    public void generate(CodeBuilder builder, Set<ClassData> classImports) {
        classImports.add(annotationClass);
        generateSelf(builder, classImports);
    }
}
