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
 * Test case for {@link EqualityMatcher}
 */
public class EqualityMatcherTest {

    /**
     * Verify the passing matches work as expected
     */
    @Test
    public void testMatches() {
        Assertions.assertTrue(new EqualityMatcher("abc123").match("abc123"));
        Assertions.assertTrue(new EqualityMatcher("321321").match("321321"));
        Assertions.assertTrue(new EqualityMatcher(null).match(null));
    }

    /**
     * Verify the failing matches work as expected
     */
    @Test
    public void testDoesNotMatch() {
        Assertions.assertFalse(new EqualityMatcher("abc123").match("ABC123"));
        Assertions.assertFalse(new EqualityMatcher("321321").match("3213211"));
        Assertions.assertFalse(new EqualityMatcher("abc123").match(null));
        Assertions.assertFalse(new EqualityMatcher(null).match("abc123"));
    }
}
