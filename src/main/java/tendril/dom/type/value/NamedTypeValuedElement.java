package tendril.dom.type.value;

import tendril.codegen.field.type.TypeData;
import tendril.dom.type.NamedTypeElement;
import tendril.dom.type.Type;

/**
 * Represents a {@link NamedTypeElement} which has a value assigned to it.
 * 
 * @param <DATA_TYPE>  indicates the type of data that is to be stored in the field
 * @param <VALUE_TYPE> indicates wrapping data structure of the applied value
 */
public class NamedTypeValuedElement<DATA_TYPE extends Type, VALUE_TYPE> extends NamedTypeElement<DATA_TYPE> {
    /** The value that is applied to the field */
    private final ValueElement<DATA_TYPE, VALUE_TYPE> value;

    /**
     * CTOR
     * 
     * @param type  {@link TypeData} indicating the data structure stored in the element
     * @param name  {@link String} the name of the element
     * @param value {@link ValueElement} representing the specific value stored in the element
     * @throws IllegalArgumentException if there is a type mismatch between the value and what is expected
     */
    public NamedTypeValuedElement(TypeData<DATA_TYPE> type, String name, ValueElement<DATA_TYPE, VALUE_TYPE> value) {
        super(type, name);

        // Validate the data to ensure that it is sensible
        if (type.isVoid())
            throw new IllegalArgumentException(NamedTypeValuedElement.class.getSimpleName() + " cannot have a void type");
        isCorrectType(type, value);

        this.value = value;
    }

    /**
     * Verify that the value matches the desired type
     * 
     * @param type  {@link TypeData} indicating the data structure stored in the element
     * @param value {@link ValueElement} representing the specific value stored in the element
     * @throws IllegalArgumentException if there is a type mismatch between the value and what is expected
     */
    private void isCorrectType(TypeData<DATA_TYPE> type, ValueElement<DATA_TYPE, VALUE_TYPE> value) throws IllegalArgumentException {
        DATA_TYPE expectedType = type.getDataType();
        if (!value.isInstanceOf(expectedType))
            throw new IllegalArgumentException("Type mismatch field expects " + expectedType + " but value " + value.getType());
    }

    /**
     * Get the value assigned to the element.
     * 
     * @return {@link ValueElement} representing the value
     */
    public ValueElement<DATA_TYPE, VALUE_TYPE> getValue() {
        return value;
    }
}
