package tendril.codegen.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import tendril.codegen.CodeBuilder;
import tendril.codegen.field.JValue;
import tendril.dom.type.core.ClassType;

/**
 * Annotation which has one or more parameters, which need not be default
 */
public class JAnnotationFull extends JAnnotation {
	/** Mapping of parameter name to its value */
	private final Map<String, JValue<?>> parameters;

	/**
	 * CTOR
	 * 
	 * @param klass      {@link Class} extending {@link Annotation} defining the annotation
	 * @param parameters {@link Map} of {@link String} parameter names to their associated {@link JValue} value representation
	 */
	public JAnnotationFull(Class<? extends Annotation> klass, Map<String, JValue<?>> parameters) {
		super(klass);
		this.parameters = parameters;

		// Make sure that the annotation has at least one parameter
		List<Method> methods = Arrays.asList(klass.getDeclaredMethods());
		if (methods.size() == 0)
			throw new IllegalArgumentException(getName() + " annotation must have at least one parameter");

		// Make sure that all of the specified parameters apply to the annotation
		for (String s : parameters.keySet()) {
			boolean found = false;
			for (Method m : methods) {
				if (m.getName().equals(s)) {
					found = true;
					break;
				}
			}

			if (!found)
				throw new IllegalArgumentException("Specified paramter " + s + " does not appear in " + getName());
		}
	}

	/**
	 * @see tendril.codegen.BaseElement#generateSelf(tendril.codegen.CodeBuilder, java.util.Set)
	 */
	@Override
	protected void generateSelf(CodeBuilder builder, Set<ClassType> classImports) {
		String code = name + "(";

		int remaining = parameters.size();
		List<String> orderedNames = new ArrayList<>(parameters.keySet());
		orderedNames.sort((lhs, rhs) -> lhs.compareTo(rhs));
		for (String name : orderedNames) {
			code += name + " = " + parameters.get(name).generate(classImports);
			if (--remaining > 0)
				code += ", ";
		}

		builder.append(code + ")");
	}

}
