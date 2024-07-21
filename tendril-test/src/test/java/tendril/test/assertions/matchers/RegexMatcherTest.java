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

/**
 * Test case for {@link RegexMatcher}
 */
public class RegexMatcherTest {

    /**
     * Verify the passing matches work as expected
     */
    @Test
    public void testMatches() {
        Assertions.assertTrue(new RegexMatcher("abc123").match("abc123"));
        Assertions.assertTrue(new RegexMatcher("[a-z]+[0-9]+").match("abc123"));
        Assertions.assertTrue(new RegexMatcher("[a-z]+[0-9]+").match("aaaaaaaaaaaaaaabbbbbbbbbbbbbbbbcccccccccccccccccccc22222222222222"));
        Assertions.assertTrue(new RegexMatcher("[a-z]+[0-9]+").match("a2380720394724957324957340957304957340957"));
    }

    /**
     * Verify the failing matches work as expected
     */
    @Test
    public void testDoesNotMatch() {
        Assertions.assertFalse(new RegexMatcher("abc123").match("ABC123"));
        Assertions.assertFalse(new RegexMatcher("[a-z]+[0-9]+").match("123"));
        Assertions.assertFalse(new RegexMatcher("[a-z]+[0-9]+").match("abc"));
        Assertions.assertFalse(new RegexMatcher("[a-z]+[0-9]+").match("ABC123"));
        Assertions.assertFalse(new RegexMatcher("[a-z]+[0-9]+").match("123abc"));
        Assertions.assertFalse(new RegexMatcher("abc123").match(null));
        Assertions.assertFalse(new RegexMatcher(null).match("abc123"));
    }
}
