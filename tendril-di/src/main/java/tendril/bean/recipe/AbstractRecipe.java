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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import tendril.BeanCreationException;
import tendril.bean.Fallback;
import tendril.bean.Inject;
import tendril.bean.PostConstruct;
import tendril.bean.Primary;
import tendril.bean.qualifier.Descriptor;
import tendril.bean.requirement.Requirement;
import tendril.context.Engine;

/**
 * The base abstract Recipe, which provides the mechanisms for how to create a bean and process it dependencies, but leaving it up to the concrete recipe for how the created bean is to be handled.
 * 
 * @param <BEAN_TYPE> indicating the type of bean that the recipe is "announcing" as creating
 * @param <INSTANCE_TYPE> the actual type of the object that is created for the bean. This must extend {@code BEAN_TYPE}
 */
public abstract class AbstractRecipe<BEAN_TYPE, INSTANCE_TYPE extends BEAN_TYPE> {

	/** The {@link Engine} which drives the overall dependency injection */
	protected final Engine engine;
	/** The description of this bean */
	private final Descriptor<BEAN_TYPE> descriptor;
	/** The environment requirements of this bean */
	private final Requirement envRequirement = new Requirement();
	/** The property requirements of this bean */
	private final Requirement propRequirement = new Requirement();
	/** Flag indicating that the bean is a primary bean */
	private boolean isPrimary;
	/** Flag indicating that the bean is a fallback bean */
	private boolean isFallback;

	/** List of the dependencies that the bean must receive */
	private final List<Injector<BEAN_TYPE>> consumers = new ArrayList<>();
	/** Flag to mark whether the bean is being constructed - used to detect dependency cycles */
	private boolean isUnderConstruction = false;

	/**
	 * CTOR
	 * 
	 * @param engine     {@link Engine} powering the dependency injection and bean passing
	 * @param beanClass  {@link Class} of the bean the recipe is to build
	 * @param isPrimary  boolean flag for whether the bean is marked as {@link Primary}
	 * @param isFallback boolean flag for whether the bean is marked as {@link Fallback}
	 */
	protected AbstractRecipe(Engine engine, Class<BEAN_TYPE> beanClass, boolean isPrimary, boolean isFallback) {
		this.engine = engine;
		this.descriptor = new Descriptor<>(beanClass);
		this.isPrimary = isPrimary;
		this.isFallback = isFallback;
		init();
	}

	/**
	 * CTOR
	 * 
	 * @param engine     {@link Engine} powering the dependency injection and bean passing
	 * @param beanClass  {@link Class} of the bean the recipe is to build
	 * @param descriptor {@link Descriptor} to apply to the bean/recipe
	 */
	protected AbstractRecipe(Engine engine, Class<BEAN_TYPE> beanClass, Descriptor<BEAN_TYPE> descriptor) {
		this.engine = engine;
		this.descriptor = descriptor;
		this.isPrimary = false;
		this.isFallback = false;
		init();
	}
	
	/**
	 * Perform the necessary initialization of the recipe
	 */
	private void init() {
		setupDescriptor(descriptor);
		setupEnvironmentRequirement(envRequirement);
		setupPropertyRequirement(propRequirement);
	}

	/**
	 * Updates the priorities of this recipe with those of another
	 * 
	 * @param other {@link AbstractRecipe} from which to update priorities
	 */
	public void updatePriorities(AbstractRecipe<?, ?> other) {
		this.isPrimary = other.isPrimary();
		this.isFallback = other.isFallback();
	}

	/**
	 * Check if the bean is a {@link Primary} bean
	 * 
	 * @return true if it is
	 */
	public boolean isPrimary() {
		return isPrimary;
	}

	/**
	 * Check if the bean is a {@link Fallback} bean
	 * 
	 * @return true if it is
	 */
	public boolean isFallback() {
		return isFallback;
	}

	/**
	 * To be overloaded by the concrete recipe to provide the appropriate description of the bean that the recipe is to create.
	 * 
	 * @param descriptor {@link Descriptor} where the description is to be provided
	 */
	protected abstract void setupDescriptor(Descriptor<BEAN_TYPE> descriptor);

	/**
	 * To be overloaded by the concrete recipe to provide the environment requirements the context needs to meet for this recipe to be created.
	 * 
	 * @param requirement {@link Requirement} where the bean requirements are to be outlined
	 */
	protected abstract void setupEnvironmentRequirement(Requirement requirement);

	/**
	 * To be overloaded by the concrete recipe to provide the property requirements the context needs to meet for this recipe to be created.
	 * 
	 * @param requirement {@link Requirement} where the bean requirements are to be outlined
	 */
	protected abstract void setupPropertyRequirement(Requirement requirement);

	/**
	 * Get the description of the bean the recipe is to create.
	 * 
	 * @return {@link Descriptor} for the bean
	 */
	public Descriptor<BEAN_TYPE> getDescription() {
		return descriptor;
	}

	/**
	 * Get the environment requirement for the recipe. This will under what circumstances the recipe can be accepted and the bean within created.
	 * 
	 * @return {@link Requirement} for the bean/recipe environment
	 */
	public Requirement getEnvironmentRequirement() {
		return envRequirement;
	}

	/**
	 * Get the property requirement for the recipe. This will under what circumstances the recipe can be accepted and the bean within created.
	 * 
	 * @return {@link Requirement} for the bean/recipe (system) properties
	 */
	public Requirement getPropertyRequirement() {
		return propRequirement;
	}

	/**
	 * Register a dependency that the bean requires in order to be created. The dependency has two parts:
	 * 
	 * <ul>
	 * <li>{@link Descriptor} which describes what the dependency is</li>
	 * <li>{@link Applicator} which provides the means of actually applying the dependency to the bean</li>
	 * </ul>
	 * 
	 * These dependencies must be mutually independent (i.e.: they can be fulfilled in any order) and will only be applied after the instance has been actually created. As such, first the instance
	 * will be created and only after will the dependencies be applied.
	 * 
	 * @param <DEPENDENCY_TYPE> the type of class that is to be applied as the dependency
	 * @param desc              {@link Descriptor} providing a description of which exact bean the dependency is
	 * @param appl              {@link Applicator} providing the appropriate mechanism for applying the dependency to the bean under construction
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
	 * Get the instance of the bean that has been created. This is expected to be called by the {@link Engine} in response to another bean (recipe) requiring the one created and defined by the current
	 * recipe.
	 * 
	 * @return The instance of the bean that the recipe has created
	 */
	public abstract BEAN_TYPE get();

	/**
	 * Performs the steps necessary for creating an instance of the bean per the recipe. The expectation is that this will be called by the get() method, allowing the concrete recipe to focus on the
	 * mechanism of managing the bean instance life cycle, with the abstract recipe bean construction.
	 * 
	 * @return The (an) instance of the bean that the recipe is to create
	 * @throws BeanCreationException if there is an issue creating the bean
	 */
	protected INSTANCE_TYPE buildBean() {
		if (isUnderConstruction)
			throw new BeanCreationException(descriptor, "Cycle detected");

		// Create the instance
		try {
			// Create the instance
			isUnderConstruction = true;
			INSTANCE_TYPE bean = createInstance(engine);
			// Apply dependencies
			consumers.forEach(c -> c.inject(bean, engine));
			isUnderConstruction = false;
			// Trigger post construct
			postConstruct(bean);
			return bean;
		} catch (Throwable e) {
			isUnderConstruction = false;
			throw new BeanCreationException(descriptor, e);
		}
	}

	/**
	 * Create the bean. Must be implemented by the concrete recipe to ensure that the bean object is properly created.
	 * 
	 * @param engine {@link Engine} from which dependencies for the constructor are to be pulled
	 * @throws Throwable accounting for the possibility that the nested bean creation could throw an exception
	 * @return BEAN_TYPE
	 */
	protected abstract INSTANCE_TYPE createInstance(Engine engine) throws Throwable;

	/**
	 * Called after the bean has been initialized, to allow all {@link PostConstruct} annotated methods to be called
	 * 
	 * @param bean INSTANCE_TYPE that the recipe is building
	 */
	protected void postConstruct(INSTANCE_TYPE bean) {
		// Intentionally left blank, concrete recipe to trigger the appropriate @PostConstruct called
	}
	
	/**
	 * Helper method to find the the method to inject via reflection. This is intended to be used at runtime if the injected method is not
	 * directly accessible from the recipe at runtime.
	 * 
	 * @param klass {@link Class} in which to find the method to inject
	 * @param name {@link String} the name of the method
	 * @param params {@link Class}... listing all of the parameters that the method should contain
	 * @return {@link Method} reference which can be invoked via reflection
	 * @throws NoSuchMethodException if no valid injectable method is found
	 * @throws SecurityException if the method is not accessible
	 */
	protected static Method findReflectedMethod(Class<?> klass, String name, Class<?>...params) throws NoSuchMethodException, SecurityException {
		try {
			Method rm = klass.getDeclaredMethod(name, params);
			if (rm.getAnnotation(Inject.class) != null)
				return rm;
		} catch (NoSuchMethodException e) {
			// Ignore, means that need to check the parent class
		}
		
		Class<?> parent = klass.getSuperclass();
		if (parent == null)
			throw new NoSuchMethodException("No injectable method " + name + " in the class hierarchy");
		
		return findReflectedMethod(parent, name, params);
	}
}
