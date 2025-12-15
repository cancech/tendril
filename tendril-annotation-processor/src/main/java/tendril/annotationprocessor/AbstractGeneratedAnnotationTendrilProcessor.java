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
    
    /**
     * CTOR
     */
    public AbstractGeneratedAnnotationTendrilProcessor() {
    }

    /**
     * The generated annotation is tagged with a non-generated annotation. Search for the non-generated annotation, and when found repeat the search with
     * the generated one.
     * 
     * @see tendril.annotationprocessor.AbstractTendrilProccessor#findAndProcessElements(javax.lang.model.element.TypeElement)
     */
    @Override
    protected void findAndProcessElements(TypeElement annotation) {
    	// Track the original round
        final RoundEnvironment original = roundEnv;
        
        // Find who is using the generated annotation
        findAndProcessElements(annotation, customAnnon -> {
            EnvironmentCollector.getAllEnvironments(roundEnv).forEach(e -> {
                roundEnv = e;
                super.findAndProcessElements((TypeElement) customAnnon);   
            });
        });
        
        // Reset the original round
        roundEnv = original;
    }
}
