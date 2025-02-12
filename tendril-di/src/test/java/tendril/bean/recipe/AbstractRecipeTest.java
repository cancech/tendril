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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.commons.lang3.NotImplementedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.context.Engine;
import tendril.test.AbstractUnitTest;
import tendril.test.bean.HiddenCtorBean;
import tendril.test.bean.MultipleCtorBean;
import tendril.test.bean.SingleCtorBean;

/**
 * Test case for the {@link AbstractRecipe}
 */
public class AbstractRecipeTest extends AbstractUnitTest {
    
    /**
     * Concrete implementation of the {@link AbstractRecipe} to use for testing
     * 
     * @param <BEAN_TYPE> the type of bean that the recipe is to produce
     */
    private class TestRecipe<BEAN_TYPE> extends AbstractRecipe<BEAN_TYPE> {

        protected TestRecipe(Class<BEAN_TYPE> beanClass) {
            super(mockEngine, beanClass);
        }

        @Override
        public BEAN_TYPE get() {
            throw new NotImplementedException("Not required for testing");
        }
        
    }
    
    // Mocks to use for testing
    @Mock
    private Engine mockEngine;
    @Mock
    private Descriptor<String> mockStringDescriptor;
    @Mock
    private Applicator<SingleCtorBean, String> mockStringApplicator;
    @Mock
    private Descriptor<Integer> mockIntDescriptor;
    @Mock
    private Applicator<SingleCtorBean, Integer> mockIntApplicator;
    @Mock
    private Descriptor<Double> mockDoubleDescriptor;
    @Mock
    private Applicator<SingleCtorBean, Double> mockDoubleApplicator;
    
    // Instance to test
    private AbstractRecipe<SingleCtorBean> recipe;

    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        recipe = new TestRecipe<>(SingleCtorBean.class);
        Assertions.assertEquals(SingleCtorBean.class, recipe.getBeanClass());
    }
    
    /**
     * Verify that attempting to create a recipe without the appropriate constructors fails
     */
    @Test
    public void testInvalidCtors() {
        Assertions.assertThrows(RuntimeException.class, () -> new TestRecipe<>(Runnable.class).buildBean());
        Assertions.assertThrows(RuntimeException.class, () -> new TestRecipe<>(MultipleCtorBean.class).buildBean());
        Assertions.assertThrows(RuntimeException.class, () -> new TestRecipe<>(HiddenCtorBean.class).buildBean());
    }
    
    /**
     * Verify that the bean can be built if no dependencies are specified
     */
    @Test
    public void testBuildNoDependencies() {
        Assertions.assertNotNull(recipe.buildBean());
    }
    
    /**
     * Verify that the bean can be built if one dependency is specified
     */
    @Test
    public void testBuildSingleDependencies() {
        recipe.registerDependency(mockStringDescriptor, mockStringApplicator);
        verifyAllChecked();
        
        when(mockEngine.getBean(mockStringDescriptor)).thenReturn("abc123");
        SingleCtorBean instance = recipe.buildBean();
        Assertions.assertNotNull(instance);
        verify(mockEngine).getBean(mockStringDescriptor);
        verify(mockStringApplicator).apply(instance, "abc123");
    }
    
    /**
     * Verify that the bean can be built if multiples dependencies are specified
     */
    @Test
    public void testBuildMultipleDependencies() {
        recipe.registerDependency(mockStringDescriptor, mockStringApplicator);
        recipe.registerDependency(mockIntDescriptor, mockIntApplicator);
        recipe.registerDependency(mockDoubleDescriptor, mockDoubleApplicator);
        verifyAllChecked();

        when(mockEngine.getBean(mockStringDescriptor)).thenReturn("abc123");
        when(mockEngine.getBean(mockIntDescriptor)).thenReturn(123);
        when(mockEngine.getBean(mockDoubleDescriptor)).thenReturn(1.23);
        SingleCtorBean instance = recipe.buildBean();
        Assertions.assertNotNull(instance);
        verify(mockEngine).getBean(mockStringDescriptor);
        verify(mockEngine).getBean(mockIntDescriptor);
        verify(mockEngine).getBean(mockDoubleDescriptor);
        verify(mockStringApplicator).apply(instance, "abc123");
        verify(mockIntApplicator).apply(instance, 123);
        verify(mockDoubleApplicator).apply(instance, 1.23);
    }

}
