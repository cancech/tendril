package tendril.dom.type.value;

import tendril.dom.type.Type;
import tendril.dom.type.TypedElement;

/**
 * A representation of a value. The type is a metadata class indicating the type (via the concrete {@link Type} marked class) and the data itself is stored as either raw data or a wrapper
 * representation of the raw value.
 * 
 * @param <DATA_TYPE>  extends {@link Type} to indicate the type of value that is stored
 * @param <VALUE_TYPE> the "raw data" that is stored to represent the value
 */
public class ValueElement<DATA_TYPE extends Type, VALUE_TYPE> implements TypedElement<DATA_TYPE> {
    /** Representation of the type of data stored */
    private final DATA_TYPE type;
    /** Representation of the "raw data" of the value */
    private final VALUE_TYPE value;

    /**
     * CTOR
     * 
     * @param type  DATA_TYPE representing what the data structure type of the value is
     * @param value VALUE_TYPE wrapping the data of the value
     */
    public ValueElement(DATA_TYPE type, VALUE_TYPE value) {
        this.type = type;
        this.value = value;
    }

    /**
     * Perform a check to verify if the value matches the other data type. Or to put it more explicitly, can this type be assigned to the otherType.
     * 
     * @param otherType DATA_TYPE of the type to be checked against
     * @return boolean true if this value is an instance of the other type
     */
    public boolean isInstanceOf(Type otherType) {
        if (otherType == null || type == null)
            return false;

        return type.isAssignableTo(otherType);
    }

    /**
     * Get the type of this value
     * 
     * @return DATA_TYPE
     */
    public DATA_TYPE getType() {
        return type;
    }

    /**
     * Get the wrapper value data
     * 
     * @return VALUE_TYPE wrapping the "raw data" of the value
     */
    public VALUE_TYPE getValue() {
        return value;
    }
}
