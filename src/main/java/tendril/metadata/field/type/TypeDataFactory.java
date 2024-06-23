package tendril.metadata.field.type;

import tendril.metadata.classes.ClassData;

public class TypeDataFactory {

    public static TypeData<VoidType> create() {
        return new TypeDataVoid();
    }
    
    public static TypeData<ClassData> create(Class<?> type) {
        return new TypeDataDeclared(type);
    }
    
    public static TypeData<ClassData> create(ClassData type) {
        return new TypeDataDeclared(type);
    }
    
    public static TypeData<PoDType> create(PoDType type) {
        return new TypeDataPoD(type);
    }
}
