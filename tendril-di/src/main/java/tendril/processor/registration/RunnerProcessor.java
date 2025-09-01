/*
 * Copyright 2024 Jaroslav Bosak
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
package tendril.processor.registration;

import java.util.ArrayList;
import java.util.List;

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
import tendril.context.launch.Runner;
import tendril.context.launch.TendrilRunner;
import tendril.processor.recipe.RecipeGenerator;

/**
 * Annotation processor for the {@link Runner} annotation
 */
@SupportedAnnotationTypes("tendril.context.launch.Runner")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
@AutoService(Processor.class)
public class RunnerProcessor extends AbstractDelayedAnnotationTendrilProcessor {

    /** The class that is the recipe for the runner */
    private List<String> runners = new ArrayList<>();
    
    /**
     * CTOR
     */
    public RunnerProcessor() {
    }
    
    /**
     * @see tendril.annotationprocessor.AbstractTendrilProccessor#validateType(javax.lang.model.element.TypeElement)
     * 
     * @throws TendrilException if the annotated type does not implement the {@link TendrilRunner} interface
     */
    @Override
    protected void validateType(TypeElement type) throws TendrilException {
        if (!isTypeOf(type, TendrilRunner.class))
            throw new InvalidConfigurationException(type.getQualifiedName() + " must implement the " + TendrilRunner.class.getSimpleName() + " interface");
    }

    /**
     * @see tendril.processor.BeanProcessor#processType()
     * 
     * @throws TendrilException if multiple @{@link Runner}s were defined
     */
    @Override
    protected ClassDefinition processType() throws TendrilException {
        ClassDefinition generatedDef = RecipeGenerator.generate(currentClassType, currentClass, processingEnv.getMessager(), false);
        runners.add(generatedDef.getType().getFullyQualifiedName());
        return generatedDef;
    }

    /**
     * @see tendril.processor.BeanProcessor#processMethod()
     * @throws TendrilException if the annotation is applied to a method
     */
    @Override
    protected ClassDefinition processMethod() throws TendrilException {
        throw new InvalidConfigurationException(currentClassType.getFullyQualifiedName() + "::" + currentMethod.getName() +
                " - Runner cannot be a method");
    }

    /**
     * @see tendril.annotationprocessor.AbstractTendrilProccessor#processingOver()
     */
    @Override
    protected void processingOver() {
        super.processingOver();
        
        // Nothing to be done if no runner was found
        if (runners == null)
            return;
        writeResourceFile(RunnerFile.PATH, runners);
    }
}
