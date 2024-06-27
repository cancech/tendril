package tendril.codegen.field.type;

import tendril.dom.type.core.VoidType;
import tendril.dom.type.value.ValueElement;

class TypeDataVoid extends TypeData<VoidType> {
    
    TypeDataVoid() {
        super(VoidType.INSTANCE, "void");
    }
    
    @Override
    public boolean isVoid() {
        return true;
    }

    @Override
    public ValueElement<VoidType, ?> asValue(Object value) {
        throw new IllegalArgumentException("A void type cannot hold any value");
    }
    
}
