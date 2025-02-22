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
 * @param <BEAN_TYPE> The type into which the dependency is to be injected
 * @param <DEPENDENCY_TYPE> The type of dependency that the bean is to be injected with
 */
public class InjectDependency<BEAN_TYPE, DEPENDENCY_TYPE> implements Injector<BEAN_TYPE> {
    
    /** Contains the description of the dependency which is to be consumed */
    private final Descriptor<DEPENDENCY_TYPE> descriptor;
    /** Contains the appropriate mechanism for applying the dependency to the consumer */
    private final Applicator<BEAN_TYPE, DEPENDENCY_TYPE> applicator;

    /**
     * CTOR
     * 
     * @param descriptor {@link Descriptor} describing what type of bean is to be injected/consumed
     * @param applicator {@link Applicator} containing the mechanism for how to apply the bean to the consumer
     */
    public InjectDependency(Descriptor<DEPENDENCY_TYPE> descriptor, Applicator<BEAN_TYPE, DEPENDENCY_TYPE> applicator) {
        this.descriptor = descriptor;
        this.applicator = applicator;
    }
    
    /**
     * @see tendril.bean.recipe.Injector#inject(java.lang.Object, tendril.context.Engine)
     */
    @Override
    public void inject(BEAN_TYPE consumer, Engine engine) {
        applicator.apply(consumer, engine.getBean(descriptor));
    }
}
