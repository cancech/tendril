package tendril.codegen.classes.method;

import tendril.codegen.VisibilityType;
import tendril.codegen.classes.JClass;
import tendril.codegen.classes.MethodBuilder;
import tendril.codegen.field.type.TypeData;
import tendril.dom.method.MethodElement;
import tendril.dom.type.Type;

public class InterfaceMethodBuilder<RETURN_TYPE extends Type> extends MethodBuilder<RETURN_TYPE> {

	public InterfaceMethodBuilder(JClass encompassingClass, TypeData<RETURN_TYPE> returnType, String name) {
		super(encompassingClass, returnType, name);
	}

	@Override
	protected void validateData() throws IllegalArgumentException {
		if ((visibility == VisibilityType.PRIVATE && !hasCode())
				|| visibility != VisibilityType.PUBLIC)
			throw new IllegalArgumentException("Interface method can only be public, or private if it has an implementation");
	}

	@Override
	protected JMethod<RETURN_TYPE> buildMethod(MethodElement<RETURN_TYPE> methodElement) {
		return new JMethodInterface<RETURN_TYPE>(visibility, methodElement, linesOfCode);
	}

}
