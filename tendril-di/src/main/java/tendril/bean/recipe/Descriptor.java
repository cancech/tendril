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
 * Description of a bean that is (expected to be) available within the {@link ApplicationContext} and accessible via its {@link Engine}.
 * 
 * @param <BEAN_TYPE> the type of bean that the {@link Descriptor} describes
 */
public class Descriptor<BEAN_TYPE> {
    
    /** The {@link Class} of the bean */
    private final Class<BEAN_TYPE> beanClass;
    /** The name of the bean */
    private String name = "";
    
    /**
     * CTOR
     * 
     * @param beanClass {@link Class} of the bean that is described
     */
    public Descriptor(Class<BEAN_TYPE> beanClass) {
        this.beanClass = beanClass;
    }
    
    /**
     * Get the {@link Class} of the described bean
     * 
     * @return {@link Class}
     */
    public Class<BEAN_TYPE> getBeanClass() {
        return beanClass;
    }
    
    /**
     * Set the name of the described bean
     * 
     * @param name {@link String} of the bean
     * 
     * @return {@link Descriptor} describing the bean
     */
    public Descriptor<BEAN_TYPE> setName(String name) {
        this.name = name;
        return this;
    }
    
    /**
     * Get the name of the bean.
     * 
     * @return {@link String} name of the bean
     */
    public String getName() {
        return name;
    }

}
