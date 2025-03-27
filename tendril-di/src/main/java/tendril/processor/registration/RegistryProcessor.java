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

import com.google.auto.service.AutoService;

import tendril.annotationprocessor.AbstractTendrilProccessor;
import tendril.annotationprocessor.ClassDefinition;
import tendril.annotationprocessor.exception.ProcessingException;
import tendril.bean.recipe.Registry;

/**
 * Annotation processor for recipes which are annotated with @{@link Registry} and are to be added to the recipe registry
 */
@SupportedAnnotationTypes("tendril.bean.recipe.Registry")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
@AutoService(Processor.class)
public class RegistryProcessor extends AbstractTendrilProccessor {
    /** List of all recipes that are to be registered */
    private final List<String> registers = new ArrayList<>();

    /**
     * CTOR
     */
    public RegistryProcessor() {
    }

    /**
     * @see tendril.annotationprocessor.AbstractTendrilProccessor#processType()
     */
    @Override
    protected ClassDefinition processType() {
        registers.add(currentClassType.getFullyQualifiedName());
        return null;
    }

    /**
     * @see tendril.annotationprocessor.AbstractTendrilProccessor#processMethod()
     */
    @Override
    protected ClassDefinition processMethod() {
        throw new ProcessingException(currentClassType.getFullyQualifiedName() + "::" + currentMethod.getName() +
                " - Registry cannot be a method");
    }

    /**
     * @see tendril.annotationprocessor.AbstractTendrilProccessor#errorRaised()
     */
    @Override
    protected void errorRaised() {
        processingOver();
    }

    /**
     * @see tendril.annotationprocessor.AbstractTendrilProccessor#processingOver()
     */
    @Override
    protected void processingOver() {
        writeResourceFile(RegistryFile.PATH, registers);
    }
}
