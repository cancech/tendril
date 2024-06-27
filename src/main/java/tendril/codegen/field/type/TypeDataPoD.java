package tendril.codegen.field.type;

import tendril.dom.type.core.PoDType;
import tendril.dom.type.value.ValueElement;

class TypeDataPoD extends TypeData<PoDType> {

    TypeDataPoD(PoDType type) {
        super(type, type.toString());
    }

    @Override
    public ValueElement<PoDType, ?> asValue(Object value) {
        switch(type) {
            case BOOLEAN:
                return new ValueElement<PoDType, Boolean>(PoDType.BOOLEAN, (Boolean) value);
            case BYTE:
                return new ValueElement<PoDType, Byte>(PoDType.BYTE, (Byte) value);
            case CHAR:
                return new ValueElement<PoDType, Character>(PoDType.CHAR, (Character) value);
            case DOUBLE:
                return new ValueElement<PoDType, Double>(PoDType.DOUBLE, (Double) value);
            case FLOAT:
                return new ValueElement<PoDType, Float>(PoDType.FLOAT, (Float) value);
            case INT:
                return new ValueElement<PoDType, Integer>(PoDType.INT, (Integer) value);
            case LONG:
                return new ValueElement<PoDType, Long>(PoDType.LONG, (Long) value);
            case SHORT:
                return new ValueElement<PoDType, Short>(PoDType.SHORT, (Short) value);
            default:
                throw new IllegalArgumentException("Invalid type specified: " + type);
        }
    }

}
