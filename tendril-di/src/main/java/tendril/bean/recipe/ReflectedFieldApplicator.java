package tendril.bean.recipe;

import tendril.bean.Inject;

/**
 * {@link Applicator} for the purpose of injecting field in a bean annotated with @{@link Inject} via reflection. This is only intended to be used
 * in situations where direct access to the field is not possible.
 * 
 * @param <CONSUMER> representing the class into which the bean is to be injected/applied
 * @param <BEAN> representing the class of the bean which is to be injected/applied
 */
public class ReflectedFieldApplicator<CONSUMER, BEAN> extends ReflectedFieldInjectionHandler<CONSUMER> implements Applicator<CONSUMER, BEAN> {
	
	/**
	 * CTOR - to support injecting beans annotated with @{@link Inject}
	 * This CTOR is to be used when the {@link Applicator} capability is required
	 * 
	 * @param fieldPath {@link String} the full path to the field (for error reporting purposes)
	 * @param fieldName {@link String} the name of the field/variable that is to be injected
	 */
	public ReflectedFieldApplicator(String fieldPath, String fieldName) {
		super(fieldPath, fieldName, Inject.class);
	}

	/**
	 * @see tendril.bean.recipe.Applicator#apply(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void apply(CONSUMER consumer, BEAN bean) {
		injectBean(consumer, bean);
	}
}
