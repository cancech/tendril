package tendril.metadata;

import tendril.metadata.field.type.TypeData;

public class NamedTypeData<DATA_TYPE> implements TypedElement<DATA_TYPE> {
    
    private final TypeData<DATA_TYPE> returnType;
    private final String name;

    public NamedTypeData(TypeData<DATA_TYPE> returnType, String name) {
        this.returnType = returnType;
        this.name = name;
    }
 
    public TypeData<DATA_TYPE> getType() {
        return returnType;
    }
    
    public String getName() {
        return name;
    }
}
