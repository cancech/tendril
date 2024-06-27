package tendril.dom.type.core;

import tendril.dom.type.Type;

/**
 * Type that represents a void value. Intended to be only used with methods.
 */
public class VoidType implements Type {

    /** Singleton instance that should be used to avoid creating needless copies */
    public static final VoidType INSTANCE = new VoidType();

    /**
     * CTOR hidden so that it is only used as a singleton (no need to have more than one instance floating around).
     */
    private VoidType() {
    }

    /**
     * 
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof VoidType;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "void";
    }

    /**
     * Always returns false, as void cannot be assigned to/from anything
     * 
     * @see tendril.dom.type.Type#isAssignableTo(tendril.dom.type.Type)
     */
    @Override
    public boolean isAssignableTo(Type other) {
        return false;
    }
}
