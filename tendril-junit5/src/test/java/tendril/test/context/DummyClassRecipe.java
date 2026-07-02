package tendril.test.context;

import tendril.bean.qualifier.Descriptor;
import tendril.bean.recipe.SingletonRecipe;
import tendril.bean.requirement.Requirement;
import tendril.context.Engine;

/**
 * Recipe through which the {@link DummyClass} is loaded by the {@link TestEngine}. This is done so that it is not part of the dependency injection and full control can be exerted over the creation of
 * the {@link DummyClass}
 */
public class DummyClassRecipe extends SingletonRecipe<DummyClass> {

	/**
	 * CTOR
	 * 
	 * @param engine {@link Engine} in which the recipe is to run
	 */
	public DummyClassRecipe(Engine engine) {
		super(engine, DummyClass.class, false, false);
	}

	/**
	 * @see tendril.bean.recipe.AbstractRecipe#setupDescriptor(tendril.bean.qualifier.Descriptor)
	 */
	@Override
	protected void setupDescriptor(Descriptor<DummyClass> descriptor) {
	}

	/**
	 * @see tendril.bean.recipe.AbstractRecipe#setupEnvironmentRequirement(tendril.bean.requirement.Requirement)
	 */
	@Override
	protected void setupEnvironmentRequirement(Requirement requirement) {
	}

	/**
	 * @see tendril.bean.recipe.AbstractRecipe#setupPropertyRequirement(tendril.bean.requirement.Requirement)
	 */
	@Override
	protected void setupPropertyRequirement(Requirement requirement) {
	}

	/**
	 * @see tendril.bean.recipe.AbstractRecipe#createInstance(tendril.context.Engine)
	 */
	@Override
	protected DummyClass createInstance(Engine engine) throws Throwable {
		return new DummyClass();
	}

}
