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
import java.util.Map;

import javax.annotation.processing.Messager;

import tendril.annotationprocessor.exception.InvalidConfigurationException;
import tendril.annotationprocessor.exception.TendrilException;
import tendril.bean.duplicate.Sibling;
import tendril.bean.recipe.Injector;
import tendril.codegen.JBase;
import tendril.codegen.VisibilityType;
import tendril.codegen.classes.ClassBuilder;
import tendril.codegen.classes.JClass;
import tendril.codegen.classes.JParameter;
import tendril.codegen.field.JField;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.TypeFactory;
import tendril.codegen.generics.GenericFactory;
import tendril.context.Engine;
import tendril.processor.BlueprintProcessor;
import tendril.util.TendrilStringUtil;

/**
 * Generates the recipe code for sibling beans, ergo the recipe which makes the specific copies tied to bean to be duplicated
 */
class DuplicateSiblingRecipeGenerator extends BeanRecipeGenerator {
	/** The type of the enum which drives the duplication */
	private final ClassType blueprintType;

	/**
	 * CTOR
	 * 
	 * @param beanType {@link ClassType} of the bean which is to be created
	 * @param bean {@link JClass} describing the class of the bean
	 * @param messager {@link Messager} for the processing
	 * @param blueprintType {@link ClassType} of the enum which drives the duplication
	 */
	DuplicateSiblingRecipeGenerator(ClassType beanType, JClass bean, Messager messager, ClassType blueprintType) {
		super(beanType, bean, messager);
		this.blueprintType = blueprintType;
	}
	
	/**
	 * @see tendril.processor.recipe.ClassRecipeGenerator#generateConstructor(tendril.codegen.classes.ClassBuilder)
	 */
	@Override
    protected void generateConstructor(ClassBuilder builder) throws InvalidConfigurationException {

        // Instance field for the type that is being built
        builder.buildField(blueprintType, "siblingCopy").setVisibility(VisibilityType.PRIVATE).setFinal(true).finish();
        
        // Instance field for the map of qualifying annotations for each copy
        try {
    		addImport(Map.class);
			List<ClassType> generatedAnnotations = BlueprintProcessor.getGeneratedAnnotations(blueprintType);
			List<String> mappings = new ArrayList<>();
			for (ClassType aType: generatedAnnotations) {
				addImport(aType);
				String name = aType.getSimpleName();
				mappings.add("\"" + name + "\", " + name + ".class");
			}

			ClassType qualifierClass = TypeFactory.createClassType(Class.class, GenericFactory.createWildcard());
			ClassType mapType = TypeFactory.createClassType(Map.class, GenericFactory.create(TypeFactory.createClassType(String.class)), GenericFactory.create(qualifierClass));
			
			builder.buildField(mapType, "copyQualifiers").setVisibility(VisibilityType.PRIVATE).setFinal(true)
				.setCustomInitialization("Map.of(" + TendrilStringUtil.join(mappings) + ")").finish();
        } catch (TendrilException ex) {
        	messager.printError("No blueprint qualifier annotations exist for " + blueprintType.getFullyQualifiedName());
        	throw new InvalidConfigurationException("No blueprint qualifier annotations exist for " + blueprintType.getFullyQualifiedName(), ex);
        }
		
		
        // CTOR contents
        List<String> ctorCode = new ArrayList<>();
        ctorCode.add("super(engine, " + creatorType.getSimpleName() + ".class, " + isPrimary + ", " + isFallback + ");");
        ctorCode.add("this.siblingCopy = siblingCopy;");
        generateFieldConsumers(ctorCode);
        generateMethodConsumers(ctorCode);
        ctorCode.add("getDescription().addQualifier(copyQualifiers.get(this.siblingCopy.name()));");

        builder.buildConstructor().setVisibility(VisibilityType.PUBLIC).buildParameter(TypeFactory.createClassType(Engine.class), "engine").finish()
        	.buildParameter(blueprintType, "siblingCopy").finish().addCode(ctorCode.toArray(new String[ctorCode.size()])).finish();
    }
	
	/**
	 * @see tendril.processor.recipe.ClassRecipeGenerator#generateFieldInjection(tendril.codegen.field.JField, java.lang.String, java.util.List)
	 */
	@Override
	protected void generateFieldInjection(JField<?> field, String fieldTypeName, List<String> ctorLines) {
		if (field.getType().equals(blueprintType) && field.hasAnnotation(Sibling.class)) {
            addImport(Injector.class);
	        ctorLines.add("registerInjector(new " + Injector.class.getSimpleName() + "<" + creatorType.getSimpleName() + ">() {");
	        ctorLines.add("    @Override");
	        ctorLines.add("    public void inject(" + creatorType.getSimpleName() + " consumer, Engine engine) {");
	        ctorLines.add("        consumer." + field.getName() + " = siblingCopy;");
	        ctorLines.add("    }");
	        ctorLines.add("});");
		} else
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
		if (element.hasAnnotation(Sibling.class))
			lines.add("addQualifier(copyQualifiers.get(siblingCopy.name()))");
		return lines;
	}
	
	/**
	 * @see tendril.processor.recipe.AbstractRecipeGenerator#createParameterInjectionCodeRhs(tendril.codegen.classes.JParameter)
	 */
	@Override
	protected String createParameterInjectionCodeRhs(JParameter<?> param) throws InvalidConfigurationException {
		if (param.getType().equals(blueprintType) && param.hasAnnotation(Sibling.class))
			return "siblingCopy";
		return super.createParameterInjectionCodeRhs(param);
	}
}
