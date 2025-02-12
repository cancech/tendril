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

import tendril.test.AbstractUnitTest;
import tendril.test.bean.SingleCtorBean;

/**
 * Test class for {@link Descriptor}
 */
public class DescriptorTest extends AbstractUnitTest {

    // Instance to test
    private Descriptor<SingleCtorBean> descriptor;
    
    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        descriptor = new Descriptor<>(SingleCtorBean.class);
    }

    /**
     * Verify that the default values are as per expectations
     */
    @Test
    public void testDefaultValues() {
        Assertions.assertEquals(SingleCtorBean.class, descriptor.getBeanClass());
        Assertions.assertEquals("", descriptor.getName());
    }

    /**
     * Verify that the name can be updated
     */
    @Test
    public void testUpdateName() {
        descriptor.setName("SomeBeanName");
        Assertions.assertEquals(SingleCtorBean.class, descriptor.getBeanClass());
        Assertions.assertEquals("SomeBeanName", descriptor.getName());
    }
}
