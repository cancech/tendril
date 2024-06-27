package tendril.dom.annotation;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tendril.dom.classes.ImportElement;
import tendril.dom.method.MethodElement;
import tendril.dom.type.core.ClassType;
import tendril.dom.type.value.ValueElement;

/**
 * Representation of an annotation that has been applied to an annotatable element. This includes the details of what the annotation is (package/class name) as well as any/all values that are
 * specified for it.
 */
public class AppliedAnnotation extends ImportElement {
    /** The parameters (methods) that have been applied to the annotation */
    private final List<MethodElement<?>> parameters = new ArrayList<>();
    /** The values that have been applied to the parameters (methods) of the annotation */
    private final Map<MethodElement<?>, ValueElement<?, ?>> values = new HashMap<>();

    /**
     * CTOR
     * 
     * @param klass {@link Class} the specific Annotation class of the applied annotation
     */
    public AppliedAnnotation(Class<? extends Annotation> klass) {
        super(klass);
    }

    /**
     * CTOR
     * 
     * @param classData {@link ClassType} representing the specific Annotation class
     */
    public AppliedAnnotation(ClassType classData) {
        super(classData.getPackageName(), classData.getClassName());
    }

    /**
     * CTOR
     * 
     * @param fullyQualifiedName {@link String} the fully qualified name of the specific Annotation class
     */
    public AppliedAnnotation(String fullyQualifiedName) {
        super(fullyQualifiedName);
    }

    /**
     * CTOR
     * 
     * @param packageName {@link String} the name of the package where the specific Annotation class is located
     * @param className   {@link String} the name of the specific Annotation class
     */
    public AppliedAnnotation(String packageName, String className) {
        super(packageName, className);
    }

    /**
     * Add the defined parameter to the Annotation instance
     * 
     * @param parameter {@link MethodElement} representing the parameter method
     * @param value     {@link ValueElement} representing the value applied to the parameter
     */
    public void addParameter(MethodElement<?> parameter, ValueElement<?, ?> value) {
        parameters.add(parameter);
        values.put(parameter, value);
    }

    /**
     * Get all of the parameters specified for the Annotation
     * 
     * @return {@link List} of {@link MethodElement} representing the specified parameters
     */
    public List<MethodElement<?>> getParameters() {
        return parameters;
    }

    /**
     * Get the value for the indicated parameter
     * 
     * @param parameter {@link MethodElement} representing the desired parameter
     * @return {@link ValueElement} containing the applied value
     */
    public ValueElement<?, ?> getValue(MethodElement<?> parameter) {
        return values.get(parameter);
    }
}
