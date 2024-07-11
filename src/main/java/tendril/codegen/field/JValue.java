package tendril.codegen.field;

import java.util.Set;

import tendril.dom.type.core.ClassType;

/**
 * The base representation of a value
 * 
 * @param <TYPE> indicating the type of value that is stored within
 */
public abstract class JValue<TYPE> {
    /** The specific value */
    protected final TYPE value;

    /**
     * CTOR
     * 
     * @param value TYPE the value to store
     */
    protected JValue(TYPE value) {
        this.value = value;
    }

    /**
     * Generate a code representation of the value
     * 
     * @param classImports {@link Set} of {@link ClassType} where any imports for this value are to be registered
     * @return {@link String} code representing the value
     */
    public abstract String generate(Set<ClassType> classImports);
}
