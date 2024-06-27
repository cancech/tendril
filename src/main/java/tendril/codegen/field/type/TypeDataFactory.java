package tendril.codegen.field.type;

import tendril.dom.type.core.ClassType;
import tendril.dom.type.core.PoDType;
import tendril.dom.type.core.VoidType;

public class TypeDataFactory {

    public static TypeData<VoidType> create() {
        return new TypeDataVoid();
    }
    
    public static TypeData<ClassType> create(Class<?> type) {
        return new TypeDataDeclared(type);
    }
    
    public static TypeData<ClassType> create(ClassType type) {
        return new TypeDataDeclared(type);
    }
    
    public static TypeData<PoDType> create(PoDType type) {
        return new TypeDataPoD(type);
    }
}
