package tendril.processor;

import javax.annotation.processing.Processor;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

import com.google.auto.service.AutoService;

import tendril.annotationprocessor.AbstractDelayedAnnotationTendrilProcessor;
import tendril.annotationprocessor.ClassDefinition;
import tendril.annotationprocessor.exception.InvalidConfigurationException;
import tendril.annotationprocessor.exception.TendrilException;
import tendril.bean.Bean;
import tendril.bean.Configuration;
import tendril.bean.duplicate.Duplicate;
import tendril.bean.recipe.Registry;
import tendril.codegen.field.type.ClassType;
import tendril.processor.recipe.RecipeGenerator;

/**
 * Processor for the {@link Duplicate} annotated beans. This will generate the appropriate recipes for the creation of the sibling recipe (i.e.: the recipe which creates a concrete duplicate of a
 * bean) as well as the recipe triggering duplication when applied to a bean class. When applied to a {@link Configuration} method, the {@link ConfigurationProcessor} is responsible for generating the
 * recipe triggering the duplication from the method.
 */
@SupportedAnnotationTypes("tendril.bean.duplicate.Duplicate")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
@AutoService(Processor.class)
public class DuplicateProcessor extends AbstractDelayedAnnotationTendrilProcessor {

	/**
	 * Ensure that the type is not also annotated as a {@link Bean}. Thrown an exception caused by conflicting definitions if true.
	 * 
	 * @see tendril.annotationprocessor.AbstractTendrilProccessor#validateType(javax.lang.model.element.TypeElement)
	 */
	@Override
	protected void validateType(TypeElement type) throws TendrilException {
		super.validateType(type);
		if (type.getAnnotationsByType(Bean.class).length != 0)
			throwBeanAnnotationPresentException(type.getQualifiedName().toString());
	}
	
	/**
	 * Ensure that the type is not also annotated as a {@link Bean}. Thrown an exception caused by conflicting definitions if true.
	 * 
	 * @see tendril.annotationprocessor.AbstractTendrilProccessor#validateMethod()
	 */
	@Override
	protected void validateMethod() throws TendrilException {
		super.validateMethod();
		if (currentMethod.hasAnnotation(Bean.class))
			throwBeanAnnotationPresentException(currentMethod.getFullElementPath());
	}

	/**
	 * Helper which throws an exception indicating that the generated blueprint annotation is applied along side @Bean
	 * 
	 * @param typeName {@link String} the name of the type which is triggering the exception
	 * @throws TendrilException indicating which type has the @Bean annotation applied
	 */
	private void throwBeanAnnotationPresentException(String typeName) throws TendrilException {
		throw new TendrilException(
				"Unable to process " + typeName + ", it has both @Bean and @" + currentAnnotation.getSimpleName() + " annotations applied. Only one of these can be applied to a bean.");
	}
	
	/**
	 * @see tendril.annotationprocessor.AbstractTendrilProccessor#processType()
	 */
	@Override
	protected ClassDefinition processType() throws TendrilException {
		try {
			// Determine the type of blueprint
			ClassType blueprint = AnnotationHelper.retrieveDuplicateBlueprint(currentClass);
			if (blueprint == null)
				throw new TendrilException("Unable to retrieve bluerprint class type");

			// Generate the code
			writeCode(RecipeGenerator.generateDuplicateSiblingBean(blueprint, currentClassType, currentClass, processingEnv.getMessager()));
			return RecipeGenerator.generateDuplicateBean(blueprint, currentClassType, currentClass, processingEnv.getMessager(), Registry.class);
		} catch (Exception e) {
			throw new TendrilException("Unable to process " + currentClassType.getFullyQualifiedName(), e);
		}
	}

	/**
	 * @see tendril.annotationprocessor.AbstractTendrilProccessor#processMethod()
	 */
	@Override
	protected ClassDefinition processMethod() throws TendrilException {
		// A separate processor handles configurations, this "merely" generates the recipe for the bean that the method is producing
		if (!currentClass.hasAnnotation(Configuration.class))
			throw new InvalidConfigurationException(currentMethod.getFullElementPath() + " - Blueprint methods cannot be outside of a configuration");

		return RecipeGenerator.generateDuplicateSiblingBean(AnnotationHelper.retrieveDuplicateBlueprint(currentMethod), currentClassType, currentMethod, processingEnv.getMessager());
	}

}
