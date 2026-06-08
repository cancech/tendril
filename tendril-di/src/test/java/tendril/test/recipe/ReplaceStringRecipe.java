package tendril.test.recipe;

import tendril.bean.qualifier.Descriptor;
import tendril.bean.recipe.AbstractRecipe;
import tendril.bean.requirement.Requirement;
import tendril.context.Engine;

/**
 * Recipe to replace the {@link IntTestRecipe} in testing
 */
public class ReplaceStringRecipe extends AbstractRecipe<String> {
	
    /** The value that the replacement recipe produces */
    public static final String VALUE = "replacement";

    /**
     * CTOR
     * 
     * @param engine {@link Engine} in which the replacement recipe is to be registered
     */
	public ReplaceStringRecipe(Engine engine) {
		super(engine, String.class, false, false);
	}

	/**
	 * @see tendril.bean.recipe.AbstractRecipe#setupDescriptor(tendril.bean.qualifier.Descriptor)
	 */
	@Override
	protected void setupDescriptor(Descriptor<String> descriptor) {
	}

	/**
	 * @see tendril.bean.recipe.AbstractRecipe#setupEnvironmentRequirement(tendril.bean.requirement.Requirement)
	 */
	@Override
	protected void setupEnvironmentRequirement(Requirement requirement) {
	}

	@Override
	protected void setupPropertyRequirement(Requirement requirement) {
	}

	/**
	 * @see tendril.bean.recipe.AbstractRecipe#get()
	 */
	@Override
	public String get() {
		return VALUE;
	}

	/**
	 * @see tendril.bean.recipe.AbstractRecipe#createInstance(tendril.context.Engine)
	 */
	@Override
	protected String createInstance(Engine engine) {
		return VALUE;
	}

}
