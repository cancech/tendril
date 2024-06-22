package tendril.metadata;

public class NamedTypeData {
    
    private final TypeData returnType;
    private final String name;

    public NamedTypeData(TypeData returnType, String name) {
        this.returnType = returnType;
        this.name = name;
    }
 
    public TypeData getReturnType() {
        return returnType;
    }
    
    public String getName() {
        return name;
    }
}
