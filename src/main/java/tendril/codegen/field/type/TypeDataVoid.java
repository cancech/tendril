package tendril.codegen.field.type;

import tendril.dom.type.core.VoidType;
import tendril.dom.type.value.ValueElement;

/**
 * Representation of a void data type
 */
class TypeDataVoid extends TypeData<VoidType> {
    
    /**
     * CTOR
     */
    TypeDataVoid() {
        super(VoidType.INSTANCE, "void");
    }
    
    /**
     * @see tendril.codegen.field.type.TypeData#isVoid()
     */
    @Override
    public boolean isVoid() {
        return true;
    }

    /**
     * Cannot produce a value for a void data type. IllegalArgumentException is thrown when attempted.
     * 
     * @see tendril.codegen.field.type.TypeData#asValue(java.lang.Object)
     */
    @Override
    public ValueElement<VoidType, ?> asValue(Object value) {
        throw new IllegalArgumentException("A void type cannot hold any value");
    }
    
}
