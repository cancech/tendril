package tendril.codegen.annotation;

import java.lang.annotation.Annotation;
import java.util.Set;

import tendril.codegen.CodeBuilder;
import tendril.dom.type.core.ClassType;

/**
 * Annotation which takes no parameters, merely acting as
 */
public class JAnnotationMarker extends JAnnotation {

	/**
	 * CTOR
	 * 
	 * @param klass {@link Class} extending {@link Annotation} defining the annotation
	 */
	public JAnnotationMarker(Class<? extends Annotation> klass) {
		super(klass);

		// Make sure that the interface is in face a marker
		if (klass.getDeclaredMethods().length != 0)
			throw new IllegalArgumentException(getName() + " is not a marker annotation, it has parameters.");
	}

	/**
	 * @see tendril.codegen.BaseElement#generateSelf(tendril.codegen.CodeBuilder, java.util.Set)
	 */
	@Override
	protected void generateSelf(CodeBuilder builder, Set<ClassType> classImports) {
		builder.append(name);
	}
}
