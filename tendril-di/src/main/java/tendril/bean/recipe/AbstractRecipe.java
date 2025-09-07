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

import java.util.ArrayList;
import java.util.List;

import tendril.BeanCreationException;
import tendril.bean.PostConstruct;
import tendril.bean.qualifier.Descriptor;
import tendril.bean.requirement.Requirement;
import tendril.context.Engine;

/**
 * The base abstract Recipe, which provides the mechanisms for how to create a bean and process it dependencies, but leaving it up to the concrete recipe for how the
 * created bean is to be handled.
 * 
 * @param <BEAN_TYPE> indicating the type of bean that the recipe is to build
 */
public abstract class AbstractRecipe<BEAN_TYPE> {

    /** The {@link Engine} which drives the overall dependency injection */
    protected final Engine engine;
    /** The description of this bean */
    private final Descriptor<BEAN_TYPE> descriptor;
    /** The requirements of this bean */
    private final Requirement requirement;
    
    /** List of the dependencies that the bean must receive */
    private final List<Injector<BEAN_TYPE>> consumers = new ArrayList<>();
    /** Flag to mark whether the bean is being constructed - used to detect dependency cycles */
    private boolean isUnderConstruction = false;
    
    /**
     * CTOR
     * 
     * @param engine {@link Engine} powering the dependency injection and bean passing
     * @param beanClass {@link Class} of the bean the recipe is to build
     */
    protected AbstractRecipe(Engine engine, Class<BEAN_TYPE> beanClass) {
        this.engine = engine;
        this.descriptor = new Descriptor<>(beanClass);
        this.requirement = new Requirement();
        
        setupDescriptor(descriptor);
        setupRequirement(requirement);
    }
    
    /**
     * To be overloaded by the concrete recipe to provide the appropriate description of the bean that the recipe is to create.
     * 
     * @param descriptor {@link Descriptor} where the description is to be provided
     */
    protected abstract void setupDescriptor(Descriptor<BEAN_TYPE> descriptor);
    
    /**
     * To be overloaded by the concrete recipe to provide the requirements the context needs to meet for this recipe to be created.
     * 
     * @param requirement {@link Requirement} where the bean requirements are to be outlined
     */
    protected abstract void setupRequirement(Requirement requirement);
    
    /**
     * Get the description of the bean the recipe is to create.
     * 
     * @return {@link Descriptor} for the bean
     */
    public Descriptor<BEAN_TYPE> getDescription() {
        return descriptor;
    }
    
    /**
     * Get the requirement for the recipe. This will under what circumstances the recipe can be accepted and the bean within created.
     * 
     * @return {@link Requirement} for the bean/recipe
     */
    public Requirement getRequirement() {
        return requirement;
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
        registerInjector(new InjectDependency<>(desc, appl));
    }
    
    /**
     * Register an injector which is to inject a dependency into the bean created by the recipe
     * 
     * @param injector {@link Injector} for the bean
     */
    protected void registerInjector(Injector<BEAN_TYPE> injector) {
        consumers.add(injector);
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
    protected BEAN_TYPE buildBean() {
        if (isUnderConstruction)
            throw new BeanCreationException(descriptor, "Cycle detected");
        
        // Create the instance
        try {
            // Create the instance
            isUnderConstruction = true;
            BEAN_TYPE bean = createInstance(engine);
            // Apply dependencies
            consumers.forEach(c -> c.inject(bean, engine));
            isUnderConstruction = false;
            // Trigger post construct
            postConstruct(bean);
            return bean;
        } catch (Exception e) {
            isUnderConstruction = false;
            throw new BeanCreationException(descriptor, e);
        }
    }
    
    /**
     * Create the bean. Must be implemented by the concrete recipe to ensure that the bean object is properly created.
     * 
     * @param engine {@link Engine} from which dependencies for the constructor are to be pulled
     * @return BEAN_TYPE
     */
    protected abstract BEAN_TYPE createInstance(Engine engine);
    
    /**
     * Called after the bean has been initialized, to allow all {@link PostConstruct} annotated methods to be called
     * 
     * @param bean BEAN_TYPE that the recipe is building
     */
    protected void postConstruct(BEAN_TYPE bean) {
        // Intentionally left blank, concrete recipe to trigger the appropriate @PostConstruct called
    }
}
