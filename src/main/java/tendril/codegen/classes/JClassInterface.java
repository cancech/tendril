package tendril.codegen.classes;

import tendril.codegen.VisibilityType;
import tendril.codegen.classes.method.InterfaceMethodBuilder;
import tendril.codegen.field.type.TypeData;
import tendril.dom.type.Type;
import tendril.dom.type.core.ClassType;

/**
 * Representation of an interface
 */
public class JClassInterface extends JClass {

	/**
	 * CTOR
	 * 
	 * @param visibility {@link VisibilityType} what the visibility of the class is
	 * @param data       {@link ClassType} the information about the class
	 */
	protected JClassInterface(VisibilityType visibility, ClassType data) {
		super(visibility, data);
	}

	/**
	 * @see tendril.codegen.classes.JClass#classType()
	 */
	@Override
	protected String classType() {
		return "interface";
	}

	/**
	 * @see tendril.codegen.classes.JClass#createMethodBuilder(tendril.codegen.field.type.TypeData, java.lang.String)
	 */
	@Override
	protected <RETURN_TYPE extends Type> MethodBuilder<RETURN_TYPE> createMethodBuilder(TypeData<RETURN_TYPE> returnType, String name) {
		return new InterfaceMethodBuilder<RETURN_TYPE>(this, returnType, name);
	}

}
