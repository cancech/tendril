package tendril.metadata.field;

import tendril.metadata.MetaData;

public class ValueData<METADATA, TYPE> implements MetaData<METADATA> {

    private final METADATA type;
    private final TYPE value;
    
    public ValueData(METADATA type, TYPE value) {
        this.type = type;
        this.value = value;
    }
    
    public boolean isInstanceOf(METADATA otherType) {
        if (otherType == null || type == null)
            return false;
        
        return otherType.equals(type);
    }
    
    public METADATA getType() {
        return type;
    }
    
    public TYPE getValue() {
        return value;
    }
}
