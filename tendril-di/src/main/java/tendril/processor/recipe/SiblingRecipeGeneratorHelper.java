package tendril.processor.recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.Messager;

import tendril.annotationprocessor.exception.InvalidConfigurationException;
import tendril.annotationprocessor.exception.TendrilException;
import tendril.bean.duplicate.Sibling;
import tendril.bean.qualifier.Named;
import tendril.bean.recipe.Injector;
import tendril.codegen.JBase;
import tendril.codegen.VisibilityType;
import tendril.codegen.annotation.JAnnotationFactory;
import tendril.codegen.classes.ClassBuilder;
import tendril.codegen.classes.ConstructorBuilder;
import tendril.codegen.classes.FieldBuilder;
import tendril.codegen.classes.JParameter;
import tendril.codegen.field.JField;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.TypeFactory;
import tendril.codegen.field.value.JValueFactory;
import tendril.codegen.generics.GenericFactory;
import tendril.processor.BlueprintProcessor;
import tendril.util.TendrilStringUtil;

/**
 * Helper which centralized the necessary code for the generation of sibling recipes. This is not a generator as such, but rather container the appropriate features to allow a generator to produce the
 * appropriate code for sibling recipes. Ultimately this is mainly in place to avoid duplicating code between the {@link DuplicateSiblingClassRecipeGenerator} and the
 * {@link DuplicateSiblingMethodRecipeGenerator}.
 */
class SiblingRecipeGeneratorHelper {
	/** The element that is triggering bean creation */
	private final JBase creator;
	/** The type of the bean that is being created */
	private final ClassType beanType;
	/** The type of the enum which drives the duplication */
	private final ClassType blueprintType;
	/** Flag for whether the blueprint is enum derived */
	private final boolean derivedFromEnum;
	/** Messager through which to provide "proper" feedback */
	private final Messager messager;
	/** The generator which is preparing the recipe proper */
	private final AbstractRecipeGenerator<?> generator;

	/** Flag for whether the sibling instance field was used in the generated code */
	private boolean siblingUsed = false;

	/**
	 * CTOR
	 * 
	 * @param creator       {@link JBase} that is triggering the bean's creation
	 * @param beanType      {@link ClassType} the type of bean
	 * @param blueprintType {@link ClassType} of the enum which drives the duplication
	 * @param messager      {@link Messager} that is used by the annotation processor
	 * @param generator     {@link AbstractRecipeGenerator} which is generating the recipe the helper is helping with
	 */
	SiblingRecipeGeneratorHelper(JBase creator, ClassType beanType, ClassType blueprintType, Messager messager, AbstractRecipeGenerator<?> generator) {
		this.creator = creator;
		this.beanType = beanType;
		this.blueprintType = blueprintType;
		this.messager = messager;
		this.generator = generator;

		derivedFromEnum = BlueprintProcessor.isEnumDerived(blueprintType);
	}

	/**
	 * Add the appropriate instance fields for the recipe
	 * 
	 * @param builder {@link ClassBuilder} where the class is being assembled
	 * @throws InvalidConfigurationException if the annotated code is improperly configured
	 */
	void addInstanceFields(ClassBuilder builder) throws InvalidConfigurationException {
		// Instance field for the type that is being built
		addSiblingCopyField(builder);
		// Instance field for the map of qualifying annotations for each copy
		addQualifierMap(builder);
	}

	/**
	 * Add the {@code siblingCopy} instance field
	 * 
	 * @param builder {@link ClassBuilder} where the class is being assembled
	 */
	private void addSiblingCopyField(ClassBuilder builder) {
		FieldBuilder<ClassType> fieldBuilder = builder.buildField(blueprintType, "siblingCopy").setVisibility(VisibilityType.PRIVATE).setFinal(true);
		if (!siblingUsed)
			fieldBuilder.addAnnotation(JAnnotationFactory.create(SuppressWarnings.class, JValueFactory.create("unused")));
		fieldBuilder.finish();
	}

	/**
	 * Add the {@code copyQualifiers} map if it is appropriate to do so.
	 * 
	 * @param builder {@link ClassBuilder} where the class is being assembled
	 * @throws InvalidConfigurationException if the annotated code is improperly configured
	 */
	private void addQualifierMap(ClassBuilder builder) throws InvalidConfigurationException {
		if (!derivedFromEnum)
			return;

		// Instance field for the map of qualifying annotations for each copy
		try {
			generator.addImport(Map.class);
			List<ClassType> generatedAnnotations = BlueprintProcessor.getGeneratedAnnotations(blueprintType);
			List<String> mappings = new ArrayList<>();
			for (ClassType aType : generatedAnnotations) {
				generator.addImport(aType);
				String name = aType.getSimpleName();
				mappings.add("\"" + name + "\", " + name + ".class");
			}

			ClassType qualifierClass = TypeFactory.createClassType(Class.class, GenericFactory.createWildcard());
			ClassType mapType = TypeFactory.createClassType(Map.class, GenericFactory.create(TypeFactory.createClassType(String.class)), GenericFactory.create(qualifierClass));

			builder.buildField(mapType, "copyQualifiers").setVisibility(VisibilityType.PRIVATE).setFinal(true).setCustomInitialization("Map.of(" + TendrilStringUtil.join(mappings) + ")").finish();
		} catch (TendrilException ex) {
			messager.printError("No blueprint qualifier annotations exist for " + blueprintType.getFullyQualifiedName());
			throw new InvalidConfigurationException("No blueprint qualifier annotations exist for " + blueprintType.getFullyQualifiedName(), ex);
		}
	}

	/**
	 * Add the appropriate "extra" code that needs to appear in the recipe constructor
	 * 
	 * @param ctorCode {@link List} of {@link String}s where the CTOR code is being collected
	 */
	void addCtorCode(List<String> ctorCode) {
		// Assign the instance field copy
		ctorCode.add("this.siblingCopy = siblingCopy;");

		// @Named should not be applied
		checkIfNamed(creator);

		// This must be done here rather than as part of setupDescriptor as siblingCopy is not yet initialized in setupDescriptor
		String siblingDescription = "getDescription().setBlueprint(siblingCopy)";
		if (derivedFromEnum)
			siblingDescription += ".addQualifier(copyQualifiers.get(siblingCopy.name()));";
		else
			siblingDescription += ".setName(siblingCopy.getName());";
		ctorCode.add(siblingDescription);
	}

	/**
	 * Build the {@code siblingCopy} CTOR parameter. It is up to the caller to ensure that it is placed in the appropriate location in the CTOR parameter list.
	 * 
	 * @param builder {@link ConstructorBuilder} where the CTOR is being assembled
	 */
	void buildCtorParameter(ConstructorBuilder builder) {
		builder.buildParameter(blueprintType, "siblingCopy").finish();
	}

	/**
	 * Add the sibling field injection, if it is appropriate to do so.
	 * 
	 * @param field     {@link JField} for which the injection code is being generated
	 * @param ctorLines {@link List} of {@link String}s where the CTOR code is being collected
	 * @return {@code boolean} true if the code was generated (if not returns false and the caller must figure out what to do otherwise)
	 */
	boolean addFieldInjection(JField<?> field, List<String> ctorLines) {
		if (!field.getType().equals(blueprintType) || !field.hasAnnotation(Sibling.class))
			return false;

		// @Named should not be applied
		checkIfNamed(field);

		generator.addImport(Injector.class);
		siblingUsed = true;
		ctorLines.add("registerInjector(new " + Injector.class.getSimpleName() + "<" + beanType.getSimpleName() + ">() {");
		ctorLines.add("    @Override");
		ctorLines.add("    public void inject(" + beanType.getSimpleName() + " consumer, Engine engine) {");
		ctorLines.add("        consumer." + field.getName() + " = siblingCopy;");
		ctorLines.add("    }");
		ctorLines.add("});");
		return true;
	}

	/**
	 * Add the appropriate descriptor lines for siblings
	 * 
	 * @param element {@link JBase} being described
	 * @param lines   {@link List} of {@link String}s where the descriptor lines are being collected
	 */
	void addDescriptorLines(JBase element, List<String> lines) {
		// Do nothing if this isn't a sibling
		if (!element.hasAnnotation(Sibling.class))
			return;

		// @Named should not be applied
		checkIfNamed(element);

		siblingUsed = true;
		lines.add("setBlueprint(siblingCopy)");
		if (derivedFromEnum)
			lines.add("addQualifier(copyQualifiers.get(siblingCopy.name()))");
		else
			lines.add("setName(siblingCopy.getName())");
	}

	/**
	 * Check if the {@link Named} annotation is applied to the element
	 * 
	 * @param element {@link JBase} to check
	 */
	private void checkIfNamed(JBase element) {
		if (!derivedFromEnum && creator.hasAnnotation(Named.class)) {
			messager.printWarning(generator.getFullElementName(element) + " has an @Named annotation applied when it is to be derived from the blueprint " + blueprintType);
		}
	}

	/**
	 * Check if the parameter is a sibling
	 * 
	 * @param param {@link JParameter} to check
	 * @return {@code boolean} true if it is a sibling
	 */
	boolean isSiblingParameter(JParameter<?> param) {
		return param.getType().equals(blueprintType) && param.hasAnnotation(Sibling.class);
	}

	/**
	 * Get the name of the sibling copy instance field
	 * 
	 * @return {@link String} the name of the instance field
	 */
	String getSiblingCopyFieldName() {
		return "siblingCopy";
	}
}
