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

import javax.annotation.processing.Messager;

import tendril.codegen.classes.ClassBuilder;
import tendril.codegen.classes.JClass;
import tendril.codegen.field.type.ClassType;

/**
 * Generator for recipe classes where the bean is defined as part of its own class (i.e.: bean definition and description are all within a single class.
 */
class BeanRecipeGenerator extends ClassRecipeGenerator {

    /**
     * CTOR
     * 
     * @param beanType {@link ClassType} indicating the type of the bean
     * @param bean {@link JClass} where the bean is defined
     * @param messager {@link Messager} that is used by the annotation processor
     */
    BeanRecipeGenerator(ClassType beanType, JClass bean, Messager messager) {
        super(beanType, bean, messager);
    }

    /**
     * @see tendril.processor.recipe.RecipeGenerator#populateBuilder(tendril.codegen.classes.ClassBuilder)
     */
    protected void populateBuilder(ClassBuilder builder) {
        // Build up the contents of the recipe
        generateConstructor(builder);
        generateRecipeDescriptor(builder);
        generateCreateInstance(builder);
        processPostConstruct(builder);
    }

}
