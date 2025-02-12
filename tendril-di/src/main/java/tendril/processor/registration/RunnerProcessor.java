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

import javax.annotation.processing.Processor;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

import com.google.auto.service.AutoService;

import tendril.annotationprocessor.ClassDefinition;
import tendril.codegen.classes.method.JMethod;
import tendril.codegen.field.type.ClassType;
import tendril.context.launch.Runner;
import tendril.context.launch.TendrilRunner;
import tendril.processor.ProviderProcessor;

/**
 * Annotation processor for the {@link Runner} annotation
 */
@SupportedAnnotationTypes("tendril.context.launch.Runner")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
@AutoService(Processor.class)
public class RunnerProcessor extends ProviderProcessor {

    /** The class that is the recipe for the runner */
    private String mainRunner = null;
    
    /**
     * CTOR
     */
    public RunnerProcessor() {
        super(false);
    }
    
    /**
     * @see tendril.annotationprocessor.AbstractTendrilProccessor#validateType(javax.lang.model.element.TypeElement)
     */
    @Override
    protected void validateType(TypeElement type) {
        if (!isTypeOf(type, TendrilRunner.class))
            throw new RuntimeException(type.getQualifiedName() + " must implement the TendrilRunner interface");
    }

    /**
     * @see tendril.processor.ProviderProcessor#processType(tendril.codegen.field.type.ClassType)
     */
    @Override
    protected ClassDefinition processType(ClassType data) {
        if (mainRunner != null)
            throw new RuntimeException("There can only be a single runner specified");
        
        ClassDefinition generatedDef = super.processType(data);
        mainRunner = generatedDef.getType().getFullyQualifiedName();
        return generatedDef;
    }

    /**
     * @see tendril.processor.ProviderProcessor#processMethod(tendril.codegen.field.type.ClassType, tendril.codegen.classes.method.JMethod)
     */
    @Override
    protected ClassDefinition processMethod(ClassType classData, JMethod<?> methodData) {
        throw new RuntimeException("Runner cannot be a method");
    }

    /**
     * @see tendril.annotationprocessor.AbstractTendrilProccessor#processingOver()
     */
    @Override
    protected void processingOver() {
        writeResourceFile(RunnerFile.PATH, mainRunner);
    }
}
