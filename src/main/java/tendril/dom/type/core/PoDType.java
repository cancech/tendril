package tendril.dom.type.core;

import tendril.dom.type.Type;

/**
 * Enumeration of the plain ol' Data Types that can be used in Java
 */
public enum PoDType implements Type {
    BOOLEAN, BYTE, CHAR, DOUBLE, FLOAT, INT, LONG, SHORT;

    /**
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return name().toLowerCase();
    }

    /**
     * @see tendril.dom.type.Type#isAssignableTo(tendril.dom.type.Type)
     */
    @Override
    public boolean isAssignableTo(Type other) {
        return this == other;
    }
}
