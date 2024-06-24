package tendril.metadata.annotation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tendril.metadata.classes.ClassData;
import tendril.metadata.classes.ImportData;
import tendril.metadata.field.ValueData;
import tendril.metadata.method.MethodData;

public class AppliedAnnotation extends ImportData {
    
    private final List<MethodData<?>> parameters = new ArrayList<>();
    private final Map<MethodData<?>, ValueData<?,?>> values = new HashMap<>();
    
    public AppliedAnnotation(Class<?> klass) {
        super(klass);
    }
    
    public AppliedAnnotation(ClassData classData) {
        super(classData.getPackageName(), classData.getClassName());
    }

    public AppliedAnnotation(String fullyQualifiedName) {
        super(fullyQualifiedName);
    }

    public AppliedAnnotation(String packageName, String className) {
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
