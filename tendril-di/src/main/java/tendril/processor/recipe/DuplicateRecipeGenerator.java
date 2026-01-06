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

import javax.annotation.processing.Messager;

import tendril.annotationprocessor.exception.InvalidConfigurationException;
import tendril.annotationprocessor.exception.TendrilException;
import tendril.bean.recipe.AbstractRecipe;
import tendril.bean.recipe.ConfigurationRecipe;
import tendril.codegen.VisibilityType;
import tendril.codegen.annotation.JAnnotationFactory;
import tendril.codegen.classes.ClassBuilder;
import tendril.codegen.classes.JClass;
import tendril.codegen.field.JField;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.TypeFactory;
import tendril.context.Engine;

/**
 * Generator for "entry" recipe for blueprint driven duplicates. Much like a configuration recipe, this is not a recipe for a bean as such, but rather an entry point for
 * the individuals recipes that will create the required copies.
 */
public class DuplicateRecipeGenerator extends ConfigurationRecipeGenerator {
	/** The type of the {@link Enum} that acts as the blueprint driving the duplicates */
	private final ClassType blueprintType;

	/**
	 * CTOR
	 * 
	 * @param blueprintType {@link ClassType} of the {@link Enum} which is driving the creation of the duplicates
	 * @param beanType {@link ClassType} that is to be duplicated
     * @param creator  {@link JClass} which defines and creates the bean
     * @param messager {@link Messager} that is used by the annotation processor
	 */
	DuplicateRecipeGenerator(ClassType blueprintType, ClassType beanType, JClass creator, Messager messager) {
		super(beanType, creator, messager);
		this.blueprintType = blueprintType;
	}

	/**
	 * @see tendril.processor.recipe.AbstractRecipeGenerator#getRecipeClass()
	 * 
	 * Override to replace the default behavior and force the ConfigurationRecipe base class
	 */
	@SuppressWarnings("rawtypes")
	@Override
	protected Class<? extends AbstractRecipe> getRecipeClass() throws InvalidConfigurationException {
		return ConfigurationRecipe.class;
	}
	
	/**
	 * This is not required for the Duplication entry recipe, and so this does nothing
	 * 
	 * @see tendril.processor.recipe.ClassRecipeGenerator#generateFieldInjection(tendril.codegen.field.JField, java.lang.String, java.util.List)
	 */
	@Override
	protected void generateFieldInjection(JField<?> field, String fieldTypeName, List<String> ctorLines) {
	}
	
	/**
	 * This is not required for the Duplication entry recipe, and so this does nothing
	 * 
	 * @see tendril.processor.recipe.ClassRecipeGenerator#generateFieldConsumers(java.util.List)
	 */
	@Override
	protected void generateFieldConsumers(List<String> ctorLines) throws InvalidConfigurationException {
	}
	
	/**
	 * This is not required for the Duplication entry recipe, and so this does nothing
	 * 
	 * @see tendril.processor.recipe.ClassRecipeGenerator#generateMethodConsumers(java.util.List)
	 */
	@Override
	protected void generateMethodConsumers(List<String> ctorLines) throws InvalidConfigurationException {
	}

	/**
	 * @see tendril.processor.recipe.ClassRecipeGenerator#generateCreateInstance(tendril.codegen.classes.ClassBuilder)
	 * 
	 * This will not produce a bean, so while an implementation must be provided, it does nothing.
	 */
	@Override
	protected void generateCreateInstance(ClassBuilder builder) throws TendrilException {
		builder.buildMethod(creatorType, "createInstance").setVisibility(VisibilityType.PROTECTED).addAnnotation(JAnnotationFactory.create(Override.class))
				.buildParameter(TypeFactory.createClassType(Engine.class), "engine").finish().addCode("return null;").finish();
	}

	/**
	 * The nested recipe code reflects the creation of the "child" beans, driven by the different blueprint values
	 * 
	 * @see tendril.processor.recipe.ConfigurationRecipeGenerator#nestedRecipesCode()
	 */
	@Override
	protected String[] nestedRecipesCode() {
		ClassType siblingType = RecipeGenerator.getSiblingRecipeType(creatorType);
		externalImports.add(siblingType);
		externalImports.add(TypeFactory.createClassType(HashMap.class));
		externalImports.add(blueprintType);
		List<String> code = new ArrayList<>();
		code.add("Map<String, AbstractRecipe<?>> recipes = new HashMap<>();");
		code.add("for(" + blueprintType.getClassName() + " copy: " + blueprintType.getClassName() + ".values()) {");
		code.add("	recipes.put(copy.name(), new " + siblingType.getClassName() + "(engine, copy));");
		code.add("}");

		code.add("return recipes;");
		return code.toArray(new String[code.size()]);
	}
}
