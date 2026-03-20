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
import java.util.List;

import javax.annotation.processing.Messager;

import tendril.annotationprocessor.exception.InvalidConfigurationException;
import tendril.annotationprocessor.exception.TendrilException;
import tendril.codegen.JBase;
import tendril.codegen.VisibilityType;
import tendril.codegen.classes.ClassBuilder;
import tendril.codegen.classes.ConstructorBuilder;
import tendril.codegen.classes.JClass;
import tendril.codegen.classes.JParameter;
import tendril.codegen.field.JField;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.TypeFactory;
import tendril.context.Engine;

/**
 * Generates the recipe code for sibling beans, ergo the recipe which makes the specific copies tied to bean to be duplicated
 */
class DuplicateSiblingClassRecipeGenerator extends BeanRecipeGenerator {
	/** Helper for the creation of sibling duplicate recipes */
	private final SiblingRecipeGeneratorHelper siblingHelper;

	/**
	 * CTOR
	 * 
	 * @param beanType      {@link ClassType} of the bean which is to be created
	 * @param bean          {@link JClass} describing the class of the bean
	 * @param messager      {@link Messager} for the processing
	 * @param blueprintType {@link ClassType} of the enum which drives the duplication
	 */
	DuplicateSiblingClassRecipeGenerator(ClassType beanType, JClass bean, Messager messager, ClassType blueprintType) {
		super(beanType, bean, messager);
		siblingHelper = new SiblingRecipeGeneratorHelper(bean, beanType, blueprintType, messager, this);
	}

	/**
	 * Delay the creation of the siblingCopy instance field until the end so that we know whether or not it has been used.
	 * 
	 * @see tendril.processor.recipe.BeanRecipeGenerator#populateBuilder(tendril.codegen.classes.ClassBuilder)
	 */
	@Override
	protected void populateBuilder(ClassBuilder builder) throws TendrilException {
		super.populateBuilder(builder);
		siblingHelper.addInstanceFields(builder);
	}

	/**
	 * @see tendril.processor.recipe.ClassRecipeGenerator#generateConstructor(tendril.codegen.classes.ClassBuilder)
	 */
	@Override
	protected void generateConstructor(ClassBuilder builder) throws InvalidConfigurationException {
		// CTOR contents
		List<String> ctorCode = new ArrayList<>();
		ctorCode.add("super(engine, " + creatorType.getSimpleName() + ".class, " + isPrimary + ", " + isFallback + ");");
		siblingHelper.addCtorCode(ctorCode);

		generateFieldConsumers(ctorCode);
		generateMethodConsumers(ctorCode);

		ConstructorBuilder ctorBuilder = builder.buildConstructor().setVisibility(VisibilityType.PUBLIC).buildParameter(TypeFactory.createClassType(Engine.class), "engine").finish();
		siblingHelper.buildCtorParameter(ctorBuilder);
		ctorBuilder.addCode(ctorCode.toArray(new String[ctorCode.size()])).finish();
	}

	/**
	 * @see tendril.processor.recipe.ClassRecipeGenerator#generateFieldInjection(tendril.codegen.field.JField, java.lang.String, java.util.List)
	 */
	@Override
	protected void generateFieldInjection(JField<?> field, String fieldTypeName, List<String> ctorLines) {
		if (!siblingHelper.addFieldInjection(field, ctorLines))
			super.generateFieldInjection(field, fieldTypeName, ctorLines);
	}

	/**
	 * If an @Sibling annotation is present, this is "converted" to the appropriate qualifier for the sibling.
	 * 
	 * @see tendril.processor.recipe.AbstractRecipeGenerator#getDescriptorLines(tendril.codegen.JBase)
	 */
	@Override
	protected List<String> getDescriptorLines(JBase element) {
		List<String> lines = super.getDescriptorLines(element);
		siblingHelper.addDescriptorLines(element, lines);
		return lines;
	}

	/**
	 * @see tendril.processor.recipe.AbstractRecipeGenerator#createParameterInjectionCodeRhs(tendril.codegen.classes.JParameter)
	 */
	@Override
	protected String createParameterInjectionCodeRhs(JParameter<?> param) throws InvalidConfigurationException {
		if (siblingHelper.isSiblingParameter(param))
			return siblingHelper.getSiblingCopyFieldName();
		return super.createParameterInjectionCodeRhs(param);
	}
}
