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
package tendril.annotationprocessor;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;

/**
 * Processor, which is used for the purpose of processing generated annotations. When processing, then entire environment (including previous rounds) is processed, rather than just the environment of
 * the current round.
 */
public abstract class AbstractGeneratedAnnotationTendrilProcessor extends AbstractTendrilProccessor {
    /** Flag for whether this is the first time the processor is called upon to process something */
    private boolean isFirst = true;
    
    /**
     * CTOR
     */
    public AbstractGeneratedAnnotationTendrilProcessor() {
    }

    /**
     * @see tendril.annotationprocessor.AbstractTendrilProccessor#findAndProcessElements(javax.lang.model.element.TypeElement)
     */
    @Override
    protected void findAndProcessElements(TypeElement annotation) {
        // After the first, can perform processing as-per normal
        if (!isFirst) {
            super.findAndProcessElements(annotation);
            return;
        }
            
        // Only the first iteration will need to go back and double-check previous rounds (as those will have been skipped)
        isFirst = false;
        final RoundEnvironment original = roundEnv;
        findAndProcessElements(annotation, customAnnon -> {
            EnvironmentCollector.getAllEnvironments(roundEnv).forEach(e -> {
                roundEnv = e;
                super.findAndProcessElements((TypeElement) customAnnon);   
            });
            roundEnv = original;
        });
    }
}
