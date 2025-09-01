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
package tendril.test.runner;

import tendril.bean.qualifier.Descriptor;
import tendril.bean.recipe.AbstractRecipe;
import tendril.bean.requirement.Requirement;
import tendril.context.Engine;

/**
 * {@link AbstractRecipe} to use for the purpose of allowing the {@link TestTendrilRunner2} to be "injected" into tests
 */
public class TestTendrilRunnerRecipe2 extends AbstractRecipe<TestTendrilRunner2> {

    /**
     * CTOR
     * 
     * @param engine {@link Engine} that the recipe is to be employed with
     */
    public TestTendrilRunnerRecipe2(Engine engine) {
        super(engine, TestTendrilRunner2.class);
    }

    /**
     * @see tendril.bean.recipe.AbstractRecipe#setupDescriptor(tendril.bean.qualifier.Descriptor)
     */
    @Override
    protected void setupDescriptor(Descriptor<TestTendrilRunner2> descriptor) {
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
    public TestTendrilRunner2 get() {
        return new TestTendrilRunner2();
    }

    /**
     * @see tendril.bean.recipe.AbstractRecipe#createInstance(tendril.context.Engine)
     */
    @Override
    protected TestTendrilRunner2 createInstance(Engine engine) {
        return new TestTendrilRunner2();
    }

}
