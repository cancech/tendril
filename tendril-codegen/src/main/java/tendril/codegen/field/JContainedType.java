package tendril.codegen.field;

import tendril.codegen.field.type.Type;

/**
 * Represents a type which can be contained within another (such as a method in a class, a parameter in a method).
 * 
 * @param <DATA_TYPE> the {@link Type} indicating what kind of data structure is represented
 */
public abstract class JContainedType<DATA_TYPE extends Type> extends JType<DATA_TYPE> {
	/** The container that this type appears in */
	private JContainedType<?> parentContainer = null;
	
    /**
     * CTOR
     * 
     * @param type DATA_TYPE of the element
     * @param name {@link String} of the element
     */
	public JContainedType(DATA_TYPE type, String name) {
		super(type, name);
	}
    
	/**
	 * Set the container that this type belongs within
	 * 
	 * @param parent {@link JContainedType} this type belong within
	 */
    public void setContainer(JContainedType<?> parent) {
    	parentContainer = parent;
    }
    
    /**
     * Get the container in which this type is contained
     * 
     * @return {@link JContainedType} containing this type
     */
    public JContainedType<?> getContainer() {
    	return parentContainer;
    }

}
