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
package tendril;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link TendrilStartupException}
 */
public class TendrilStartupExceptionTest {

    /**
     * Verify that the message is properly stored
     */
    @Test
    public void testMessage() {
        Assertions.assertEquals("abc", new TendrilStartupException("abc").getMessage());
        Assertions.assertEquals("123", new TendrilStartupException("123").getMessage());
        Assertions.assertEquals("abc123", new TendrilStartupException("abc123").getMessage());
    }
}
