package tendril.metadata;

import tendril.metadata.field.type.TypeData;

public class NamedTypeData<METADATA> {
    
    private final TypeData<METADATA> returnType;
    private final String name;

    public NamedTypeData(TypeData<METADATA> returnType, String name) {
        this.returnType = returnType;
        this.name = name;
    }
 
    public TypeData<METADATA> getType() {
        return returnType;
    }
    
    public String getName() {
        return name;
    }
}
