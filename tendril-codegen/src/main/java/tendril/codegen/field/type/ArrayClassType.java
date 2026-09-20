package tendril.codegen.field.type;

import tendril.codegen.DefinitionException;
import tendril.codegen.field.value.JValue;
import tendril.codegen.field.value.JValueFactory;
import tendril.util.ArrayConverter;

/**
 * Representation of an array class.
 */
public class ArrayClassType extends ClassType {

	/**
	 * CTOR
	 * 
	 * @param packageName {@link String} the name of the package where the defined class lives
	 * @param className   {@link String} the name of the class itself
	 */
	ArrayClassType(String packageName, String className) {
		super(packageName, className);
	}

	/**
	 * @see tendril.codegen.field.type.ClassType#getClassName()
	 * 
	 * Appends [] to the class name
	 */
	@Override
	public String getClassName() {
		return super.getClassName() + "[]";
	}
	
	/**
	 * @see tendril.codegen.field.type.ClassType#getFullyQualifiedName()
	 * 
	 * Appends [] to the class name
	 */
	@Override
	public String getFullyQualifiedName() {
		return super.getFullyQualifiedName() + "[]";
	}
	
	/**
	 * @see tendril.codegen.field.type.ClassType#isAssignableFrom(tendril.codegen.field.type.Type)
	 */
	@Override
	public boolean isAssignableFrom(Type other) {
		if (isArrayType(other))
			return super.isAssignableFrom(other);
		
		return false;
	}
	
	/**
	 * @see tendril.codegen.field.type.ClassType#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (isArrayType(obj))
			return super.equals(obj);
		
		return false;
	}
	
	/**
	 * @see tendril.codegen.field.type.ClassType#isTypeOf(java.lang.Object)
	 */
	@Override
	public boolean isTypeOf(Object value) {
		if (isArrayType(value))
			return super.isTypeOf(value);
		
		return false;
	}
	
	/**
	 * Checks if the specified object is an instance of {@link ArrayClassType}
	 * 
	 * @param other {@link Object} to check
	 * @return boolean true if it is an instance of {@link ArrayClassType}
	 */
	private boolean isArrayType(Object other) {
		return other instanceof ArrayClassType;
	}
	
	/**
	 * @see tendril.codegen.field.type.ClassType#getCodeName()
	 * 
	 * Appends [] to the class name
	 */
	@Override
	public String getCodeName() {
		return super.getFullyQualifiedName() + getGenericsApplicationKeyword(false) + "[]";
	}
	
	/**
	 * @see tendril.codegen.field.type.ClassType#asValue(java.lang.Object)
	 */
	@Override
	public JValue<?, ?> asValue(Object value) {
        if (value.getClass().isArray() && super.isTypeOf(value))
            return JValueFactory.createArray(ArrayConverter.toObjectArray(value));
        
        throw new DefinitionException(this, "Incompatible value, expected " + getCodeName() + " but received " + value);
	}
}
