package tendril.codegen.field.value;

import java.util.Set;

import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.Type;

/**
 * Representation of a value where the class itself is the value (i.e.: {@code Object.class}.
 */
public class JValueClass extends JValue<ClassType, ClassType> {

	/**
	 * CTOR
	 * 
	 * @param type {@link ClassType} representing the class
	 */
	public JValueClass(ClassType type) {
		super(type, type);
	}
	
	/**
	 * @see tendril.codegen.field.value.JValue#isInstanceOf(tendril.codegen.field.type.Type)
	 */
	@Override
	public boolean isInstanceOf(Type otherType) {
        if (otherType == null || !(otherType instanceof ClassType))
            return false;
        
        ClassType otherClass = (ClassType) otherType;
        if (!Class.class.getName().equals(otherClass.getFullyQualifiedName()))
        	return false;
        
        return otherClass.getGenerics().get(0).isAssignableFrom(type);
	}

	/**
	 * @see tendril.codegen.field.value.JValue#generate(java.util.Set)
	 */
	@Override
	public String generate(Set<ClassType> classImports) {
		classImports.add(type);
		return type.getClassName() + ".class";
	}

}
