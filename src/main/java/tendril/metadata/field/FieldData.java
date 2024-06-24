package tendril.metadata.field;

import tendril.metadata.field.type.TypeData;
import tendril.metadata.method.ParameterData;

public class FieldData<DATA_TYPE, VALUE_TYPE> extends ParameterData<DATA_TYPE> {
    
    private final ValueData<DATA_TYPE, VALUE_TYPE> value;

    public FieldData(TypeData<DATA_TYPE> returnType, String name, ValueData<DATA_TYPE, VALUE_TYPE> value) {
        super(returnType, name);

        if (returnType.isVoid())
            throw new IllegalArgumentException("A Field cannot have a void type");
        isCorrectType(returnType, value);
        
        this.value = value;
    }

    private void isCorrectType(TypeData<DATA_TYPE> returnType, ValueData<DATA_TYPE, VALUE_TYPE> value) {
        DATA_TYPE expectedType = returnType.getDataType();
        if (!value.isInstanceOf(expectedType))
            throw new IllegalArgumentException("Type mismatch field expects " + expectedType + " but value " + value.getType());
    }

    public ValueData<DATA_TYPE, VALUE_TYPE> getValue() {
        return value;
    }
}
