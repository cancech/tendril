package tendril.codegen.annotation;

import java.lang.annotation.Annotation;
import java.util.Set;

import tendril.codegen.CodeBuilder;
import tendril.codegen.field.JValue;
import tendril.dom.type.core.ClassType;

public class JAnnotationDefaultValue extends JAnnotation {
    
    private final JValue<?> value;

    public JAnnotationDefaultValue(Class<? extends Annotation> klass, JValue<?> value) {
        super(klass);
        this.value = value;
    }
    
    @Override
    protected void generateSelf(CodeBuilder builder, Set<ClassType> classImports) {
        builder.append(name + "(" + value.generate(classImports) + ")");
    }

}
