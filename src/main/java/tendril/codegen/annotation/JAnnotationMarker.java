package tendril.codegen.annotation;

import java.lang.annotation.Annotation;
import java.util.Set;

import tendril.codegen.CodeBuilder;
import tendril.metadata.ClassData;

public class JAnnotationMarker extends JAnnotation {

    public JAnnotationMarker(Class<? extends Annotation> klass) {
        super(klass);
    }

    @Override
    protected void generateSelf(CodeBuilder builder, Set<ClassData> classImports) {
        builder.append(name);
    }
}
