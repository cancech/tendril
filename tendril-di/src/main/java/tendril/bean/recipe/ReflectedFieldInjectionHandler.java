package tendril.bean.recipe;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import tendril.TendrilStartupException;

/**
 * Base class which does not know how to locate the bean to be injected, however located the field into which the bean is to be injected via reflection and performs the injection itself.
 * 
 * @param <CONSUMER> class into which the injection is to be performed
 */
class ReflectedFieldInjectionHandler<CONSUMER> {

	/** The full path to reaching the field, used for the purpose of error reporting */
	private final String fieldPath;
	/** The name of the field/variable that is to be injected */
	private final String fieldName;
	/** The annotation which is applied to the field to indicate the target of injection */
	private final Class<? extends Annotation> injectAnnotation;

	/**
	 * Supporting CTOR to cover all basis.
	 * 
	 * @param fieldPath {@link String} the full path to the field (for error reporting purposes)
	 * @param fieldName {@link String} the name of the field/variable that is to be injected
	 * @param injection {@link Class} extending {@link Annotation} representing the type of annotation employed to indicate the field is to be injected
	 */
	ReflectedFieldInjectionHandler(String fieldPath, String fieldName, Class<? extends Annotation> injection) {
		this.fieldPath = fieldPath;
		this.fieldName = fieldName;
		this.injectAnnotation = injection;
	}

	/**
	 * Inject the specified bean into the appropriate field.
	 * 
	 * @param <BEAN>   the class that is to be injected into the field
	 * @param consumer {@code CONSUMER} instance in which the field to inject exists
	 * @param bean     {@code BEAN} that is to be injected
	 */
	<BEAN> void injectBean(CONSUMER consumer, BEAN bean) {
		try {
			Field rf = findField(consumer.getClass());
			@SuppressWarnings("deprecation")
			boolean origAccess = rf.isAccessible();
			rf.setAccessible(true);
			rf.set(consumer, bean);
			rf.setAccessible(origAccess);
		} catch (Exception ex) {
			throw new TendrilStartupException("Unable to inject " + fieldPath, ex);
		}
	}

	/**
	 * Find the field for injection - the field must not only match the name, but must also be appropriately annotated
	 * 
	 * @param klass {@link Class} where to start looking for the field
	 * @return {@link Field} that was located
	 * 
	 * @throws NoSuchFieldException if the desired field cannot be found
	 * @throws SecurityException    if there are issues accessing the desired field
	 */
	private Field findField(Class<?> klass) throws NoSuchFieldException, SecurityException {
		try {
			Field rf = klass.getDeclaredField(fieldName);
			if (rf.getAnnotation(injectAnnotation) != null)
				return rf;
		} catch (NoSuchFieldException e) {
			// Ignore, means that need to check the parent class
		}

		Class<?> parent = klass.getSuperclass();
		if (parent == null)
			throw new NoSuchFieldException("No injectable field " + fieldName + " in the class hierarchy");

		return findField(parent);
	}
}
