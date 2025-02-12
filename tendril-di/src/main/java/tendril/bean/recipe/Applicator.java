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

/**
 * Functional interface which is intended to apply a bean to the indicated consumer.
 * 
 * @param <CONSUMER> the type which is to consume the bean
 * @param <BEAN> the type which is to be applied/consumed
 */
public interface Applicator<CONSUMER, BEAN> {
    
    /**
     * Apply the bean to the indicated consumer in whatever method is appropriate.
     * 
     * @param consumer CONSUMER to which to apply the bean
     * @param bean BEAN that is to be applied
     */
    void apply(CONSUMER consumer, BEAN bean);

}
