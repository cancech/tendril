package tendril.test.recipe;

import tendril.bean.qualifier.Descriptor;
import tendril.bean.recipe.AbstractRecipe;
import tendril.bean.requirement.Requirement;
import tendril.context.Engine;

/**
 * Recipe to replace the {@link IntTestRecipe} in testing
 */
public class ReplaceIntRecipe extends AbstractRecipe<Integer> {
	
    /** The value that the replacement recipe produces */
    public static final int VALUE = 321;

    /**
     * CTOR
     * 
     * @param engine {@link Engine} in which the replacement recipe is to be registered
     */
	public ReplaceIntRecipe(Engine engine) {
		super(engine, Integer.class, false, false);
	}

	/**
	 * @see tendril.bean.recipe.AbstractRecipe#setupDescriptor(tendril.bean.qualifier.Descriptor)
	 */
	@Override
	protected void setupDescriptor(Descriptor<Integer> descriptor) {
		descriptor.setName(IntTestRecipe.NAME);
	}

	/**
	 * @see tendril.bean.recipe.AbstractRecipe#setupRequirement(tendril.bean.requirement.Requirement)
	 */
	@Override
	protected void setupRequirement(Requirement requirement) {
	}

	/**
	 * @see tendril.bean.recipe.AbstractRecipe#get()
	 */
	@Override
	public Integer get() {
		return VALUE;
	}

	/**
	 * @see tendril.bean.recipe.AbstractRecipe#createInstance(tendril.context.Engine)
	 */
	@Override
	protected Integer createInstance(Engine engine) {
		return VALUE;
	}

}
