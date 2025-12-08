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
package tendril.bean.recipe;

import java.util.Map;

import tendril.bean.qualifier.Descriptor;
import tendril.context.Engine;

/**
 * Recipe that is to be used for configuration files. This is by and large an {@link AbstractRecipe} by every right, with the distinction being, that the "bean" (i.e.: configuration) that it produces
 * is not the final end result, but rather a stepping stone to the beans that are defined within (via its methods). This recipe is to be passed on to the generated recipes for the nested method beans,
 * which then retrieve the configuration and call the appropriate method from it.
 * 
 * @param <CONFIGURATION_TYPE> indicating the class of the configuration the recipe is creating
 */
public abstract class ConfigurationRecipe<CONFIGURATION_TYPE> extends SingletonRecipe<CONFIGURATION_TYPE> {

    /**
     * CTOR
     * 
     * @param engine      {@link Engine} powering the dependency injection and bean passing
     * @param configClass {@link Class} of the configuration
     * @param isPrimary true if the bean is a Primary bean
     * @param isFallback true if the bean is a fallback bean
     */
    protected ConfigurationRecipe(Engine engine, Class<CONFIGURATION_TYPE> configClass, boolean isPrimary, boolean isFallback) {
        super(engine, configClass, isPrimary, isFallback);
    }

    /**
     * @see tendril.bean.recipe.AbstractRecipe#setupDescriptor(tendril.bean.qualifier.Descriptor)
     */
    @Override
    protected void setupDescriptor(Descriptor<CONFIGURATION_TYPE> descriptor) {
        // No descriptor required for configurations
    }

    /**
     * Get the recipes for the nested method beans
     * 
     * @return {@link Map} of {@link String} (method name) to the {@link AbstractRecipe} which creates the nested bean
     */
    public abstract Map<String, AbstractRecipe<?>> getNestedRecipes();
}
