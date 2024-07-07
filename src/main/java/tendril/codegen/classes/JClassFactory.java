package tendril.codegen.classes;

import tendril.codegen.VisibilityType;
import tendril.dom.type.core.ClassType;

/**
 * Factory for creating {@link JClass} representations of distinct types of classes
 */
public class JClassFactory {

	/**
	 * Create a default (non-abstract, concrete) class
	 * 
	 * @param visibility {@link VisibilityType} what the visibility of the class is
	 * @param data       {@link ClassType} the information about the class
	 * @return {@link JClass}
	 */
	public static JClass createClass(VisibilityType visibility, ClassType data) {
		return new JClassDefault(visibility, data);
	}

	/**
	 * Create an abstract class
	 * 
	 * @param visibility {@link VisibilityType} what the visibility of the class is
	 * @param data       {@link ClassType} the information about the class
	 * @return {@link JClass}
	 */
	public static JClass createAbstractClass(VisibilityType visibility, ClassType data) {
		return new JClassAbstract(visibility, data);
	}

	/**
	 * Create an interface
	 * 
	 * @param visibility {@link VisibilityType} what the visibility of the class is
	 * @param data       {@link ClassType} the information about the class
	 * @return {@link JClass}
	 */
	public static JClass createInterface(VisibilityType visibility, ClassType data) {
		return new JClassInterface(visibility, data);
	}

	/**
	 * Create an annotation
	 * 
	 * @param visibility {@link VisibilityType} what the visibility of the class is
	 * @param data       {@link ClassType} the information about the class
	 * @return {@link JClass}
	 */
	public static JClass createAnnotation(VisibilityType visibility, ClassType data) {
		return new JClassAnnotation(visibility, data);
	}
}
