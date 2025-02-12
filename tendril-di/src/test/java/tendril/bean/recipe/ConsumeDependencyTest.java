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

import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.context.Engine;
import tendril.test.AbstractUnitTest;
import tendril.test.bean.HiddenCtorBean;
import tendril.test.bean.SingleCtorBean;

/**
 * Test case for {@link ConsumeDependency}
 */
public class ConsumeDependencyTest extends AbstractUnitTest {
    
    // Mocks to use for testing
    @Mock
    private Descriptor<HiddenCtorBean> mockDescriptor;
    @Mock
    private Applicator<SingleCtorBean, HiddenCtorBean> mockApplicator;
    @Mock
    private Engine mockEngine;
    @Mock
    private SingleCtorBean mockBean;
    @Mock
    private HiddenCtorBean mockDependency;

    // Instance to test
    private ConsumeDependency<SingleCtorBean, HiddenCtorBean> dep;
    
    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        dep = new ConsumeDependency<>(mockDescriptor, mockApplicator);
    }
    
    /**
     * Verify that the dependency is properly consumed.
     */
    @Test
    public void testConsume() {
        when(mockEngine.getBean(mockDescriptor)).thenReturn(mockDependency);
        
        dep.consume(mockBean, mockEngine);
        
        verify(mockEngine).getBean(mockDescriptor);
        verify(mockApplicator).apply(mockBean, mockDependency);
    }

}
