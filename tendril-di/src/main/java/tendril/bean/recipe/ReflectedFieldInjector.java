package tendril.bean.recipe;

import tendril.bean.InjectAll;
import tendril.bean.qualifier.Descriptor;
import tendril.context.Engine;

/**
 * {@link Injector} for the purpose of injecting field in a bean annotated with @{@link InjectAll} via reflection. This is only intended to be used
 * in situations where direct access to the field is not possible.
 * 
 * @param <CONSUMER> representing the class into which the bean is to be injected/applied
 * @param <BEAN> representing the class of the bean which is to be injected/applied
 */
public class ReflectedFieldInjector<CONSUMER, BEAN> extends ReflectedFieldInjectionHandler<CONSUMER> implements Injector<CONSUMER> {
	
	/** Descriptor of the beans to be injected, intended to be used when performing an @InjectAll */
	private final Descriptor<BEAN> descriptor;
	
	/**
	 * CTOR - to support injecting beans annotated with @{@link InjectAll}
	 * This CTOR is to be used when the {@link Injector} capability is required
	 * 
	 * @param fieldPath {@link String} the full path to the field (for error reporting purposes)
	 * @param fieldName {@link String} the name of the field/variable that is to be injected
	 * @param descriptor {@link Descriptor} describing all of the beans that are to be injected
	 */
	public ReflectedFieldInjector(String fieldPath, String fieldName, Descriptor<BEAN> descriptor) {
		super(fieldPath, fieldName, InjectAll.class);
		this.descriptor = descriptor;
	}

	/**
	 * @see tendril.bean.recipe.Injector#inject(java.lang.Object, tendril.context.Engine)
	 */
	@Override
	public void inject(CONSUMER consumer, Engine engine) {
		injectBean(consumer, engine.getAllBeans(descriptor));
	}
}
