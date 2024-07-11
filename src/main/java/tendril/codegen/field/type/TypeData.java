package tendril.codegen.field.type;

import java.util.Set;

import tendril.dom.classes.Importable;
import tendril.dom.type.Type;
import tendril.dom.type.TypedElement;
import tendril.dom.type.core.ClassType;
import tendril.dom.type.value.ValueElement;

/**
 * Representation of the {@link Type} of a given data
 * 
 * @param <DATA_TYPE> extends {@link Type}
 */
public abstract class TypeData<DATA_TYPE extends Type> implements Importable, TypedElement<DATA_TYPE> {
    /** The specific representation of the type */
    protected final DATA_TYPE type;
    /** The name of the type */
    private final String name;

    /**
     * CTOR
     * 
     * @param type DATA_TYPE representing the data type
     * @param name {@link String} the name (code) of the type
     */
    protected TypeData(DATA_TYPE type, String name) {
        this.type = type;
        this.name = name;
    }

    /**
     * Check if the type is void.
     * 
     * @return boolean true if this is a void type
     */
    public boolean isVoid() {
        return false;
    }

    /**
     * Get the data type
     * 
     * @return DATA_TYPE
     */
    public DATA_TYPE getDataType() {
        return type;
    }

    /**
     * Get the simple name/code of the type
     * 
     * @return {@link String}
     */
    public String getSimpleName() {
        return name;
    }

    /**
     * @see tendril.dom.classes.Importable#registerImport(java.util.Set)
     */
    @Override
    public void registerImport(Set<ClassType> classImports) {
    }

    /**
     * Create a value for the DATA_TYPE
     * 
     * @param value {@link Object} the value to employ
     * @return {@link ValueElement}
     */
    public abstract ValueElement<DATA_TYPE, ?> asValue(Object value);
}
