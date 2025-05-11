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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.bean.qualifier.Descriptor;
import tendril.context.Engine;
import tendril.test.AbstractUnitTest;
import tendril.test.bean.SingleCtorBean;

/**
 * Test case for the {@link FactoryRecipe}
 */
public class FactoryRecipeTest extends AbstractUnitTest {
    
    // Mocks to use for testing
    @Mock
    private Engine mockEngine;
    
    // Instance to test
    private FactoryRecipe<SingleCtorBean> recipe;

    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        recipe = new FactoryRecipe<>(mockEngine, SingleCtorBean.class) {

            @Override
            protected void setupDescriptor(Descriptor<SingleCtorBean> descriptor) {
            }

            @Override
            protected SingleCtorBean createInstance(Engine engine) {
                return new SingleCtorBean();
            }
        };
    }

    /**
     * Verify that a new instance is created and always returned
     */
    @Test
    public void testSingletonInstance() {
        SingleCtorBean bean = recipe.get();
        Assertions.assertTrue(bean != recipe.get());
        Assertions.assertTrue(bean != recipe.get());
        Assertions.assertTrue(bean != recipe.get());
        Assertions.assertTrue(bean != recipe.get());
    }
}
