package tendril.metadata;

import java.util.ArrayList;
import java.util.List;

public class MethodData extends NamedTypeData {
    
    private final List<ParameterData> parameters = new ArrayList<ParameterData>();
    
    public MethodData(TypeData returnType, String name) {
        super(returnType, name);
    }
    
    public void addParameter(ParameterData parameter) {
        parameters.add(parameter);
    }
    
    public List<ParameterData> getParameters() {
        return parameters;
    }
}
