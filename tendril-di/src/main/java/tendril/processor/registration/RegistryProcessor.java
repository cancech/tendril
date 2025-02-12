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

import tendril.bean.recipe.Registry;
import tendril.codegen.classes.method.JMethod;
import tendril.codegen.field.type.ClassType;
import tendril.processor.AbstractTendrilProccessor;
import tendril.processor.ClassDefinition;

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
     * @see tendril.processor.AbstractTendrilProccessor#processType(tendril.codegen.field.type.ClassType)
     */
    @Override
    protected ClassDefinition processType(ClassType data) {
        registers.add(data.getFullyQualifiedName());
        return null;
    }

    /**
     * @see tendril.processor.AbstractTendrilProccessor#processMethod(tendril.codegen.field.type.ClassType, tendril.codegen.classes.method.JMethod)
     */
    @Override
    protected ClassDefinition processMethod(ClassType classData, JMethod<?> methodData) {
        registers.add(classData.getFullyQualifiedName() + ":" + methodData.getName());
        return null;
    }

    /**
     * @see tendril.processor.AbstractTendrilProccessor#errorRaised()
     */
    @Override
    protected void errorRaised() {
        processingOver();
    }

    /**
     * @see tendril.processor.AbstractTendrilProccessor#processingOver()
     */
    @Override
    protected void processingOver() {
        writeResourceFile(RegistryFile.PATH, registers);
    }
}
