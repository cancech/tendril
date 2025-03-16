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

import tendril.annotationprocessor.ClassDefinition;
import tendril.bean.Configuration;
import tendril.codegen.classes.JClass;
import tendril.codegen.classes.method.JMethod;
import tendril.codegen.field.type.ClassType;

/**
 * Generator for Recipe files, intended to be used by Annotation Processors that need to generate recipe files that are to be used by the DI capability at runtime.
 */
public final class RecipeGenerator {
    
    /**
     * Hidden CTOR
     */
    private RecipeGenerator() {}

    /**
     * Generate the {@link ClassDefinition} for a {@link JClass} which defines and is the bean
     * 
     * @param creator {@link JClass} defining the bean
     * @return {@link ClassDefinition}
     */
    public static ClassDefinition generate(JClass creator) {
        return generate(creator.getType(), creator, true);
    }

    /**
     * Generate the {@link ClassDefinition} for a {@link JClass} which defines and is the bean
     * 
     * @param beanType {@link ClassType} indicating the type of the bean
     * @param creator {@link JClass} defining the bean
     * @return {@link ClassDefinition}
     */
    public static ClassDefinition generate(ClassType beanType, JClass creator) {
        return generate(beanType, creator, true);
    }

    /**
     * Generate the {@link ClassDefinition} for a {@link JClass} which defines and is the bean
     * 
     * @param beanType {@link ClassType} indicating the type of the bean
     * @param creator {@link JClass} defining the bean
     * @param annotateRegistry boolean true if the recipe is to be added to the generated registry (false will create the recipe but not register it)
     * @return {@link ClassDefinition}
     */
    public static ClassDefinition generate(ClassType beanType, JClass creator, boolean annotateRegistry) {
        return new BeanRecipeGenerator(beanType, creator).generate(getRecipeType(beanType), annotateRegistry);
    }
    
    /**
     * Generate the {@link ClassDefinition} for a {@link JMethod} which creates a bean as part of a {@link Configuration}.
     * 
     * @param configType {@link ClassType} of the configuration (i.e.: contains the method)
     * @param creator {@link JMethod} which creates the bean
     * @return {@link ClassDefinition}
     */
    public static ClassDefinition generate(ClassType configType, JMethod<?> creator) {
        MethodRecipeGenerator generator = new MethodRecipeGenerator(configType, creator.getType().asClassType(), creator);
        return generator.generate(getRecipeType(configType, creator), false);
    }
    
    /**
     * Generate the recipe for a {@link Configuration}
     * 
     * @param config {@link JClass} containing the configuration
     * @return {@link ClassDefinition}
     */
    public static ClassDefinition generateConfiguration(JClass config) {
        return generateConfiguration(config.getType(), config);
    }

    /**
     * Generate the recipe for a {@link Configuration}
     * 
     * @param configType {@link ClassType} of the configuration
     * @param config {@link JClass} containing the configuration
     * @return {@link ClassDefinition}
     */
    public static ClassDefinition generateConfiguration(ClassType configType, JClass config) {
        return new ConfigurationRecipeGenerator(configType, config).generate(getRecipeType(configType), true);
    }
    
    /**
     * Derive the recipe type for a bean which is defined within a configuration
     * 
     * @param configType {@link ClassType} defining the configuration
     * @param creator {@link JMethod} where the bean is created
     * @return {@link ClassType}
     */
    static ClassType getRecipeType(ClassType configType, JMethod<?> creator) {
        ClassType providerClass = configType.generateFromClassSuffix(creator.getName());
        return getRecipeType(providerClass);
        
    }
    
    /**
     * Derive the recipe class for the bean
     * 
     * @param beanType {@link ClassType} indicating the class of the bean
     * @return {@link ClassType}
     */
    private static ClassType getRecipeType(ClassType beanType) {
        return beanType.generateFromClassSuffix("Recipe");
    }
}
