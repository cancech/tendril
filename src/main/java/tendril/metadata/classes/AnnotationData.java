package tendril.metadata.classes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tendril.metadata.MethodData;
import tendril.metadata.field.ValueData;

public class AnnotationData extends ImportData {
    
    private final List<MethodData<?>> parameters = new ArrayList<>();
    private final Map<MethodData<?>, ValueData<?,?>> values = new HashMap<>();
    
    public AnnotationData(Class<?> klass) {
        super(klass);
    }
    
    public AnnotationData(ClassData classData) {
        super(classData.getPackageName(), classData.getClassName());
    }

    public AnnotationData(String fullyQualifiedName) {
        super(fullyQualifiedName);
    }

    public AnnotationData(String packageName, String className) {
        super(packageName, className);
    }
    
    public void addParameter(MethodData<?> parameter, ValueData<?, ?> value) {
        parameters.add(parameter);
        values.put(parameter, value);
    }
    
    public List<MethodData<?>> getParameters() {
        return parameters;
    }
    
    public ValueData<?, ?> getValue(MethodData<?> parameter) {
        return values.get(parameter);
    }
}
