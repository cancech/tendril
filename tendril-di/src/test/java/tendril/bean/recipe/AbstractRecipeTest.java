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

import tendril.BeanCreationException;
import tendril.context.Engine;
import tendril.test.AbstractUnitTest;
import tendril.test.bean.SingleCtorBean;

/**
 * Test case for the {@link AbstractRecipe}
 */
public class AbstractRecipeTest extends AbstractUnitTest {
    
    /**
     * Concrete implementation of the {@link AbstractRecipe} to use for testing
     */
    private class TestRecipe extends AbstractRecipe<SingleCtorBean> {

        private boolean isDescriptorSetup;
        private int timesCreateInstanceCalled = 0;
        private int timesPostConstructCalled = 0;
        private SingleCtorBean postConstructBean = null;

        private boolean createInstanceThrows = false;
        private boolean postConstructThrows = false;
        
        protected TestRecipe() {
            super(mockEngine, SingleCtorBean.class);
        }

        /**
         * @see tendril.bean.recipe.AbstractRecipe#setupDescriptor(tendril.bean.recipe.Descriptor)
         */
        @Override
        protected void setupDescriptor(Descriptor<SingleCtorBean> descriptor) {
            Assertions.assertFalse(isDescriptorSetup);
            isDescriptorSetup = true;
        }

        @Override
        public SingleCtorBean get() {
            throw new NotImplementedException("Not required for testing");
        }
        
        public void assertDescriptorSetup() {
            Assertions.assertTrue(isDescriptorSetup);
        }

        /**
         * @see tendril.bean.recipe.AbstractRecipe#createInstance(tendril.context.Engine)
         */
        @Override
        protected SingleCtorBean createInstance(Engine engine) {
            if (createInstanceThrows)
                throw new NullPointerException();
            timesCreateInstanceCalled++;
            return new SingleCtorBean();
        }
        
        public void assertTimesCreateInstanceCalled(int timesExpected) {
            Assertions.assertEquals(timesExpected, timesCreateInstanceCalled);
        }
        
        /**
         * @see tendril.bean.recipe.AbstractRecipe#postConstruct(java.lang.Object)
         */
        @Override
        protected void postConstruct(SingleCtorBean bean) {
            if (postConstructThrows)
                throw new NullPointerException();
            timesPostConstructCalled++;
            postConstructBean = bean;
        }
        
        public void assertTimesPostConstructCalled(int timesExpected, SingleCtorBean expectedBean) {
            Assertions.assertEquals(timesExpected, timesPostConstructCalled);
            Assertions.assertEquals(expectedBean, postConstructBean);
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
    @Mock
    private Injector<SingleCtorBean> mockInjector1;
    @Mock
    private Injector<SingleCtorBean> mockInjector2;
    @Mock
    private Injector<SingleCtorBean> mockInjector3;
    
    // Instance to test
    private TestRecipe recipe;

    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        recipe = new TestRecipe();
        recipe.assertDescriptorSetup();
        Assertions.assertEquals(SingleCtorBean.class, recipe.getDescription().getBeanClass());
    }
    
    /**
     * Verify that an exception is thrown if an exception is encountered during the bean creation
     */
    @Test
    public void testBuildBeanExceptionThrown() {
        // If createInstance generate an exception
        recipe.createInstanceThrows = true;
        Assertions.assertThrows(BeanCreationException.class, () -> recipe.buildBean());
        recipe.assertTimesCreateInstanceCalled(0);
        recipe.assertTimesPostConstructCalled(0, null);
        
        // If postConstruct generate an exception
        recipe.createInstanceThrows = false;
        recipe.postConstructThrows = true;
        Assertions.assertThrows(BeanCreationException.class, () -> recipe.buildBean());
        recipe.assertTimesCreateInstanceCalled(1);
        recipe.assertTimesPostConstructCalled(0, null);
        
        // If injection generate an exception
        recipe.createInstanceThrows = false;
        recipe.postConstructThrows = false;
        recipe.registerInjector((consumer, engine) -> { throw new IllegalArgumentException(); });
        Assertions.assertThrows(BeanCreationException.class, () -> recipe.buildBean());
        recipe.assertTimesCreateInstanceCalled(2);
        recipe.assertTimesPostConstructCalled(0, null);
    }
    
    /**
     * Verify that the bean can be built if no dependencies are specified
     */
    @Test
    public void testBuildNoDependencies() {
        SingleCtorBean instance = recipe.buildBean();
        Assertions.assertNotNull(instance);
        recipe.assertTimesCreateInstanceCalled(1);
        recipe.assertTimesPostConstructCalled(1, instance);
    }
    
    /**
     * Verify that the bean can be built if one Applicator is specified
     */
    @Test
    public void testBuildSingleApplicatorNoInjectors() {
        recipe.registerDependency(mockStringDescriptor, mockStringApplicator);
        verifyAllChecked();
        
        when(mockEngine.getBean(mockStringDescriptor)).thenReturn("abc123");
        SingleCtorBean instance = recipe.buildBean();
        Assertions.assertNotNull(instance);
        recipe.assertTimesCreateInstanceCalled(1);
        recipe.assertTimesPostConstructCalled(1, instance);
        verify(mockEngine).getBean(mockStringDescriptor);
        verify(mockStringApplicator).apply(instance, "abc123");
    }
    
    /**
     * Verify that the bean can be built if multiples Applicators are specified
     */
    @Test
    public void testBuildMultipleApplicatorsNoInjectors() {
        recipe.registerDependency(mockStringDescriptor, mockStringApplicator);
        recipe.registerDependency(mockIntDescriptor, mockIntApplicator);
        recipe.registerDependency(mockDoubleDescriptor, mockDoubleApplicator);
        verifyAllChecked();

        when(mockEngine.getBean(mockStringDescriptor)).thenReturn("abc123");
        when(mockEngine.getBean(mockIntDescriptor)).thenReturn(123);
        when(mockEngine.getBean(mockDoubleDescriptor)).thenReturn(1.23);
        SingleCtorBean instance = recipe.buildBean();
        recipe.assertTimesCreateInstanceCalled(1);
        recipe.assertTimesPostConstructCalled(1, instance);
        Assertions.assertNotNull(instance);
        verify(mockEngine).getBean(mockStringDescriptor);
        verify(mockEngine).getBean(mockIntDescriptor);
        verify(mockEngine).getBean(mockDoubleDescriptor);
        verify(mockStringApplicator).apply(instance, "abc123");
        verify(mockIntApplicator).apply(instance, 123);
        verify(mockDoubleApplicator).apply(instance, 1.23);
    }
    
    /**
     * Verify that the bean can be build if one injector is specified
     */
    @Test
    public void testBuildSingleInjectorNoApplicators() {
        recipe.registerInjector(mockInjector1);
        verifyAllChecked();
        
        SingleCtorBean instance = recipe.buildBean();
        recipe.assertTimesCreateInstanceCalled(1);
        recipe.assertTimesPostConstructCalled(1, instance);
        Assertions.assertNotNull(instance);
        verify(mockInjector1).inject(instance, mockEngine);
    }
    
    /**
     * Verify that the bean can be build if multiple injectors are specified
     */
    @Test
    public void testBuildMultipleInjectorsNoApplicators() {
        recipe.registerInjector(mockInjector1);
        recipe.registerInjector(mockInjector2);
        recipe.registerInjector(mockInjector3);
        verifyAllChecked();
        
        SingleCtorBean instance = recipe.buildBean();
        recipe.assertTimesCreateInstanceCalled(1);
        recipe.assertTimesPostConstructCalled(1, instance);
        Assertions.assertNotNull(instance);
        verify(mockInjector1).inject(instance, mockEngine);
        verify(mockInjector2).inject(instance, mockEngine);
        verify(mockInjector3).inject(instance, mockEngine);
    }
    
    /**
     * Verify that the bean can be built if multiples Applicators and multiple injectors are specified
     */
    @Test
    public void testBuildMultipleApplicatorsMultipleInjectors() {
        recipe.registerDependency(mockStringDescriptor, mockStringApplicator);
        recipe.registerDependency(mockIntDescriptor, mockIntApplicator);
        recipe.registerDependency(mockDoubleDescriptor, mockDoubleApplicator);
        recipe.registerInjector(mockInjector1);
        recipe.registerInjector(mockInjector2);
        recipe.registerInjector(mockInjector3);
        verifyAllChecked();

        when(mockEngine.getBean(mockStringDescriptor)).thenReturn("abc123");
        when(mockEngine.getBean(mockIntDescriptor)).thenReturn(123);
        when(mockEngine.getBean(mockDoubleDescriptor)).thenReturn(1.23);
        SingleCtorBean instance = recipe.buildBean();
        recipe.assertTimesCreateInstanceCalled(1);
        recipe.assertTimesPostConstructCalled(1, instance);
        Assertions.assertNotNull(instance);
        verify(mockEngine).getBean(mockStringDescriptor);
        verify(mockEngine).getBean(mockIntDescriptor);
        verify(mockEngine).getBean(mockDoubleDescriptor);
        verify(mockStringApplicator).apply(instance, "abc123");
        verify(mockIntApplicator).apply(instance, 123);
        verify(mockDoubleApplicator).apply(instance, 1.23);
        verify(mockInjector1).inject(instance, mockEngine);
        verify(mockInjector2).inject(instance, mockEngine);
        verify(mockInjector3).inject(instance, mockEngine);
    }

}
