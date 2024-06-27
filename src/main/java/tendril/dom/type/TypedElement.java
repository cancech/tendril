package tendril.dom.type;

import tendril.dom.type.core.ClassType;
import tendril.dom.type.core.PoDType;
import tendril.dom.type.core.VoidType;

/**
 * Marker interface to allow for direct comparisons between items which have a type. The following can be used:
 * 
 * <ul>
 * <li>{@link VoidType} specifically for void methods</li>
 * <li>{@link ClassType} to represent a particular class or other Declared type</li>
 * <li>{@link PoDType} to represent the different "Plain Ol' Data Types" available within Java</li>
 * </ul>
 * 
 * @param <DATA_TYPE> The representation of the type of data which is stored within.
 */
public interface TypedElement<DATA_TYPE extends Type> {

}
