package tendril.codegen.classes.method;

import tendril.codegen.classes.JClass;
import tendril.codegen.classes.MethodBuilder;
import tendril.codegen.field.type.TypeData;
import tendril.dom.method.MethodElement;
import tendril.dom.type.Type;

public class ConcreteMethodBuilder<RETURN_TYPE extends Type> extends MethodBuilder<RETURN_TYPE> {

	public ConcreteMethodBuilder(JClass encompassingClass, TypeData<RETURN_TYPE> returnType, String name) {
		super(encompassingClass, returnType, name);
	}

	@Override
	protected void validateData() throws IllegalArgumentException {
		if (!hasCode())
			throw new IllegalArgumentException("Concrete methods much have an implementation");
	}

	@Override
	protected JMethod<RETURN_TYPE> buildMethod(MethodElement<RETURN_TYPE> methodElement) {
		return new JMethodDefault<RETURN_TYPE>(visibility, methodElement, linesOfCode);
	}

}
