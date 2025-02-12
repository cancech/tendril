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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import tendril.bean.recipe.AbstractRecipe;
import tendril.bean.recipe.Descriptor;
import tendril.processor.registration.RegistryFile;

/**
 * The core element within the {@link ApplicationContext} which is responsible for the bulk of the Dependency Injection capability. This tracks all beans (via their recipes)
 * and allows for their access. It is not expected that client code will ever touch or manipulate the engine directly, with the expected interactions being via recipes
 * and their beans.
 */
public class Engine {

    /** List of all known recipes */
    private final List<AbstractRecipe<?>> recipes = new ArrayList<>();

    /**
     * CTOR
     */
    public Engine() {
    }

    /**
     * Initialize the engine by reading the list of all known recipes and registering them with the engine. Each recipe object is created, though the bean contained within
     * is not created until it becomes necessary to do so (i.e.: accessed by a Consumer).
     */
    void init() {
        try {
            for (String recipe: RegistryFile.read()) {
                try {
                    recipes.add((AbstractRecipe<?>) Class.forName(recipe).getDeclaredConstructor(Engine.class).newInstance(this));
                    System.out.println("Loading: " + recipe);
                } catch (ClassCastException e) {
                    System.err.println(recipe + " is not a proper recipe (does not extend " + AbstractRecipe.class.getName() + ")");
                } catch (ClassNotFoundException e) {
                    System.err.println("Unable to find class " + recipe);
                } catch (NoSuchMethodException | InstantiationException | IllegalArgumentException | InvocationTargetException e) {
                    System.err.println("Unable to create " + recipe);
                } catch (IllegalAccessException | SecurityException e) {
                    System.err.println("Unable to access " + recipe);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Get the number of beans that are registered with the engine
     * 
     * @return int the number of beans
     */
    public int getBeanCount() {
        return recipes.size();
    }
    
    /**
     * Get the bean matching the provided descriptor. The descriptor must resolve to exactly one instance otherwise an exception will be thrown.
     * 
     * @param <BEAN_TYPE> indicating the type of bean that is to be retrieved
     * @param descriptor {@link Descriptor} containing the description of the bean that is to be retrieved
     * 
     * @return The specific bean that is desired
     * @throws IllegalArgumentException if there is an issue retrieving the desired bean
     */
    public <BEAN_TYPE> BEAN_TYPE getBean(Descriptor<BEAN_TYPE> descriptor) {
        List<AbstractRecipe<BEAN_TYPE>> matchingRecipes = getRecipesForClass(descriptor.getBeanClass());
        
        if (matchingRecipes.isEmpty())
            throw new IllegalArgumentException("No matching bean found");
        if (matchingRecipes.size() > 1)
            throw new IllegalArgumentException("Too many matches found");
        
        return matchingRecipes.get(0).get();
    }
    
    /**
     * Get all of the recipes which are available for the desired type. This includes exact matches (i.e.: recipe provides exactly the desired class) as well as classes
     * which can be referenced as the desired type (i.e.: they are higher in the hierarchy of the desired type). 
     * 
     * @param <BEAN_TYPE>
     * @param beanClass
     * @return
     */
    @SuppressWarnings("unchecked")
    private <BEAN_TYPE> List<AbstractRecipe<BEAN_TYPE>> getRecipesForClass(Class<BEAN_TYPE> beanClass) {
        List<AbstractRecipe<BEAN_TYPE>> foundRecipes = new ArrayList<>();
        for (AbstractRecipe<?> r: recipes) {
            if (beanClass.isAssignableFrom(r.getBeanClass()))
                foundRecipes.add((AbstractRecipe<BEAN_TYPE>) r);
        }
        
        return foundRecipes;
    }
}
