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
package tendril.annotationprocessor.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link InvalidConfigurationException}
 */
public class InvalidConfigurationExceptionTest {

    /**
     * Verify that the message is properly stored
     */
    @Test
    public void testMessage() {
        Assertions.assertEquals("abc123", new InvalidConfigurationException("abc123").getMessage());
        Assertions.assertEquals("123abc", new InvalidConfigurationException("123abc").getMessage());
        Assertions.assertEquals("qwerty", new InvalidConfigurationException("qwerty").getMessage());
    }
}
