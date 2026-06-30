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
package tendril.context;

import tendril.TendrilStartupException;
import tendril.bean.duplicate.BlueprintDriver;
import tendril.bean.recipe.AbstractRecipe;
import tendril.context.launch.TendrilRunner;

/**
 * Performs the creation and initialization of the {@link ApplicationContext} in which the dependency injection will take place, and within which the application logic 
 * will execute. There is the expectation of a single context being present for a single application and it will encompass the whole execution environment of said 
 * application. In many respects the application context is ultimately just a wrapper for the various subprocesses which are taking place within. Namely:
 * 
 * <ul>
 *      <li> {@link AbstractRecipe} - dictates what beans are present and how they are to be assembled (i.e.: what they require)</li>
 *      <li> {@link Engine} - drives the passing of beans and is largely responsible for the dependency injection to take place</li>
 *      <li> {@link TendrilRunner} - triggers the execution of the necessary behavior(s) and logic(s) of the application</li>
 * </ul>
 */
public class ApplicationContextBuilder {
    
    /** The {@link Engine} which drives the bean passing */
    private final Engine engine;
    
    /**
     * CTOR
     */
    public ApplicationContextBuilder() {
        this(new Engine()); 
    }
    
    /**
     * CTOR - convenience constructor primarily for the purpose of testing
     * 
     * @param engine {@link Engine} to be used within the context
     */
    public ApplicationContextBuilder(Engine engine) {
        this.engine = engine;
    }
    
    /**
     * Set the environments in which the application is to be executed
     * 
     * @param envs {@link String}... indicating what all environments are to be used
     */
    public void setEnvironments(String...envs) {
        engine.addEnvironments(envs);
    }
    
    /**
     * Add a class based dynamic blueprint
     * 
     * @param driver {@link BlueprintDriver} the dynamic blueprint to add
     */
    public void addDynamicBlueprint(BlueprintDriver driver) {
    	Class<?> blueprintClass = driver.getClass();
    	if (blueprintClass.isEnum())
    		throw new TendrilStartupException(blueprintClass.getName() + " is an enum, only regular classes can be used as dynamic blueprints.");
    	
    	engine.addDynamicBlueprint(driver);
    }
    
    /**
     * Trigger the creation and initialization of the application context, but does not start it. Start must be manually performed on the application
     * context itself.
     * 
     * @return {@link ApplicationContext}
     */
    public ApplicationContext build() {
        engine.init();
    	return engine;
    }
}
