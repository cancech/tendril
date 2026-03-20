package tendril.processor.recipe;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.Messager;

import tendril.annotationprocessor.exception.InvalidConfigurationException;
import tendril.bean.Configuration;
import tendril.codegen.JBase;
import tendril.codegen.VisibilityType;
import tendril.codegen.classes.ClassBuilder;
import tendril.codegen.classes.ConstructorBuilder;
import tendril.codegen.classes.JParameter;
import tendril.codegen.classes.method.JMethod;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.TypeFactory;
import tendril.context.Engine;

/**
 * Generates the recipe code for sibling beans which are produced within a {@link Configuration}, ergo the recipe which makes the specific copies tied to bean to be duplicated
 */
public class DuplicateSiblingMethodRecipeGenerator extends MethodRecipeGenerator {
	/** Helper for the creation of sibling duplicate recipes */
	private final SiblingRecipeGeneratorHelper siblingHelper;

	/**
	 * CTOR
	 * 
	 * @param configType    {@link ClassType} indicating the configuration class
	 * @param beanType      {@link ClassType} of the bean which is to be produced
	 * @param beanCreator   {@link JMethod} which is to produce the bean
	 * @param messager      {@link Messager} that is used by the annotation processor
	 * @param blueprintType {@link ClassType} of the enum which drives the duplication
	 */
	DuplicateSiblingMethodRecipeGenerator(ClassType configType, ClassType beanType, JMethod<?> beanCreator, Messager messager, ClassType blueprintType) {
		super(configType, beanType, beanCreator, messager);
		siblingHelper = new SiblingRecipeGeneratorHelper(beanCreator, beanType, blueprintType, messager, this);
	}

	/**
	 * Delay the creation of the siblingCopy instance field until the end so that we know whether or not it has been used.
	 * 
	 * @see tendril.processor.recipe.BeanRecipeGenerator#populateBuilder(tendril.codegen.classes.ClassBuilder)
	 */
	@Override
	protected void populateBuilder(ClassBuilder builder) throws InvalidConfigurationException {
		super.populateBuilder(builder);
		siblingHelper.addInstanceFields(builder);
	}

	/**
	 * @see tendril.processor.recipe.ClassRecipeGenerator#generateConstructor(tendril.codegen.classes.ClassBuilder)
	 */
	@Override
	protected void generateConstructor(ClassType configRecipeType, ClassBuilder builder) throws InvalidConfigurationException {
		// Instance fields for the config (need both the config from the base class as well as the siblingCopy for the duplication)
		builder.buildField(configRecipeType, "config").setVisibility(VisibilityType.PRIVATE).setFinal(true).finish();

		// Add the constructor
		List<String> ctorCode = new ArrayList<>();
		ctorCode.add("super(engine, " + creatorType.getSimpleName() + ".class, " + isPrimary + ", " + isFallback + ");");
		ctorCode.add("this.config = config;");
		siblingHelper.addCtorCode(ctorCode);

		ConstructorBuilder ctorBuilder = builder.buildConstructor().setVisibility(VisibilityType.PUBLIC).buildParameter(configRecipeType, "config").finish()
				.buildParameter(TypeFactory.createClassType(Engine.class), "engine").finish();
		siblingHelper.buildCtorParameter(ctorBuilder);
		ctorBuilder.addCode(ctorCode.toArray(new String[ctorCode.size()])).finish();
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
