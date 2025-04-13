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
package tendril.processor;

import javax.annotation.processing.Processor;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;

import com.google.auto.service.AutoService;

import tendril.annotationprocessor.AbstractDelayedAnnotationTendrilProcessor;
import tendril.annotationprocessor.ClassDefinition;
import tendril.annotationprocessor.exception.InvalidConfigurationException;
import tendril.annotationprocessor.exception.TendrilException;
import tendril.bean.Bean;
import tendril.bean.Configuration;
import tendril.bean.recipe.Registry;
import tendril.processor.recipe.RecipeGenerator;

/**
 * Processor for the {@link Bean} annotation, which will generate the appropriate Recipe for the specified Provider
 */
@SupportedAnnotationTypes("tendril.bean.Bean")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
@AutoService(Processor.class)
public class BeanProcessor extends AbstractDelayedAnnotationTendrilProcessor {


    /**
     * CTOR - will be annotated as a {@link Registry}
     */
    public BeanProcessor() {
    }
    
    /**
     * @see tendril.annotationprocessor.AbstractTendrilProccessor#processType()
     */
    @Override
    protected ClassDefinition processType() throws TendrilException {
        return RecipeGenerator.generate(currentClassType, currentClass, processingEnv.getMessager());
    }

    /**
     * @see tendril.annotationprocessor.AbstractTendrilProccessor#processMethod()
     */
    @Override
    protected ClassDefinition processMethod() throws TendrilException {
        // A separate processor handles configurations, this "merely" generates the recipe for the bean that the method is producing
        if (!currentClass.hasAnnotation(Configuration.class))
            throw new InvalidConfigurationException(currentClassType.getFullyQualifiedName() + "::" + currentMethod.getName() + 
                    "() - Bean methods cannot be outside of a configuration");

        return RecipeGenerator.generate(currentClassType, currentMethod, processingEnv.getMessager());
    }
}
