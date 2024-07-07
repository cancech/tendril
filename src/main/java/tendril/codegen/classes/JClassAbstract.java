package tendril.codegen.classes;

import tendril.codegen.VisibilityType;
import tendril.codegen.classes.method.AbstractMethodBuilder;
import tendril.codegen.field.type.TypeData;
import tendril.dom.type.Type;
import tendril.dom.type.core.ClassType;

/**
 * Representation of an abstract class
 */
public class JClassAbstract extends JClassDefault {

	/**
	 * CTOR
	 * 
	 * @param visibility {@link VisibilityType} what the visibility of the class is
	 * @param data       {@link ClassType} the information about the class
	 */
	protected JClassAbstract(VisibilityType visibility, ClassType data) {
		super(visibility, data);
	}

	/**
	 * @see tendril.codegen.classes.JClassDefault#classType()
	 */
	@Override
	protected String classType() {
		return "abstract " + super.classType();
	}

	/**
	 * @see tendril.codegen.classes.JClassDefault#createMethodBuilder(tendril.codegen.field.type.TypeData, java.lang.String)
	 */
	@Override
	protected <RETURN_TYPE extends Type> MethodBuilder<RETURN_TYPE> createMethodBuilder(TypeData<RETURN_TYPE> returnType, String name) {
		return new AbstractMethodBuilder<RETURN_TYPE>(this, returnType, name);
	}
}
