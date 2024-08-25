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
package tendril.bean.qualifier;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link BeanId}
 */
public class BeanIdTest {

    /**
     * Enumeration implementing {@link BeanId} to be used for the test
     */
    private enum TestEnum implements BeanId {
        VAL_1, VAL_2, ABC123;
    }
    
    /**
     * Verify that the default ID is properly generated
     */
    @Test
    public void testId() {
        for (TestEnum en: TestEnum.values())
            Assertions.assertEquals(TestEnum.class.getName() + "." + en, en.getId());
    }
}
