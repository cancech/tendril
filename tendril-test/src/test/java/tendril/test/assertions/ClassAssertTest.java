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
package tendril.test.assertions;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

/**
 * Test case for {@link ClassAssert}
 */
public class ClassAssertTest {

    /**
     * Verify that assertInstance works properly
     */
    @Test
    public void testAssertInstance() {
        // Pass
        ClassAssert.assertInstance(String.class, "abc123");
        ClassAssert.assertInstance(Integer.class, 321);
        ClassAssert.assertInstance(ClassAssertTest.class, this);
        
        // Fail
        Assertions.assertThrows(AssertionFailedError.class, () -> ClassAssert.assertInstance(String.class, 312));
        Assertions.assertThrows(AssertionFailedError.class, () -> ClassAssert.assertInstance(Integer.class, this));
        Assertions.assertThrows(AssertionFailedError.class, () -> ClassAssert.assertInstance(ClassAssertTest.class, "abc123"));
    }
}
