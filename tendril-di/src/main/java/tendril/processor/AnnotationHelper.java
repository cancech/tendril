package tendril.processor;

import tendril.bean.Bean;
import tendril.bean.duplicate.Duplicate;
import tendril.codegen.JBase;
import tendril.codegen.annotation.JAnnotation;
import tendril.codegen.classes.method.JMethod;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.TypeFactory;
import tendril.codegen.field.value.JValueClass;

/**
 * Helper for retrieving values from annotations
 */
public class AnnotationHelper {
	/** The annotation which denotes a duplicate */
	private static final ClassType duplicateAnnotation = TypeFactory.createClassType(Duplicate.class);

	/**
	 * Retrieve the blueprint class from the {@link Duplicate} annotated element
	 * 
	 * @param element {@link JBase} element on which the annotation is expected to be placed
	 * @return {@link ClassType} indicated in the {@link Duplicate} annotation as the blueprint or {@code null} if it is not retrieved
	 */
	public static ClassType retrieveDuplicateBlueprint(JBase element) {
		return retrieveClassType(element, duplicateAnnotation, "value");
	}

	/**
	 * Retrieve the a class attribute from an annotation applies to an element
	 * 
	 * @param element    {@link JBase} element on which the annotation is expected to be placed
	 * @param annotation {@link ClassType} of the annotation to containing the attribute
	 * @param attribute  {@link String} name of the attribute whose class value to retrieve
	 * @return {@link ClassType} indicated in the {@link Bean} annotation as the override or {@code null} if it is not retrieved
	 */
	public static ClassType retrieveClassType(JBase element, ClassType annotation, String attribute) {
		JAnnotation a = element.getAnnotation(annotation);
		if (a == null)
			return null;

		for (JMethod<?> m : a.getAttributes()) {
			if (m.getName().equals(attribute)) {
				if (a.getValue(m) instanceof JValueClass type)
					return type.getValue().getGenerics().get(0).asClassType();
			}
		}

		return null;
	}

}
