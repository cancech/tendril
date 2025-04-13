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

import tendril.annotationprocessor.ClassDefinition;
import tendril.annotationprocessor.exception.TendrilException;
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
     * @param messager {@link Messager} that is used by the annotation processor
     * @return {@link ClassDefinition}
     * @throws TendrilException
     */
    public static ClassDefinition generate(JClass creator, Messager messager) throws TendrilException {
        return generate(creator.getType(), creator, messager, true);
    }

    /**
     * Generate the {@link ClassDefinition} for a {@link JClass} which defines and is the bean
     * 
     * @param beanType {@link ClassType} indicating the type of the bean
     * @param creator {@link JClass} defining the bean
     * @param messager {@link Messager} that is used by the annotation processor
     * @return {@link ClassDefinition}
     * @throws TendrilException
     */
    public static ClassDefinition generate(ClassType beanType, JClass creator, Messager messager) throws TendrilException {
        return generate(beanType, creator, messager, true);
    }

    /**
     * Generate the {@link ClassDefinition} for a {@link JClass} which defines and is the bean
     * 
     * @param beanType {@link ClassType} indicating the type of the bean
     * @param creator {@link JClass} defining the bean
     * @param messager {@link Messager} that is used by the annotation processor
     * @param annotateRegistry boolean true if the recipe is to be added to the generated registry (false will create the recipe but not register it)
     * @return {@link ClassDefinition}
     * @throws TendrilException
     */
    public static ClassDefinition generate(ClassType beanType, JClass creator, Messager messager, boolean annotateRegistry) throws TendrilException {
        return new BeanRecipeGenerator(beanType, creator, messager).generate(getRecipeType(beanType), annotateRegistry);
    }
    
    /**
     * Generate the {@link ClassDefinition} for a {@link JMethod} which creates a bean as part of a {@link Configuration}.
     * 
     * @param configType {@link ClassType} of the configuration (i.e.: contains the method)
     * @param creator {@link JMethod} which creates the bean
     * @param messager {@link Messager} that is used by the annotation processor
     * @return {@link ClassDefinition}
     * @throws TendrilException
     */
    public static ClassDefinition generate(ClassType configType, JMethod<?> creator, Messager messager) throws TendrilException {
        MethodRecipeGenerator generator = new MethodRecipeGenerator(configType, creator.getType().asClassType(), creator, messager);
        return generator.generate(getRecipeType(configType, creator), false);
    }
    
    /**
     * Generate the recipe for a {@link Configuration}
     * 
     * @param config {@link JClass} containing the configuration
     * @param messager {@link Messager} that is used by the annotation processor
     * @return {@link ClassDefinition}
     * @throws TendrilException
     */
    public static ClassDefinition generateConfiguration(JClass config, Messager messager) throws TendrilException {
        return generateConfiguration(config.getType(), config, messager);
    }

    /**
     * Generate the recipe for a {@link Configuration}
     * 
     * @param configType {@link ClassType} of the configuration
     * @param config {@link JClass} containing the configuration
     * @param messager {@link Messager} that is used by the annotation processor
     * @return {@link ClassDefinition}
     * @throws TendrilException
     */
    public static ClassDefinition generateConfiguration(ClassType configType, JClass config, Messager messager) throws TendrilException {
        return new ConfigurationRecipeGenerator(configType, config, messager).generate(getRecipeType(configType), true);
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
