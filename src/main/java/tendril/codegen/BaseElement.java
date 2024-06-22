package tendril.codegen;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import tendril.codegen.annotation.JAnnotation;
import tendril.codegen.annotation.JAnnotationFactory;
import tendril.codegen.field.JValue;
import tendril.metadata.ClassData;

public abstract class BaseElement {
    
    protected final String name;
    
    private final List<JAnnotation> annotations = new ArrayList<>();
    
    protected BaseElement(String name) {
        this.name = name;
    }

    public void annotate(Class<? extends Annotation> annotation) {
        annotations.add(JAnnotationFactory.create(annotation));
    }

    public void annotate(Class<? extends Annotation> annotation, JValue<?> value) {
        annotations.add(JAnnotationFactory.create(annotation, value));
    }
    
    public void annotate(Class<? extends Annotation> annotation, Map<String, JValue<?>> parameters) {
        annotations.add(JAnnotationFactory.create(annotation, parameters));
    }
    
    public void generate(CodeBuilder builder, Set<ClassData> classImports) {
        for (JAnnotation annon: annotations)
            annon.generate(builder, classImports);
        generateSelf(builder, classImports);
    }
    
    protected abstract void generateSelf(CodeBuilder builder, Set<ClassData> classImports);
}
