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

import javax.annotation.processing.Processor;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;

import com.google.auto.service.AutoService;

import tendril.annotationprocessor.AbstractDelayedAnnotationTendrilProcessor;
import tendril.annotationprocessor.ClassDefinition;
import tendril.annotationprocessor.exception.InvalidConfigurationException;
import tendril.annotationprocessor.exception.TendrilException;
import tendril.bean.Configuration;
import tendril.processor.recipe.RecipeGenerator;

/**
 * Processor for the {@link Configuration} annotation, which will generate an appropriate recipe for each of the beans the annotated configuration produces.
 */
@SupportedAnnotationTypes("tendril.bean.Configuration")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
@AutoService(Processor.class)
public class ConfigurationProcessor extends AbstractDelayedAnnotationTendrilProcessor {

    /**
     * CTOR
     */
    public ConfigurationProcessor() {
    }
    
    /**
     * @see tendril.annotationprocessor.AbstractTendrilProccessor#processType()
     */
    @Override
    protected ClassDefinition processType() throws TendrilException {
        return RecipeGenerator.generateConfiguration(currentClassType, currentClass, processingEnv.getMessager());
    }

    /**
     * @see tendril.annotationprocessor.AbstractTendrilProccessor#processMethod()
     */
    @Override
    protected ClassDefinition processMethod() throws TendrilException {
        throw new InvalidConfigurationException(currentClassType.getFullyQualifiedName() + "::" + currentMethod.getName() +
                " - Configuration cannot be a method");
    }
}
