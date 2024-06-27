package tendril.codegen.annotation;

import java.lang.annotation.Annotation;
import java.util.Set;

import tendril.codegen.CodeBuilder;
import tendril.dom.type.core.ClassType;

public class JAnnotationMarker extends JAnnotation {

    public JAnnotationMarker(Class<? extends Annotation> klass) {
        super(klass);
    }

    @Override
    protected void generateSelf(CodeBuilder builder, Set<ClassType> classImports) {
        builder.append(name);
    }
}
