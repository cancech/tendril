package tendril.test.recipe;

import tendril.bean.qualifier.Descriptor;
import tendril.bean.recipe.AbstractRecipe;
import tendril.bean.requirement.Requirement;
import tendril.context.Engine;

public class PrimaryStringRecipe1 extends AbstractRecipe<String> {

    /** The value that the recipe produces */
    public static final String VALUE = "PrimaryString1";
    
    /**
     * CTOR
     * 
     * @param engine {@link Engine} in which the recipe is to be registered
     */
    public PrimaryStringRecipe1(Engine engine) {
        super(engine, String.class, true, false);
    }

    /**
     * @see tendril.bean.recipe.AbstractRecipe#setupDescriptor(tendril.bean.qualifier.Descriptor)
     */
    @Override
    protected void setupDescriptor(Descriptor<String> descriptor) {
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
