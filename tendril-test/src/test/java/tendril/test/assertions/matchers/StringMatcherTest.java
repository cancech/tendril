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
package tendril.test.assertions.matchers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

/**
 * Test case for {@link StringMatcher}
 */
public class StringMatcherTest {

    /**
     * Verify that the correct default failure message is prepared
     */
    @Test
    public void testDefaultFailureMessage() {
        Throwable ex = Assertions.assertThrows(AssertionFailedError.class, () -> StringMatcher.eq("abc123").assertMatches("123abc"));
        Assertions.assertEquals("Match failure, expected \"abc123\" but was \"123abc\"", ex.getMessage());
    }

    /**
     * Verify that the correct custom failure message is prepared
     */
    @Test
    public void testCustomFailureMessage() {
        Throwable ex = Assertions.assertThrows(AssertionFailedError.class, () -> StringMatcher.eq("abc123").assertMatches("123abc", null));
        Assertions.assertEquals("Match failure, expected \"abc123\" but was \"123abc\"", ex.getMessage());

        ex = Assertions.assertThrows(AssertionFailedError.class, () -> StringMatcher.eq("abc123").assertMatches("123abc", "Some Prefix"));
        Assertions.assertEquals("Some Prefix match failure, expected \"abc123\" but was \"123abc\"", ex.getMessage());
    }
}
