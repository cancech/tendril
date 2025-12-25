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
package tendril.processor.recipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.Messager;

import tendril.annotationprocessor.exception.TendrilException;
import tendril.bean.Bean;
import tendril.bean.Configuration;
import tendril.bean.recipe.AbstractRecipe;
import tendril.codegen.VisibilityType;
import tendril.codegen.annotation.JAnnotationFactory;
import tendril.codegen.classes.ClassBuilder;
import tendril.codegen.classes.JClass;
import tendril.codegen.classes.method.JMethod;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.generics.GenericFactory;

/**
 * Generator for {@link Configuration} recipes
 */
class ConfigurationRecipeGenerator extends ClassRecipeGenerator {

    /**
     * CTOR
     * 
     * @param configType {@link ClassType} of the configuration
     * @param config     {@link JClass} of the configuration
     * @param messager {@link Messager} that is used by the annotation processor
     */
    ConfigurationRecipeGenerator(ClassType configType, JClass config, Messager messager) {
        super(configType, config, messager);
    }

    /**
     * @see tendril.processor.recipe.AbstractRecipeGenerator#populateBuilder(tendril.codegen.classes.ClassBuilder)
     */
    @Override
    protected void populateBuilder(ClassBuilder builder) throws TendrilException {
        // Build up the contents of the recipe
        generateConstructor(builder);
        generateCreateInstance(builder);
        generateRecipeRequirements(builder);
        processPostConstruct(builder);
        generateNestedRecipes(builder);
    }

    /**
     * Generate the method which populates the nested method bean creators.
     * 
     * @param builder {@link ClassBuilder} where the recipe is being defined
     */
    private void generateNestedRecipes(ClassBuilder builder) {
        ClassType returnType = new ClassType(Map.class);
        returnType.addGeneric(GenericFactory.create(new ClassType(String.class)));
        ClassType absRecipeType = new ClassType(AbstractRecipe.class);
        absRecipeType.addGeneric(GenericFactory.createWildcard());
        returnType.addGeneric(GenericFactory.create(absRecipeType));

        builder.buildMethod(returnType, "getNestedRecipes").setVisibility(VisibilityType.PUBLIC).addAnnotation(JAnnotationFactory.create(Override.class)).addCode(nestedRecipesCode()).finish();
    }

    /**
     * Generate the code for creating the nested recipes
     * 
     * @return {@link String}[] containing the necessary code
     */
    protected String[] nestedRecipesCode() {
        externalImports.add(new ClassType(HashMap.class));
        List<String> code = new ArrayList<>();
        code.add("Map<String, AbstractRecipe<?>> recipes = new HashMap<>();");

        for (JMethod<?> method : creator.getMethods(Bean.class)) {
            ClassType nestedRecipeType = RecipeGenerator.getRecipeType(creatorType, method);
            code.add("recipes.put(\"" + method.getName() + "\", new " + nestedRecipeType.getSimpleName() + "(this, engine));");
        }

        code.add("return recipes;");
        return code.toArray(new String[code.size()]);
    }

}
