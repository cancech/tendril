package tendril.processor;

import tendril.bean.duplicate.Duplicate;
import tendril.codegen.JBase;
import tendril.codegen.annotation.JAnnotation;
import tendril.codegen.classes.method.JMethod;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.TypeFactory;
import tendril.codegen.field.value.JValueClass;

/**
 * Helper when dealing with processing {@link Duplicate} annotated elements
 */
public class BlueprintHelper {
	/** The annotation which denotes a duplicate */
	private static final ClassType duplicateAnnotation = TypeFactory.createClassType(Duplicate.class);
	
	/**
	 * Retrieve the blueprint class from the {@link Duplicate} annotated element
	 * 
	 * @param element {@link JBase} element on which the annotation is expected to be placed
	 * @return {@link ClassType} indicated in the {@link Duplicate} annotation as the blueprint or {@code null} if it is not retrieved
	 */
	public static ClassType retrieveBlueprint(JBase element) {
		for (JAnnotation a : element.getAnnotations()) {
			if (duplicateAnnotation.equals(a.getType())) {
				for (JMethod<?> m : a.getAttributes()) {
					if (m.getName().equals("value")) {
						if (a.getValue(m) instanceof JValueClass type)
							return type.getValue().getGenerics().get(0).asClassType();
					}
				}
			}
		}

		return null;
	}
}
