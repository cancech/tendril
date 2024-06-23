package tendril.metadata;

import java.util.ArrayList;
import java.util.List;

import tendril.metadata.classes.AnnotationData;
import tendril.metadata.field.type.TypeData;

public class ParameterData<METADATA> extends NamedTypeData<METADATA> {
    
    private List<AnnotationData> annotations = new ArrayList<>();

    public ParameterData(TypeData<METADATA> returnType, String name) {
        super(returnType, name);
    }
    
    public void addAnnotation(AnnotationData data) {
        annotations.add(data);
    }

    public List<AnnotationData> getAnnotations() {
        return annotations;
    }
}
