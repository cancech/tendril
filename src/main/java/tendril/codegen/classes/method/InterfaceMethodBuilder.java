package tendril.codegen.classes.method;

import tendril.codegen.VisibilityType;
import tendril.codegen.classes.JClass;
import tendril.codegen.classes.MethodBuilder;
import tendril.codegen.field.type.TypeData;
import tendril.dom.method.MethodElement;
import tendril.dom.type.Type;

/**
 * Builder for creating interface methods
 * 
 * @param <RETURN_TYPE> the {@link Type} the method returns
 */
public class InterfaceMethodBuilder<RETURN_TYPE extends Type> extends MethodBuilder<RETURN_TYPE> {

    /**
     * CTOR
     * 
     * @param encompassingClass {@link JClass} which contain the method
     * @param returnType        {@link TypeData} representing what the method returns
     * @param name              {@link String} the name of the method
     */
	public InterfaceMethodBuilder(JClass encompassingClass, TypeData<RETURN_TYPE> returnType, String name) {
		super(encompassingClass, returnType, name);
	}

	/**
	 * Interface methods may or may not have an implementation, unless they're private in which case they must have code
	 * 
	 * @see tendril.codegen.classes.MethodBuilder#validateData()
	 */
	@Override
	protected void validateData() throws IllegalArgumentException {
		if (visibility != VisibilityType.PUBLIC && !(visibility == VisibilityType.PRIVATE && hasCode()))
			throw new IllegalArgumentException("Interface method can only be public, or private if it has an implementation");
	}

	/**
	 * @see tendril.codegen.classes.MethodBuilder#buildMethod(tendril.dom.method.MethodElement)
	 */
	@Override
	protected JMethod<RETURN_TYPE> buildMethod(MethodElement<RETURN_TYPE> methodElement) {
		return new JMethodInterface<RETURN_TYPE>(visibility, methodElement, linesOfCode);
	}

}
