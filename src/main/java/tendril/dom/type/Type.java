package tendril.dom.type;

/**
 * Marker interface through which to identify legitimate types for {@link TypedElement}s
 */
public interface Type {

    /**
     * Check if this can be assigned to the other type
     * 
     * @param other {@link Type} to check assignment to
     * @return boolean true if this can be assigned to other
     */
    boolean isAssignableTo(Type other);

}
