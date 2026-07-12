package tendril.bean.recipe;

import tendril.bean.qualifier.Descriptor;
import tendril.bean.requirement.Requirement;
import tendril.context.ApplicationContext;
import tendril.context.Engine;

/**
 * Recipe that acts purely as a wrapper for the object specified. It makes not attempt to even try and initialize an instance, merely returning the bean that was passed into it. In this sense it is a
 * singleton wrapper for the bean that was manually created outside of the {@link ApplicationContext}.
 * 
 * @param <BEAN_TYPE> the type of bean the recipe contains
 */
public class WrapperRecipe<BEAN_TYPE> extends SingletonRecipe<BEAN_TYPE, BEAN_TYPE> {
	/** The manually created bean instance */
	private final BEAN_TYPE bean;

	/**
	 * CTOR
	 * 
	 * @param engine      {@link Engine} powering the {@link ApplicationContext} in which the bean lives
	 * @param bean        {@code BEAN_TYPE} instance to manually inject into the {@link ApplicationContext}
	 * @param description {@link Descriptor} describing the bean
	 */
	public WrapperRecipe(Engine engine, BEAN_TYPE bean, Descriptor<BEAN_TYPE> description) {
		super(engine, description);
		this.bean = bean;
	}

	/**
	 * Does nothing, not applicable.
	 * 
	 * @see tendril.bean.recipe.AbstractRecipe#setupDescriptor(tendril.bean.qualifier.Descriptor)
	 */
	@Override
	protected void setupDescriptor(Descriptor<BEAN_TYPE> descriptor) {
		// Not required
	}

	/**
	 * Does nothing, not applicable.
	 * 
	 * @see tendril.bean.recipe.AbstractRecipe#setupEnvironmentRequirement(tendril.bean.requirement.Requirement)
	 */
	@Override
	protected void setupEnvironmentRequirement(Requirement requirement) {
		// Not required
	}

	/**
	 * Does nothing, not applicable.
	 * 
	 * @see tendril.bean.recipe.AbstractRecipe#setupPropertyRequirement(tendril.bean.requirement.Requirement)
	 */
	@Override
	protected void setupPropertyRequirement(Requirement requirement) {
		// Not required
	}

	/**
	 * Merely returns the bean instance that was originally passed into the recipe.
	 * 
	 * @see tendril.bean.recipe.AbstractRecipe#createInstance(tendril.context.Engine)
	 */
	@Override
	protected BEAN_TYPE createInstance(Engine engine) {
		return bean;
	}

}
