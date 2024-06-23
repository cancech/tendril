package tendril.codegen.annotation;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;

import tendril.codegen.CodeBuilder;
import tendril.codegen.field.JValue;
import tendril.metadata.classes.ClassData;

public class JAnnotationFull extends JAnnotation {
    
    private final Map<String, JValue<?>> parameters;

    public JAnnotationFull(Class<? extends Annotation> klass, Map<String, JValue<?>> parameters) {
        super(klass);
        this.parameters = parameters;
    }

    @Override
    protected void generateSelf(CodeBuilder builder, Set<ClassData> classImports) {
        String code = name + "(";
        
        int remaining = parameters.size();
        for (Map.Entry<String, JValue<?>> entry : parameters.entrySet()) {
            code += entry.getKey() + " = " + entry.getValue().generate(classImports);
            if (--remaining > 0)
                code += ", ";
        }
        
        builder.append(code + ")");
    }

    
}
