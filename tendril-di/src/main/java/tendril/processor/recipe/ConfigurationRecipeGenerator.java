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

import tendril.TendrilStartupException;
import tendril.annotationprocessor.exception.TendrilException;
import tendril.bean.Bean;
import tendril.bean.Configuration;
import tendril.bean.duplicate.GeneratedBlueprint;
import tendril.bean.recipe.AbstractRecipe;
import tendril.codegen.VisibilityType;
import tendril.codegen.annotation.JAnnotation;
import tendril.codegen.annotation.JAnnotationFactory;
import tendril.codegen.classes.ClassBuilder;
import tendril.codegen.classes.JClass;
import tendril.codegen.classes.method.JMethod;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.TypeFactory;
import tendril.codegen.generics.GenericFactory;
import tendril.processor.BlueprintProcessor;
import tendril.processor.GeneratedBlueprintProcessor;

/**
 * Generator for {@link Configuration} recipes
 */
class ConfigurationRecipeGenerator extends ClassRecipeGenerator {

	/**
	 * CTOR
	 * 
	 * @param configType {@link ClassType} of the configuration
	 * @param config     {@link JClass} of the configuration
	 * @param messager   {@link Messager} that is used by the annotation processor
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
	 * @throws TendrilException if an issue is encountered generating the configuration recipe
	 */
	private void generateNestedRecipes(ClassBuilder builder) throws TendrilException {
		ClassType returnType = TypeFactory.createClassType(Map.class, GenericFactory.create(TypeFactory.createClassType(String.class)),
				GenericFactory.create(TypeFactory.createClassType(AbstractRecipe.class, GenericFactory.createWildcard())));
		builder.buildMethod(returnType, "getNestedRecipes").setVisibility(VisibilityType.PUBLIC).addAnnotation(JAnnotationFactory.create(Override.class)).addCode(nestedRecipesCode()).finish();
	}

	/**
	 * Generate the code for creating the nested recipes
	 * 
	 * @return {@link String}[] containing the necessary code
	 * @throws TendrilException if an issue is encountered generating the configuration recipe
	 */
	protected String[] nestedRecipesCode() throws TendrilException {
		externalImports.add(TypeFactory.createClassType(HashMap.class));
		List<String> code = new ArrayList<>();
		code.add("Map<String, AbstractRecipe<?>> recipes = new HashMap<>();");

		// Handle the methods which create "normal" beans
		for (JMethod<?> method : creator.getMethods(Bean.class)) {
			ClassType nestedRecipeType = RecipeGenerator.getRecipeType(creatorType, method);
			code.add("recipes.put(\"" + method.getName() + "\", new " + nestedRecipeType.getSimpleName() + "(this, engine));");
		}

		// Handle the methods which create duplicated beans
		for (JMethod<?> method : creator.getMethods()) {
			JAnnotation duplicateAnnotation = duplicationAnnotation(method);
			if (duplicateAnnotation != null) {
				ClassType blueprintType = GeneratedBlueprintProcessor.getBlueprintForAnnotation(duplicateAnnotation.getType());
				externalImports.add(blueprintType);
				ClassType nestedRecipeType = RecipeGenerator.getSiblingRecipeType(creatorType, method);
				
				// Enum based duplicates iterate through enum values
				if (BlueprintProcessor.isEnumDerived(blueprintType)) {
					code.add("for(" + blueprintType.getClassName() + " b : " + blueprintType.getClassName() + ".values())");
					code.add("    recipes.put(\"" + method.getName() + "\" + b.name()" + ", new " + nestedRecipeType.getClassName() + "(this, engine, b));");
				} else {
					// Class based duplicates get the blueprints from the engine
					externalImports.add(TypeFactory.createClassType(TendrilStartupException.class));
					code.add("for(" + blueprintType.getSimpleName() + " b: engine.getBlueprints(" + blueprintType.getClassName() + ".class)) {");
					code.add("	String copyName = \"" + method.getName() + "\" + b.getName();");
					code.add("	if (recipes.containsKey(copyName))");
					code.add("		throw new TendrilStartupException(\"" + blueprintType + " has more than one copies named \" + copyName);");
					code.add("    recipes.put(copyName, new " + nestedRecipeType.getSimpleName() + "(this, engine, b));");
					code.add("}");
				}
			}
		}

		code.add("return recipes;");
		return code.toArray(new String[code.size()]);
	}

	/**
	 * Check to see whether the method is annotated for duplication. If it is, return the annotation which triggers duplication, otherwise returns null.
	 * 
	 * @param method {@link JMethod} to check
	 * @return {@link JAnnotation} which triggers duplication (or null if not applicable)
	 */
	private JAnnotation duplicationAnnotation(JMethod<?> method) {
		for (JAnnotation a : method.getAnnotations()) {
			if (a.hasAnnotation(GeneratedBlueprint.class))
				return a;
		}

		return null;
	}
}
