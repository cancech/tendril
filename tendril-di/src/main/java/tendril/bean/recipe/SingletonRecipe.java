/*
 * Copyright 2024 Jaroslav Bosak
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
 * Abstract recipe for creating singleton beans. This takes on the responsibility for creating and managing the singleton instance of the bean. The class of the bean does not
 * need to be singleton, rather the recipe ensures that the specific instance of an concrete bean is only created once and simply returns the created instance for every
 * subsequent access to the bean.
 * 
 * @param <BEAN_TYPE> the type of bean the recipe creates
 */
public abstract class SingletonRecipe<BEAN_TYPE> extends AbstractRecipe<BEAN_TYPE> {

    /** The singleton instance of the bean */
    private BEAN_TYPE bean = null;
    
    /**
     * CTOR
     * 
     * @param engine {@link Engine} powering the {@link ApplicationContext} in which the bean lives
     * @param beanClass {@link Class} of the bean instance
     * @param isPrimary true if the bean is a Primary bean
     * @param isFallback true if the bean is a fallback bean
     */
    protected SingletonRecipe(Engine engine, Class<BEAN_TYPE> beanClass, boolean isPrimary, boolean isFallback) {
        super(engine, beanClass, isPrimary, isFallback);
    }
    
    /**
     * The bean instance is treated as a singleton, created on the first access and the existing instance returned for each subsequent one.
     * 
     * @see tendril.bean.recipe.AbstractRecipe#get()
     */
    @Override
    public BEAN_TYPE get() {
        if (bean == null)
            bean = buildBean();
        
        return bean;
    }
}
