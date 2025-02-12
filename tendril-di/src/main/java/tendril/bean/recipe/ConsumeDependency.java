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
package tendril.bean.recipe;

import tendril.context.Engine;

/**
 * Helper which performs the steps necessary to retrieve the desired dependency instance from the {@link Engine} and apply it to the created consumer.
 * 
 * @param <CONSUMER_TYPE> The type which is to consumer the bean dependency
 * @param <DEPENDENCY_TYPE> The type of dependency that is the bean that is to be consumed
 */
public class ConsumeDependency<CONSUMER_TYPE, DEPENDENCY_TYPE> {
    
    /** Contains the description of the dependency which is to be consumed */
    private final Descriptor<DEPENDENCY_TYPE> descriptor;
    /** Contains the appropriate mechanism for applying the dependency to the consumer */
    private final Applicator<CONSUMER_TYPE, DEPENDENCY_TYPE> applicator;

    /**
     * CTOR
     * 
     * @param descriptor {@link Descriptor} describing what type of bean is to be applied/consumed
     * @param applicator {@link Applicator} containing the mechanism for how to apply the bean to the consumer
     */
    public ConsumeDependency(Descriptor<DEPENDENCY_TYPE> descriptor, Applicator<CONSUMER_TYPE, DEPENDENCY_TYPE> applicator) {
        this.descriptor = descriptor;
        this.applicator = applicator;
    }
    
    /**
     * Consumes the dependency by retrieving it from the engine and applying to the destination consumer.
     * 
     * @param consumer CONSUMER_TYPE which is the destination consumer of the bean
     * @param engine {@link Engine} where the bean is to be retrieved from
     */
    public void consume(CONSUMER_TYPE consumer, Engine engine) {
        applicator.apply(consumer, engine.getBean(descriptor));
    }
}
