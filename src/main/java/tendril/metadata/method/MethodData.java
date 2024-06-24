package tendril.metadata.method;

import java.util.ArrayList;
import java.util.List;

import tendril.metadata.NamedTypeData;
import tendril.metadata.field.type.TypeData;

public class MethodData<DATA_TYPE> extends NamedTypeData<DATA_TYPE> {
    
    private final List<ParameterData<DATA_TYPE>> parameters = new ArrayList<>();
    
    public MethodData(TypeData<DATA_TYPE> returnType, String name) {
        super(returnType, name);
    }
    
    public void addParameter(ParameterData<DATA_TYPE> parameter) {
        parameters.add(parameter);
    }
    
    public List<ParameterData<DATA_TYPE>> getParameters() {
        return parameters;
    }
}
