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
     * @see tendril.processor.AbstractTendrilProccessor#findAndProcessElements(javax.lang.model.element.TypeElement)
     */
    @Override
    protected void findAndProcessElements(TypeElement annotation) {
        findAndProcessElements(annotation, customAnnon -> {
            EnvironmentCollector.getAllEnvironments(roundEnv).forEach(e -> super.findAndProcessElements((TypeElement) customAnnon, defaultConsumer()));
        });
    }
}
