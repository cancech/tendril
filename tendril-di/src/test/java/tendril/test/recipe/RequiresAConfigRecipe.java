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

import java.util.Map;

import tendril.bean.recipe.AbstractRecipe;
import tendril.bean.recipe.ConfigurationRecipe;
import tendril.bean.requirement.Requirement;
import tendril.context.Engine;

/**
 * Configuration recipe to use for testing
 */
public class RequiresAConfigRecipe extends ConfigurationRecipe<TestConfig>{

    /**
     * CTOR
     * 
     * @param engine {@link Engine} for the context
     */
    public RequiresAConfigRecipe(Engine engine) {
        super(engine, TestConfig.class);
    }

    /**
     * @see tendril.bean.recipe.ConfigurationRecipe#getNestedRecipes()
     */
    @Override
    public Map<String, AbstractRecipe<?>> getNestedRecipes() {
        return Map.of("int1", new IntTestRecipe(engine),
                RequiresBNestedRecipe.NAME, new RequiresBNestedRecipe(engine));
    }

    /**
     * @see tendril.bean.recipe.AbstractRecipe#createInstance(tendril.context.Engine)
     */
    @Override
    protected TestConfig createInstance(Engine engine) {
        return new TestConfig();
    }

    /**
     * @see tendril.bean.recipe.AbstractRecipe#setupRequirement(tendril.bean.requirement.Requirement)
     */
    @Override
    protected void setupRequirement(Requirement requirement) {
        requirement.addRequiredEnvironment("A");
    }

}
