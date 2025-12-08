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
public class RequiresBRecipe extends AbstractRecipe<Double> {

    /** The name under which the double 2 bean is provided */
    public static final String NAME = "B";
    /** The value that the recipe produces */
    public static final Double VALUE = 4.56;
    
    /**
     * @param engine
     */
    public RequiresBRecipe(Engine engine) {
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
     * @see tendril.bean.recipe.AbstractRecipe#setupRequirement(tendril.bean.requirement.Requirement)
     */
    @Override
    protected void setupRequirement(Requirement requirement) {
        requirement.addRequiredEnvironment("B");
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