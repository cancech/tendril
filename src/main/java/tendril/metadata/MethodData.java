package tendril.metadata;

import java.util.ArrayList;
import java.util.List;

import tendril.metadata.field.type.TypeData;

public class MethodData<METADATA> extends NamedTypeData<METADATA> {
    
    private final List<ParameterData<METADATA>> parameters = new ArrayList<>();
    
    public MethodData(TypeData<METADATA> returnType, String name) {
        super(returnType, name);
    }
    
    public void addParameter(ParameterData<METADATA> parameter) {
        parameters.add(parameter);
    }
    
    public List<ParameterData<METADATA>> getParameters() {
        return parameters;
    }
}
