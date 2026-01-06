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
import tendril.codegen.field.type.TypeFactory;

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
     * @throws TendrilException when an issue generating the recipe is encountered
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
     * @throws TendrilException when an issue generating the recipe is encountered
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
     * @throws TendrilException when an issue generating the recipe is encountered
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
     * @throws TendrilException when an issue generating the recipe is encountered
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
     * @throws TendrilException when an issue generating the recipe is encountered
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
     * @throws TendrilException when an issue generating the recipe is encountered
     */
    public static ClassDefinition generateConfiguration(ClassType configType, JClass config, Messager messager) throws TendrilException {
        return new ConfigurationRecipeGenerator(configType, config, messager).generate(getRecipeType(configType), true);
    }
    
    /**
     * Generate the recipe for the entry recipe for duplication.
     * 
     * @param blueprintType {@link ClassType} of the {@link Enum} which drives the duplication
     * @param duplicateType {@link ClassType} of the bean which is to be duplicated
     * @param duplicate {@link JClass} describing the bean which is to be duplicated
     * @param messager {@link Messager} for the annotation processing
     * @return {@link ClassDefinition}
     * @throws TendrilException when an issue generating the recipe is encountered
     */
    public static ClassDefinition generateDuplicateBean(ClassType blueprintType, ClassType duplicateType, JClass duplicate, Messager messager) throws TendrilException {
    	return new DuplicateRecipeGenerator(blueprintType, duplicateType, duplicate, messager).generate(getRecipeType(duplicateType), true);
    }
    
    /**
     * Generate the recipe which will trigger the creation of all copies of the duplicated beans
     * 
     * @param blueprintType {@link ClassType} of the {@link Enum} which drives the duplication
     * @param siblingType {@link ClassType} of the bean which is to be duplicated
     * @param sibling {@link JClass} describing the bean which is to be duplicated
     * @param messager {@link Messager} for the annotation processing
     * @return {@link ClassDefinition}
     * @throws TendrilException when an issue generating the recipe is encountered
     */
    public static ClassDefinition generateDuplicateSiblingBean(ClassType blueprintType, ClassType siblingType, JClass sibling, Messager messager) throws TendrilException {
    	DuplicateSiblingRecipeGenerator generator = new DuplicateSiblingRecipeGenerator(siblingType, sibling, messager, blueprintType);
    	return generator.generate(getSiblingRecipeType(siblingType), false);
    }
    
    /**
     * Derive the recipe type for a bean which is defined within a configuration
     * 
     * @param configType {@link ClassType} defining the configuration
     * @param creator {@link JMethod} where the bean is created
     * @return {@link ClassType}
     */
    static ClassType getRecipeType(ClassType configType, JMethod<?> creator) {
        ClassType providerClass = TypeFactory.createClassType(configType, creator.getName());
        return getRecipeType(providerClass);
        
    }
    
    /**
     * Derive the recipe class for the bean
     * 
     * @param beanType {@link ClassType} indicating the class of the bean
     * @return {@link ClassType}
     */
    private static ClassType getRecipeType(ClassType beanType) {
        return TypeFactory.createClassType(beanType, "Recipe");
    }
    
    /**
     * Derive the recipe class for the sibling bean
     * 
     * @param siblingType {@link ClassType} indicating the class of the sibling bean
     * @return {@link ClassType}
     */
    static ClassType getSiblingRecipeType(ClassType siblingType) {
    	return getRecipeType(TypeFactory.createClassType(siblingType, "Sibling"));
    }
}
