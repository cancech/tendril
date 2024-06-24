package tendril.metadata.field;

import tendril.metadata.TypedElement;

public class ValueData<DATA_TYPE, VALUE_TYPE> implements TypedElement<DATA_TYPE> {

    private final DATA_TYPE type;
    private final VALUE_TYPE value;
    
    public ValueData(DATA_TYPE type, VALUE_TYPE value) {
        this.type = type;
        this.value = value;
    }
    
    public boolean isInstanceOf(DATA_TYPE otherType) {
        if (otherType == null || type == null)
            return false;
        
        return otherType.equals(type);
    }
    
    public DATA_TYPE getType() {
        return type;
    }
    
    public VALUE_TYPE getValue() {
        return value;
    }
}
