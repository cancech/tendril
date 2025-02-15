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

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import tendril.BeanCreationException;
import tendril.context.Engine;

/**
 * The base abstract Recipe, which provides the mechanisms for how to create a bean and process it dependencies, but leaving it up to the concrete recipe for how the
 * created bean is to be handled.
 * 
 * @param <BEAN_TYPE> indicating the type of bean that the recipe is to build
 */
public abstract class AbstractRecipe<BEAN_TYPE> {

    /** The {@link Engine} which drives the overall dependency injection */
    private final Engine engine;
    /** The {@link Class} indicating the type of bean the recipe creates */
    private final Class<BEAN_TYPE> beanClass;
    /** The description of this bean */
    private final Descriptor<BEAN_TYPE> descriptor;
    
    /** List of the dependencies that the bean must receive */
    private final List<InjectDependency<BEAN_TYPE, ?>> consumers = new ArrayList<>();
    
    /**
     * CTOR
     * 
     * @param engine {@link Engine} powering the dependency injection and bean passing
     * @param beanClass {@link Class} of the bean the recipe is to build
     */
    protected AbstractRecipe(Engine engine, Class<BEAN_TYPE> beanClass) {
        this.engine = engine;
        this.beanClass = beanClass;
        this.descriptor = new Descriptor<>(beanClass);
        
        setupDescriptor(descriptor);
    }
    
    /**
     * To be overloaded by the concrete recipe to provide the appropriate description of the bean that the recipe is to create.
     * 
     * @param descriptor {@link Descriptor} where the description is to be provided
     */
    protected abstract void setupDescriptor(Descriptor<BEAN_TYPE> descriptor);
    
    /**
     * Get the description of the bean the recipe is to create.
     * 
     * @return {@link Descriptor} for the bean
     */
    public Descriptor<BEAN_TYPE> getDescription() {
        return descriptor;
    }
    
    /**
     * Register a dependency that the bean requires in order to be created. The dependency has two parts:
     * 
     * <ul>
     *      <li>{@link Descriptor} which describes what the dependency is</li>
     *      <li>{@link Applicator} which provides the means of actually applying the dependency to the bean</li>
     * </ul>
     * 
     * These dependencies must be mutually independent (i.e.: they can be fulfilled in any order) and will only be applied after the instance has been actually created.
     * As such, first the instance will be created and only after will the dependencies be applied.
     * 
     * @param <DEPENDENCY_TYPE> the type of class that is to be applied as the dependency
     * @param desc {@link Descriptor} providing a description of which exact bean the dependency is
     * @param appl {@link Applicator} providing the appropriate mechanism for applying the dependency to the bean under construction
     */
    protected <DEPENDENCY_TYPE> void registerDependency(Descriptor<DEPENDENCY_TYPE> desc, Applicator<BEAN_TYPE, DEPENDENCY_TYPE> appl) {
        consumers.add(new InjectDependency<>(desc, appl));
    }
    
    /**
     * Get the instance of the bean that has been created. This is expected to be called by the {@link Engine} in response to another bean (recipe) requiring the one created
     * and defined by the current recipe.
     * 
     * @return The instance of the bean that the recipe has created 
     */
    public abstract BEAN_TYPE get();
    
    /**
     * Performs the steps necessary for creating an instance of the bean per the recipe. The expectation is that this will be called by the get() method, allowing the concrete
     * recipe to focus on the mechanism of managing the bean instance life cycle, with the abstract recipe bean construction.
     * 
     * @return The (an) instance of the bean that the recipe is to create
     * @throws BeanCreationException if there is an issue creating the bean
     */
    @SuppressWarnings("unchecked")
    protected BEAN_TYPE buildBean() {
        Constructor<?>[] ctors = beanClass.getDeclaredConstructors();

        // Create the instance
        if (ctors.length == 0)
            throw new BeanCreationException(descriptor, "Bean has no constructors defined (is this an interface?)");
        
        // TODO allow @inject to identify which exact constructor to use
        if (ctors.length > 1)
            throw new BeanCreationException(descriptor, "Multiple constructors are available, ambiguous as to which to call");
        
        // Apply all dependencies
        try {
            BEAN_TYPE bean = (BEAN_TYPE) ctors[0].newInstance();
            consumers.forEach(c -> c.inject(bean, engine));
            return bean;
        } catch (Exception e) {
            throw new BeanCreationException(descriptor, e);
        }
    }
}
