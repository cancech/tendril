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

import tendril.bean.recipe.AbstractRecipe;
import tendril.context.Engine;

/**
 * Recipe to use for testing where a {@link Double} 3.21 is produced.
 */
public class DoubleTestRecipe extends AbstractRecipe<Double> {
    
    /** The value that the recipe produces */
    public static final double VALUE = 3.21;

    /**
     * CTOR
     * 
     * @param engine {@link Engine} in which the recipe is to be registered
     */
    public DoubleTestRecipe(Engine engine) {
        super(engine, Double.class);
    }

    /**
     * @see tendril.bean.recipe.AbstractRecipe#get()
     */
    @Override
    public Double get() {
        return VALUE;
    }

}
