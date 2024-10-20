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

import tendril.bean.recipe.Recipe;
import tendril.processor.registration.RegistryFile;

/**
 * 
 */
public class Engine {

    private final List<Recipe<?>> recipes = new ArrayList<>();

    public void init() {
        try {
            for (String recipe: RegistryFile.read()) {
                try {
                    System.out.println("Loading: " + recipe);
                    recipes.add((Recipe<?>) Class.forName(recipe).getDeclaredConstructor().newInstance());
                } catch (ClassCastException e) {
                    System.err.println(recipe + " is not a proper recipe (does not extend " + Recipe.class.getName() + ")");
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
}
