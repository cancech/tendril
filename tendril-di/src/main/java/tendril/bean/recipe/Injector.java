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
 * Functional interface for injectors which retrieve beans from the DI {@link Engine} and apply them to the destination consumer
 * 
 * @param <BEAN_TYPE> The type into which the dependency is to be injected
 */
public interface Injector<BEAN_TYPE> {

    /**
     * Inject the dependency by retrieving it from the engine and applying to the destination consumer.
     * 
     * @param consumer CONSUMER_TYPE which is the destination consumer of the bean
     * @param engine {@link Engine} where the bean is to be retrieved from
     */
    void inject(BEAN_TYPE consumer, Engine engine);
}
