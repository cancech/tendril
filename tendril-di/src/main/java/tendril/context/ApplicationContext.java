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

import tendril.bean.recipe.AbstractRecipe;
import tendril.context.launch.TendrilRunner;
import tendril.processor.registration.RunnerFile;

/**
 * The context and scope in which the dependency injection will take place, and within which the application logic will execute. There is the expectation of a single
 * context being present for a single application and it will encompass the whole execution environment of said application. In many respects the application context is
 * ultimately just a wrapper for the various subprocesses which are taking place within. Namely:
 * 
 * <ul>
 *      <li> {@link AbstractRecipe} - dictates what beans are present and how they are to be assembled (i.e.: what they require)</li>
 *      <li> {@link Engine} - drives the passing of beans and is largely responsible for the dependency injection to take place</li>
 *      <li> {@link TendrilRunner} - triggers the execution of the necessary behavior(s) and logic(s) of the application</li>
 * </ul>
 */
public class ApplicationContext {
    
    /** The {@link Engine} which drives the bean passing */
    private final Engine engine;
    
    /**
     * CTOR
     */
    public ApplicationContext() {
        this(new Engine()); 
    }
    
    /**
     * CTOR - convenience constructor primarily for the purpose of testing
     * 
     * @param engine {@link Engine} to be used within the context
     */
    ApplicationContext(Engine engine) {
        this.engine = engine;
    }

    /**
     * Start the context and trigger execution via the defined {@link TendrilRunner}
     */
    public void start() {
        engine.init();

        try {
            String runnerClass = RunnerFile.read();
            TendrilRunner runner = (TendrilRunner) ((AbstractRecipe<?>)Class.forName(runnerClass).getDeclaredConstructor(Engine.class).newInstance(engine)).get();
            runner.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
