package tendril.codegen.classes;

import tendril.codegen.VisibilityType;
import tendril.dom.type.core.ClassType;

/**
 * Representation of an annotation
 */
public class JClassAnnotation extends JClassInterface {

	/**
	 * CTOR
	 * 
	 * @param visibility {@link VisibilityType} what the visibility of the class is
	 * @param data       {@link ClassType} the information about the class
	 */
	protected JClassAnnotation(VisibilityType visibility, ClassType data) {
		super(visibility, data);
	}

	/**
	 * @see tendril.codegen.classes.JClassInterface#classType()
	 */
	@Override
	protected String classType() {
		return "@" + super.classType();
	}
}
