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

import tendril.context.ApplicationContext;
import tendril.context.Engine;

/**
 * Abstract recipe for creating factory beans, where each retrieved bean is a separate and unique instance. Thus as many beans are retrieved, that many copies of the bean are
 * created and provided
 * 
 * @param <BEAN_TYPE> the type of bean the recipe creates
 */
public abstract class FactoryRecipe<BEAN_TYPE> extends AbstractRecipe<BEAN_TYPE> {

    /**
     * CTOR
     * 
     * @param engine {@link Engine} powering the {@link ApplicationContext} in which the bean lives
     * @param beanClass {@link Class} of the bean instance
     */
    protected FactoryRecipe(Engine engine, Class<BEAN_TYPE> beanClass) {
        super(engine, beanClass);
    }
    
    /**
     * A new instance is created for each retrieval
     * 
     * @see tendril.bean.recipe.AbstractRecipe#get()
     */
    @Override
    public BEAN_TYPE get() {
        return buildBean();
    }
}
