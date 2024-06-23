package tendril.metadata.field;

import tendril.metadata.ParameterData;
import tendril.metadata.field.type.TypeData;

public class FieldData<METADATA, TYPE> extends ParameterData<METADATA> {
    
    private final ValueData<METADATA, TYPE> value;

    public FieldData(TypeData<METADATA> returnType, String name, ValueData<METADATA, TYPE> value) {
        super(returnType, name);

        if (returnType.isVoid())
            throw new IllegalArgumentException("A Field cannot have a void type");
        isCorrectType(returnType, value);
        
        this.value = value;
    }

    private void isCorrectType(TypeData<METADATA> returnType, ValueData<METADATA, TYPE> value) {
        METADATA expectedType = returnType.getMetaData();
        if (!value.isInstanceOf(expectedType))
            throw new IllegalArgumentException("Type mismatch field expects " + expectedType + " but value " + value.getType());
    }

    public ValueData<METADATA, TYPE> getValue() {
        return value;
    }
}
