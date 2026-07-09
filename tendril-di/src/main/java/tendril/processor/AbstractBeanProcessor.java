package tendril.processor;

import java.lang.annotation.Annotation;

import tendril.annotationprocessor.AbstractDelayedAnnotationTendrilProcessor;
import tendril.annotationprocessor.ClassDefinition;
import tendril.annotationprocessor.exception.InvalidConfigurationException;
import tendril.annotationprocessor.exception.TendrilException;
import tendril.bean.Configuration;
import tendril.codegen.annotation.JAnnotationFactory;
import tendril.codegen.field.type.ClassType;
import tendril.processor.recipe.RecipeGenerator;

/**
 * Abstract processor for the purpose of handling bean classes. The subclass must associate itself with the appropriate annotation, however this abstract class handled all of the appropriate code
 * generation.
 */
public abstract class AbstractBeanProcessor extends AbstractDelayedAnnotationTendrilProcessor {
	/** The class of the registry annotation which is to be applied to the generated recipe */
	private final Class<? extends Annotation> registryAnnotation;

	/**
	 * CTOR
	 * 
	 * @param registryAnnotation {@link Class} indicating the registry annotation to apply to the generated recipe. Leave {@code null} to skip
	 */
	public AbstractBeanProcessor(Class<? extends Annotation> registryAnnotation) {
		this.registryAnnotation = registryAnnotation;
		// Disable JAnnotationFactory logging to keep the output cleaner
		JAnnotationFactory.setLoggingEnabled(false);
	}

	/**
	 * @see tendril.annotationprocessor.AbstractTendrilProccessor#processType()
	 */
	@Override
	protected ClassDefinition processType() throws TendrilException {
		return RecipeGenerator.generate(getClassOverrideType(), currentClassType, currentClass, processingEnv.getMessager(), registryAnnotation);
	}

	/**
	 * Get the override type that is to be applied when advertising the type of the class based bean in the recipe
	 * 
	 * @return {@link ClassType} to apply as override, or {@code null} if no override is to be applied
	 */
	protected ClassType getClassOverrideType() {
		return null;
	}

	/**
	 * @see tendril.annotationprocessor.AbstractTendrilProccessor#processMethod()
	 */
	@Override
	protected ClassDefinition processMethod() throws TendrilException {
		// A separate processor handles configurations, this "merely" generates the recipe for the bean that the method is producing
		if (!currentClass.hasAnnotation(Configuration.class))
			throw new InvalidConfigurationException(currentMethod.getFullElementPath() + "() - Bean methods cannot be outside of a configuration");

		return RecipeGenerator.generate(currentClassType, getMethodOverrideType(), currentMethod, processingEnv.getMessager());
	}

	/**
	 * Get the override type that is to be applied when advertising the type of the method based bean in the recipe
	 * 
	 * @return {@link ClassType} to apply as override, or {@code null} if no override is to be applied
	 */
	protected ClassType getMethodOverrideType() {
		return null;
	}
}
