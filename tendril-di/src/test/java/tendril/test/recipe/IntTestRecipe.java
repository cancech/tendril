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
import tendril.bean.recipe.Descriptor;
import tendril.context.Engine;

/**
 * Recipe to use for testing where a {@link Integer} 123 is produced.
 */
public class IntTestRecipe extends AbstractRecipe<Integer> {

    /** The value that the recipe produces */
    public static final int VALUE = 123;
    
    /**
     * CTOR
     * 
     * @param engine {@link Engine} in which the recipe is to be registered
     */
    public IntTestRecipe(Engine engine) {
        super(engine, Integer.class);
    }

    /**
     * @see tendril.bean.recipe.AbstractRecipe#setupDescriptor(tendril.bean.recipe.Descriptor)
     */
    @Override
    protected void setupDescriptor(Descriptor<Integer> descriptor) {
        
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
