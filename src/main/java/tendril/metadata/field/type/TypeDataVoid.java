package tendril.metadata.field.type;

import tendril.metadata.field.ValueData;

class TypeDataVoid extends TypeData<VoidType> {
    
    TypeDataVoid() {
        super(new VoidType(), "void");
    }
    
    @Override
    public boolean isVoid() {
        return true;
    }

    @Override
    public ValueData<VoidType, ?> asValue(Object value) {
        throw new IllegalArgumentException("A void type cannot hold any value");
    }
    
}
