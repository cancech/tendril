package tendril.processor;

import java.lang.annotation.Annotation;

import tendril.annotationprocessor.AbstractDelayedAnnotationTendrilProcessor;
import tendril.annotationprocessor.ClassDefinition;
import tendril.annotationprocessor.exception.InvalidConfigurationException;
import tendril.annotationprocessor.exception.TendrilException;
import tendril.bean.Configuration;
import tendril.codegen.annotation.JAnnotationFactory;
import tendril.processor.recipe.RecipeGenerator;

/**
 * Abstract processor for the purpose of handling bean classes. The subclass must associate itself with the appropriate annotation, however this abstract class handled all of the appropriate code
 * generation.
 */
abstract class AbstactBeanProcessor extends AbstractDelayedAnnotationTendrilProcessor {
	/** The class of the registry annotation which is to be applied to the generated recipe */
	private final Class<? extends Annotation> registryAnnotation;

	/**
	 * CTOR
	 */
	AbstactBeanProcessor(Class<? extends Annotation> registryAnnotation) {
		this.registryAnnotation = registryAnnotation;
		// Disable JAnnotationFactory logging to keep the output cleaner
		JAnnotationFactory.setLoggingEnabled(false);
	}

	/**
	 * @see tendril.annotationprocessor.AbstractTendrilProccessor#processType()
	 */
	@Override
	protected ClassDefinition processType() throws TendrilException {
		return RecipeGenerator.generate(currentClassType, currentClass, processingEnv.getMessager(), registryAnnotation);
	}

	/**
	 * @see tendril.annotationprocessor.AbstractTendrilProccessor#processMethod()
	 */
	@Override
	protected ClassDefinition processMethod() throws TendrilException {
		// A separate processor handles configurations, this "merely" generates the recipe for the bean that the method is producing
		if (!currentClass.hasAnnotation(Configuration.class))
			throw new InvalidConfigurationException(currentMethod.getFullElementPath() + "() - Bean methods cannot be outside of a configuration");

		return RecipeGenerator.generate(currentClassType, currentMethod, processingEnv.getMessager());
	}
}
