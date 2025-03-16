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

import tendril.annotationprocessor.ProcessingException;
import tendril.bean.recipe.ConfigurationRecipe;
import tendril.codegen.VisibilityType;
import tendril.codegen.classes.ClassBuilder;
import tendril.codegen.classes.method.JMethod;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.generics.GenericFactory;
import tendril.context.Engine;

/**
 * Generator for recipes where the bean is created by a method in a configuration
 */
public class MethodRecipeGenerator extends AbstractRecipeGenerator<JMethod<?>> {
    
    /** The type of the configuration class containing the method */
    private final ClassType configType;
    /** The method which is to create the bean */
    private final JMethod<?> beanCreator;

    /**
     * CTOR
     * 
     * @param configType {@link ClassType} indicating the configuration class
     * @param beanType {@link ClassType} of the bean which is to be produced
     * @param beanCreator {@link JMethod} which is to produce the bean
     */
    MethodRecipeGenerator(ClassType configType, ClassType beanType, JMethod<?> beanCreator) {
        super(beanType, beanCreator);
        this.configType = configType;
        this.beanCreator = beanCreator;
    }

    /**
     * @see tendril.processor.recipe.AbstractRecipeGenerator#validateCreator()
     */
    @Override
    protected void validateCreator() {
        if (beanCreator.isStatic())
            throwValidationException("static");
        if (beanCreator.getVisibility() == VisibilityType.PRIVATE)
            throwValidationException("private");
    }
    
    /**
     * Helper to throw an exception if class validation fails
     * 
     * @param reason {@link String} cause of the failure
     */
    private void throwValidationException(String reason) {
        throw new ProcessingException(configType.getFullyQualifiedName() + "::" + beanCreator.getName() +
                " cannot be be used to create a bean because it is " + reason);
    }

    /**
     * @see tendril.processor.recipe.AbstractRecipeGenerator#populateBuilder(tendril.codegen.classes.ClassBuilder)
     */
    @Override
    protected void populateBuilder(ClassBuilder builder) {
        // Build up the contents of the recipe
        generateConstructor(configType, builder);
        generateRecipeDescriptor(builder);
        generateCreateInstance(beanCreator, builder);
    }
    
    /**
     * Generate the constructor for the recipe
     * 
     * @param builder {@link ClassBuilder} where the recipe class is being defined
     */
    private void generateConstructor(ClassType configType, ClassBuilder builder) {
        ClassType configRecipeType = new ClassType(ConfigurationRecipe.class);
        configRecipeType.addGeneric(GenericFactory.create(configType));
        
        // Instance field for the config
        builder.buildField(configRecipeType, "config").setVisibility(VisibilityType.PRIVATE).setFinal(true).finish();
        // Add the constructor
        builder.buildConstructor().setVisibility(VisibilityType.PUBLIC)
            .buildParameter(configRecipeType, "config").finish()
            .buildParameter(new ClassType(Engine.class), "engine").finish()
            .addCode("super(engine, " + creatorType.getSimpleName() + ".class);",
                     "this.config = config;")
            .finish();
    }

}
