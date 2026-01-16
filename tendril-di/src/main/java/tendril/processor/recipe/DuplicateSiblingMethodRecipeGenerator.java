package tendril.processor.recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.Messager;

import tendril.annotationprocessor.exception.InvalidConfigurationException;
import tendril.annotationprocessor.exception.TendrilException;
import tendril.bean.Configuration;
import tendril.bean.duplicate.Sibling;
import tendril.codegen.JBase;
import tendril.codegen.VisibilityType;
import tendril.codegen.classes.ClassBuilder;
import tendril.codegen.classes.JParameter;
import tendril.codegen.classes.method.JMethod;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.TypeFactory;
import tendril.codegen.generics.GenericFactory;
import tendril.context.Engine;
import tendril.processor.BlueprintProcessor;
import tendril.util.TendrilStringUtil;

/**
 * Generates the recipe code for sibling beans which are produced within a {@link Configuration}, ergo the recipe which makes the specific copies tied to bean to be duplicated
 */
public class DuplicateSiblingMethodRecipeGenerator extends MethodRecipeGenerator {
	/** The type of the enum which drives the duplication */
	private final ClassType blueprintType;

	/**
	 * CTOR
	 * 
     * @param configType {@link ClassType} indicating the configuration class
     * @param beanType {@link ClassType} of the bean which is to be produced
     * @param beanCreator {@link JMethod} which is to produce the bean
     * @param messager {@link Messager} that is used by the annotation processor
	 * @param blueprintType {@link ClassType} of the enum which drives the duplication
	 */
	DuplicateSiblingMethodRecipeGenerator(ClassType configType, ClassType beanType, JMethod<?> beanCreator, Messager messager, ClassType blueprintType) {
		super(configType, beanType, beanCreator, messager);
		this.blueprintType = blueprintType;
	}
	
	/**
	 * @see tendril.processor.recipe.ClassRecipeGenerator#generateConstructor(tendril.codegen.classes.ClassBuilder)
	 */
	@Override
    protected void generateConstructor(ClassType configRecipeType, ClassBuilder builder) throws InvalidConfigurationException {
        // Instance fields for the config (need both the config from the base class as well as the siblingCopy for the duplication)
        builder.buildField(configRecipeType, "config").setVisibility(VisibilityType.PRIVATE).setFinal(true).finish();
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
        
        // Add the constructor
        builder.buildConstructor().setVisibility(VisibilityType.PUBLIC)
            .buildParameter(configRecipeType, "config").finish()
            .buildParameter(TypeFactory.createClassType(Engine.class), "engine").finish()
            .buildParameter(blueprintType, "siblingCopy").finish()
            .addCode("super(engine, " + creatorType.getSimpleName() + ".class, " + isPrimary + ", " + isFallback + ");",
                     "this.config = config;",
                     "this.siblingCopy = siblingCopy;",
                     "getDescription().addQualifier(copyQualifiers.get(this.siblingCopy.name()));")
            .finish();
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
