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

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.Messager;

import tendril.TendrilStartupException;
import tendril.annotationprocessor.exception.TendrilException;
import tendril.bean.Bean;
import tendril.bean.Configuration;
import tendril.bean.Replaces;
import tendril.bean.recipe.AbstractRecipe;
import tendril.codegen.VisibilityType;
import tendril.codegen.annotation.JAnnotationFactory;
import tendril.codegen.classes.ClassBuilder;
import tendril.codegen.classes.JClass;
import tendril.codegen.classes.method.JMethod;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.TypeFactory;
import tendril.codegen.generics.GenericFactory;
import tendril.processor.BlueprintHelper;

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
		generateNestedRecipes(builder, "getNestedRecipes", nestedRecipesCode(false));
		generateNestedRecipes(builder, "getNestedReplacementRecipes", nestedRecipesCode(true));
	}

	/**
	 * Generate the method which populates the nested method bean creators.
	 * 
	 * @param builder {@link ClassBuilder} where the recipe is being defined
	 * @throws TendrilException if an issue is encountered generating the configuration recipe
	 */
	private void generateNestedRecipes(ClassBuilder builder, String methodName, String[] code) throws TendrilException {
		ClassType returnType = TypeFactory.createClassType(Map.class, GenericFactory.create(TypeFactory.createClassType(String.class)),
				GenericFactory.create(TypeFactory.createClassType(AbstractRecipe.class, GenericFactory.createWildcard())));
		builder.buildMethod(returnType, methodName).setVisibility(VisibilityType.PUBLIC).addAnnotation(JAnnotationFactory.create(Override.class)).addCode(code).finish();
	}
	
	/**
	 * Generate the code for creating the nested recipes
	 * 
	 * @param isReplacement boolean {@code true} is the nested recipes are replacements
	 * @return {@link String}[] containing the necessary code
	 * @throws TendrilException if an issue is encountered generating the configuration recipe
	 */
	protected String[] nestedRecipesCode(boolean isReplacement) throws TendrilException {
		externalImports.add(TypeFactory.createClassType(HashMap.class));
		List<String> code = new ArrayList<>();
		code.add("Map<String, AbstractRecipe<?>> recipes = new HashMap<>();");
		
		if (isReplacement)
			populateNestedReplacements(code);
		else
			populateNestedRecipes(code);
		
		code.add("return recipes;");
		return code.toArray(new String[code.size()]);
	}

	/**
	 * Populate the code for creating/tracking the nested recipes. This requires that the map where the recipes are stored is called {@code recipes}
	 * 
	 * @param code {@link List} of {@link String}s where the code is tracked
	 * @throws TendrilException if there is an issue generating the code
	 */
	protected void populateNestedRecipes(List<String> code) throws TendrilException {
		// Handle the methods which create "normal" beans
		appendBeanRecipes(Bean.class, code);
		
		// Handle the methods which create duplicated beans
		for (JMethod<?> method : creator.getMethods()) {
			ClassType blueprintType = BlueprintHelper.retrieveBlueprint(method);
			if (blueprintType != null) {
				externalImports.add(blueprintType);
				ClassType nestedRecipeType = RecipeGenerator.getSiblingRecipeType(creatorType, method);
				
				// Class based duplicates get the blueprints from the engine
				externalImports.add(TypeFactory.createClassType(TendrilStartupException.class));
				code.add("for(" + blueprintType.getSimpleName() + " b: engine.getBlueprints(" + RecipeGeneratorHelper.getClassReference(blueprintType) + ")) {");
				code.add("	String copyName = \"" + method.getName() + "\" + b.getName();");
				code.add("	if (recipes.containsKey(copyName))");
				code.add("		throw new TendrilStartupException(\"" + blueprintType + " has more than one copies named \" + copyName);");
				code.add("    recipes.put(copyName, new " + nestedRecipeType.getSimpleName() + "(this, engine, b));");
				code.add("}");
			}
		}
	}
	
	/**
	 * Populate the code for creating/tracking the nested replacement recipes. This requires that the map where the recipes are stored is called {@code recipes}
	 * 
	 * @param code {@link List} of {@link String}s where the code is tracked
	 */
	protected void populateNestedReplacements(List<String> code) {
		// Handle the methods which create "normal" beans
		appendBeanRecipes(Replaces.class, code);
	}
	
	/**
	 * Append bean recipes to the map of recipes that is being assembled
	 * 
	 * @param annotation {@link Class} extending {@link Annotation} which the creation method is annotated with
	 * @param code {@link List} of {@link String} lines of code where the recipe map is being assembled
	 */
	private void appendBeanRecipes(Class<? extends Annotation> annotation, List<String> code) {
		for (JMethod<?> method : creator.getMethods(annotation)) {
			ClassType nestedRecipeType = RecipeGenerator.getRecipeType(creatorType, method);
			code.add("recipes.put(\"" + method.getName() + "\", new " + nestedRecipeType.getSimpleName() + "(this, engine));");
		}
	}
}
