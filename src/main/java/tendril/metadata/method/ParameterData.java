package tendril.metadata.method;

import java.util.ArrayList;
import java.util.List;

import tendril.metadata.NamedTypeData;
import tendril.metadata.annotation.AppliedAnnotation;
import tendril.metadata.field.type.TypeData;

public class ParameterData<DATA_TYPE> extends NamedTypeData<DATA_TYPE> {
    
    private List<AppliedAnnotation> annotations = new ArrayList<>();

    public ParameterData(TypeData<DATA_TYPE> returnType, String name) {
        super(returnType, name);
    }
    
    public void addAnnotation(AppliedAnnotation data) {
        annotations.add(data);
    }

    public List<AppliedAnnotation> getAnnotations() {
        return annotations;
    }
}
