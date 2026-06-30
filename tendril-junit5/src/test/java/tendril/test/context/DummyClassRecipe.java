package tendril.test.context;

import tendril.bean.qualifier.Descriptor;
import tendril.bean.recipe.SingletonRecipe;
import tendril.bean.requirement.Requirement;
import tendril.context.Engine;

public class DummyClassRecipe extends SingletonRecipe<DummyClass> {

	public DummyClassRecipe(Engine engine) {
		super(engine, DummyClass.class, false, false);
	}

	@Override
	protected void setupDescriptor(Descriptor<DummyClass> descriptor) {
	}

	@Override
	protected void setupEnvironmentRequirement(Requirement requirement) {
	}

	@Override
	protected void setupPropertyRequirement(Requirement requirement) {
	}

	@Override
	protected DummyClass createInstance(Engine engine) throws Throwable {
		return new DummyClass();
	}

}
