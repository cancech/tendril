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
package tendril.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import javax.annotation.processing.Processor;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import com.google.auto.service.AutoService;

import tendril.annotationprocessor.AbstractGeneratedAnnotationTendrilProcessor;
import tendril.annotationprocessor.AnnotationGeneratedListener;
import tendril.annotationprocessor.ClassDefinition;
import tendril.annotationprocessor.GeneratedAnnotationLoader;
import tendril.annotationprocessor.element.ElementLoader;
import tendril.annotationprocessor.exception.InvalidConfigurationException;
import tendril.annotationprocessor.exception.MissingAnnotationException;
import tendril.annotationprocessor.exception.TendrilException;
import tendril.bean.Bean;
import tendril.bean.Configuration;
import tendril.bean.duplicate.GeneratedBlueprint;
import tendril.codegen.classes.JClass;
import tendril.codegen.classes.method.JMethod;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.TypeFactory;
import tendril.codegen.generics.GenericType;
import tendril.processor.recipe.RecipeGenerator;

/**
 * Processor for the {@link GeneratedBlueprint} annotation, which will generate an appropriate recipe for each of the duplicates that the blueprint aims to produce.
 * Registers with the {@link ElementLoader} to notify when it generates annotations, allowing for other annotation processors to resume their operations, if they are
 * stalled waiting for a blueprint annotation to be generated. 
 */
@SupportedAnnotationTypes("tendril.bean.duplicate.GeneratedBlueprint")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
@AutoService(Processor.class)
public class GeneratedBlueprintProcessor extends AbstractGeneratedAnnotationTendrilProcessor implements GeneratedAnnotationLoader {
	/** Cache of the blueprint annotations that have been processed, removing the need to determine the associated blueprint Enum each time */
	private static final Map<ClassType, ClassType> blueprintCache = new HashMap<>();
	/** Listeners to be notified when an annotation is generated */
    private final List<AnnotationGeneratedListener> listeners = new ArrayList<>();
    /** Set of all annotations that have been generated. Used as a way of tracking which generated annotations have already notified */
    private final Set<ClassType> announcedGeneratedAnnotations = new HashSet<>();
	
    /** The generated annotation that is currently being processed */
    private ClassType annotationType;
	/** The type of the blueprint Enum that is currently being processed */
	private ClassType currentBlueprintType = null;
	/** Exception received when determining the Enum blueprint type. As the method doing that work cannot throw an exception, this is kept until later when it can be thrown */
	private Exception annotationRetrieveException = null;

	/**
	 * CTOR
	 */
	public GeneratedBlueprintProcessor() {
		// Prevent the processing of the GeneratedBlueprint
		blueprintCache.put(TypeFactory.createClassType(GeneratedBlueprint.class), null);
		// Register this processor as an annotation generator
        ElementLoader.getGeneratedAnnotationHandler().registerLoader(this);
	}

	/**
	 * @see tendril.annotationprocessor.GeneratedAnnotationLoader#addListener(tendril.annotationprocessor.AnnotationGeneratedListener)
	 */
	@Override
	public void addListener(AnnotationGeneratedListener listener) {
		listeners.add(listener);
	}
	
	/**
	 * Announce that an annotation has been generated
	 */
	private void announceGeneratedAnnotation() {
		// Make sure that it's not one that's been announced previously
		if (announcedGeneratedAnnotations.contains(annotationType))
			return;
		
		// Track the announcement and announce the new annotation
		announcedGeneratedAnnotations.add(annotationType);
		listeners.forEach(listener -> listener.annotationGenerated(annotationType));
	}
	
	/**
	 * Prior to performing the processing, determine what the blueprint Enum is as it will apply to all types from this iteration of processing.
	 *  
	 * @see tendril.annotationprocessor.AbstractTendrilProccessor#findAndProcessElements(javax.lang.model.element.TypeElement, java.util.function.Consumer)
	 */
	@Override
	public void findAndProcessElements(TypeElement annotation, Consumer<? super Element> consume) {
		try {
			currentBlueprintType = retrieveBlueprint(annotation);
			announceGeneratedAnnotation();
		} catch (Exception e) {
			annotationRetrieveException = e;
		}
		super.findAndProcessElements(annotation, consume);
	}
	
	/**
	 * Ensure that the type is not also annotated as a bean. Thrown an exception caused by conflicting definitions if true.
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
	 * Helper which throws an exception indicating that the generated blueprint annotation is applied along side @Bean
	 *  
	 * @param typeName {@link String} the name of the type which is triggering the exception
	 * @throws TendrilException indicating which type has the @Bean annotation applied
	 */
	private void throwBeanAnnotationPresentException(String typeName) throws TendrilException {
		throw new TendrilException("Unable to process " + typeName + ", it has both @Bean and @" + currentAnnotation.getSimpleName() + " annotations applied. Only one of these can be applied to a bean.");
	}
	
	/**
	 * Ensure that the type is not also annotated as a bean. Thrown an exception caused by conflicting definitions if true.
	 * 
	 * @see tendril.annotationprocessor.AbstractTendrilProccessor#validateMethod()
	 */
	@Override
	protected void validateMethod() throws TendrilException {
		super.validateMethod();
		if (currentMethod.hasAnnotation(Bean.class))
			throwBeanAnnotationPresentException(currentClassType.getFullyQualifiedName() + "::" + currentMethod.getName() + "()");
	}

	/**
	 * @see tendril.annotationprocessor.AbstractTendrilProccessor#processType()
	 */
	@Override
	protected ClassDefinition processType() throws TendrilException {
		if (annotationRetrieveException != null)
			throw new TendrilException("Unable to process " + currentClassType.getFullyQualifiedName() + " - unable to retreive the blueprint annotation", annotationRetrieveException);
		
		try {
			writeCode(RecipeGenerator.generateDuplicateSiblingBean(currentBlueprintType, currentClassType, currentClass, processingEnv.getMessager()));
			return RecipeGenerator.generateDuplicateBean(currentBlueprintType, currentClassType, currentClass, processingEnv.getMessager());
		} catch (Exception e) {
			throw new TendrilException("Unable to process " + currentClassType.getFullyQualifiedName(), e);
		}
	}

	/**
	 * Throws {@link InvalidConfigurationException} as the annotation cannot be applied to a method
	 */
	@Override
	protected ClassDefinition processMethod() throws TendrilException {
		if (annotationRetrieveException != null)
			throw new TendrilException("Unable to process " + currentClassType.getFullyQualifiedName() + " - unable to retreive the blueprint annotation", annotationRetrieveException);

        // A separate processor handles configurations, this "merely" generates the recipe for the bean that the method is producing
        if (!currentClass.hasAnnotation(Configuration.class))
            throw new InvalidConfigurationException(currentClassType.getFullyQualifiedName() + "::" + currentMethod.getName() + 
                    "() - Blueprint methods cannot be outside of a configuration");

		return RecipeGenerator.generateDuplicateSiblingBean(currentBlueprintType, currentClassType, currentMethod, processingEnv.getMessager());
	}

	/**
	 * Retrieve the blueprint enum which is driving the duplication process
	 * 
	 * @param annotation {@link TypeElement} of the annotation being processed
	 * @return {@link ClassType} of the blueprint annotation
	 * @throws MissingAnnotationException
	 * @throws TendrilException
	 * @throws ClassNotFoundException
	 */
	private ClassType retrieveBlueprint(TypeElement annotation) throws MissingAnnotationException, TendrilException, ClassNotFoundException {
		JClass annotationClass = ElementLoader.retrieveClass(annotation);
		annotationType = annotationClass.getType();
		if (blueprintCache.containsKey(annotationType))
			return blueprintCache.get(annotationType);

		// Make sure that the enumClass method is present
		List<JMethod<?>> methods = annotationClass.getMethods("enumClass");
		if (methods.isEmpty())
			throw new TendrilException(createExceptionMessage(annotationClass, "does not have the required enumClass() method"));
		else if (methods.size() > 1)
			throw new TendrilException(createExceptionMessage(annotationClass, "must have exactly one enumClass() method"));

		// Get the type of enumeration that is employed for the blueprint
		String exceptionMsg = createExceptionMessage(annotationClass, "enumClass() method must return a Class<? extends Enum<?>>");
		if (methods.get(0).getType() instanceof ClassType type && Class.class.getName().equals(type.getFullyQualifiedName())) {
			List<GenericType> generics = type.getGenerics();
			if (generics.size() != 1)
				throw new TendrilException(exceptionMsg);

			ClassType blueprintType = generics.get(0).asClassType();
			blueprintCache.put(annotationType, blueprintType);
			return blueprintType;
		} else
			throw new TendrilException(exceptionMsg);
	}
	
	/**
	 * Get the blueprint type (enum) that is associated with a blueprint annotation.
	 * 
	 * @param annotationType {@link ClassType} of the blueprint annotation
	 * @return {@link ClassType} of the blueprint enum
	 * @throws TendrilException if an unknown annotation type is provided
	 */
	public static ClassType getBlueprintForAnnotation(ClassType annotationType) throws TendrilException {
		ClassType blueprintType = blueprintCache.get(annotationType);
		if (blueprintType == null)
			throw new TendrilException("Unknown Blueprint annotation " + annotationType);
		return blueprintType;
	}

	/**
	 * Helper to generate the full exception message
	 * 
	 * @param annotationClass {@link JClass} of the annotation which is being processed
	 * @param reason          {@link String} why the exception is bring thrown
	 * @return {@link String} the complete exception text
	 */
	private static String createExceptionMessage(JClass annotationClass, String reason) {
		return "The generated annotation " + annotationClass.getName() + " " + reason;
	}
}
