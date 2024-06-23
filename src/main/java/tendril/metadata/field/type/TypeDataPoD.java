package tendril.metadata.field.type;

import tendril.metadata.field.ValueData;

class TypeDataPoD extends TypeData<PoDType> {

    TypeDataPoD(PoDType type) {
        super(type, type.toString());
    }

    @Override
    public ValueData<PoDType, ?> asValue(Object value) {
        switch(type) {
            case BOOLEAN:
                return new ValueData<PoDType, Boolean>(PoDType.BOOLEAN, (Boolean) value);
            case BYTE:
                return new ValueData<PoDType, Byte>(PoDType.BYTE, (Byte) value);
            case CHAR:
                return new ValueData<PoDType, Character>(PoDType.CHAR, (Character) value);
            case DOUBLE:
                return new ValueData<PoDType, Double>(PoDType.DOUBLE, (Double) value);
            case FLOAT:
                return new ValueData<PoDType, Float>(PoDType.FLOAT, (Float) value);
            case INT:
                return new ValueData<PoDType, Integer>(PoDType.INT, (Integer) value);
            case LONG:
                return new ValueData<PoDType, Long>(PoDType.LONG, (Long) value);
            case SHORT:
                return new ValueData<PoDType, Short>(PoDType.SHORT, (Short) value);
            default:
                throw new IllegalArgumentException("Invalid type specified: " + type);
        }
    }

}
