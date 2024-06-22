package tendril.codegen.annotation;

import java.lang.annotation.Annotation;
import java.util.Map;

import tendril.codegen.field.JValue;

public class JAnnotationFactory {

    public static JAnnotation create(Class<? extends Annotation> annotation) {
        return new JAnnotationMarker(annotation);
    }

    public static JAnnotation create(Class<? extends Annotation> annotation, JValue<?> value) {
        return new JAnnotationDefaultValue(annotation, value);
    }

    public static JAnnotation create(Class<? extends Annotation> annotation, Map<String, JValue<?>> parameters) {
        return new JAnnotationFull(annotation, parameters);
    }
}
