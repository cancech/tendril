/*
 * Copyright 2025 Jaroslav Bosak
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://opensource.org/license/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tendril.test.recipe;

import tendril.bean.qualifier.Descriptor;
import tendril.bean.recipe.AbstractRecipe;
import tendril.bean.requirement.Requirement;
import tendril.context.Engine;

/**
 * 
 */
public class RequiresEnvBNestedRecipe extends AbstractRecipe<Double> {

    public static final String NAME = "EnvNestedB";
    /** The value that the recipe produces */
    public static final Double VALUE = 0.987;
    
    /**
     * @param engine
     */
    public RequiresEnvBNestedRecipe(Engine engine) {
        super(engine, Double.class, false, false);
    }

    /**
     * @see tendril.bean.recipe.AbstractRecipe#setupDescriptor(tendril.bean.qualifier.Descriptor)
     */
    @Override
    protected void setupDescriptor(Descriptor<Double> descriptor) {
        descriptor.setName(NAME);
    }

    /**
     * @see tendril.bean.recipe.AbstractRecipe#setupEnvironmentRequirement(tendril.bean.requirement.Requirement)
     */
    @Override
    protected void setupEnvironmentRequirement(Requirement requirement) {
        requirement.addRequired("B");
    }

	@Override
	protected void setupPropertyRequirement(Requirement requirement) {
	}

    /**
     * @see tendril.bean.recipe.AbstractRecipe#get()
     */
    @Override
    public Double get() {
        return VALUE;
    }

    /**
     * @see tendril.bean.recipe.AbstractRecipe#createInstance(tendril.context.Engine)
     */
    @Override
    protected Double createInstance(Engine engine) {
        return VALUE;
    }

}