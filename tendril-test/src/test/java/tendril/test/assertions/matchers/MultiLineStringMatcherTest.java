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
 * Test case for {@link MultiLineStringMatcher}
 */
public class MultiLineStringMatcherTest {

    /**
     * Verify that an empty {@link String} matches
     */
    @Test
    public void testEmptyString() {
        Assertions.assertThrows(AssertionFailedError.class, () -> new MultiLineStringMatcher().match(""));
    }

    /**
     * Verify that a {@link String} with a single line matches
     */
    @Test
    public void testSingleLineString() {
        final MultiLineStringMatcher eqMatcher = new MultiLineStringMatcher();
        eqMatcher.eq("abc123");
        eqMatcher.match("abc123");
        Assertions.assertThrows(AssertionFailedError.class, () -> eqMatcher.match("aBc123"));
        Assertions.assertThrows(AssertionFailedError.class, () -> eqMatcher.match("abc213"));

        final MultiLineStringMatcher regexMatcher = new MultiLineStringMatcher();
        regexMatcher.regex("[a-z]+[0-9]+");
        regexMatcher.match("abc123");
        regexMatcher.match("abc213");
        regexMatcher.match("abcdefghijklmnopqrstuvwxyz1234567890");
        Assertions.assertThrows(AssertionFailedError.class, () -> regexMatcher.match("aBc123"));
        Assertions.assertThrows(AssertionFailedError.class, () -> regexMatcher.match("123abc"));
    }

    /**
     * Verify that a {@link String} with multiple lines can be matched exactly
     */
    @Test
    public void testMultiLineEqString() {
        MultiLineStringMatcher matcher = new MultiLineStringMatcher();
        matcher.eq("abc123");
        matcher.eq("123abc");
        matcher.eq("bca321");
        matcher.eq("qwerty");
        matcher.eq("123456");

        // Passes
        matcher.match(buildMultilineString("abc123", "123abc", "bca321", "qwerty", "123456"));

        // Fails
        Assertions.assertThrows(AssertionFailedError.class, () -> matcher.match(buildMultilineString("abC123", "123abc", "bca321", "qwerty", "123456")));
        Assertions.assertThrows(AssertionFailedError.class, () -> matcher.match(buildMultilineString("abc123", "124abc", "bca321", "qwerty", "123456")));
        Assertions.assertThrows(AssertionFailedError.class, () -> matcher.match(buildMultilineString("abc123", "123abc", "bcb321", "qwerty", "123456")));
        Assertions.assertThrows(AssertionFailedError.class, () -> matcher.match(buildMultilineString("abc123", "123abc", "bca321", "qw3rty", "123456")));
        Assertions.assertThrows(AssertionFailedError.class, () -> matcher.match(buildMultilineString("abc123", "123abc", "bca321", "qwerty", "123$56")));
    }

    /**
     * Verify that a {@link String} with multiple lines can be matched to regex
     */
    @Test
    public void testMultiLineRegexString() {
        MultiLineStringMatcher matcher = new MultiLineStringMatcher();
        for (int i = 0; i < 5; i++)
            matcher.regex("[a-z]+[0-9]+");

        // Passes
        matcher.match(buildMultilineString("abc123", "def456", "ghi789", "jkl012", "mno321"));
        matcher.match(buildMultilineString("abcdefghijklmnop1234567890", "abcdefghijklmnop1234567890", "abcdefghijklmnop1234567890", "abcdefghijklmnop1234567890", "abcdefghijklmnop1234567890"));
        matcher.match(buildMultilineString("a123123123123123", "b456456456456456", "c987987987987987", "d2352342342", "e4534987345983749583"));

        // Fails
        Assertions.assertThrows(AssertionFailedError.class, () -> matcher.match(buildMultilineString("123231324", "def456", "ghi789", "jkl012", "mno321")));
        Assertions.assertThrows(AssertionFailedError.class, () -> matcher.match(buildMultilineString("abc123", "sdfsdfsdfsdf", "ghi789", "jkl012", "mno321")));
        Assertions.assertThrows(AssertionFailedError.class, () -> matcher.match(buildMultilineString("abc123", "def456", "98723sdfsdf", "-", "mno321")));
        Assertions.assertThrows(AssertionFailedError.class, () -> matcher.match(buildMultilineString("abc123", "def456", "ghi789", "jkl012", "sdofih97097sdfs")));
        Assertions.assertThrows(AssertionFailedError.class, () -> matcher.match(buildMultilineString("12342ljkhdfg1324", "def456", "ghi789", "jkl012", "mno321")));
        Assertions.assertThrows(AssertionFailedError.class, () -> matcher.match(buildMultilineString("abc123", "(s)", "ghi789", "jkl012", "mno321")));
    }
    
    /**
     * Verify that a mixed eq and regex matcher works as expected across multiple lines
     */
    @Test
    public void testMultiLineMixedString() {
        MultiLineStringMatcher matcher = new MultiLineStringMatcher();
        matcher.eq("abc123");
        matcher.regex("[0-9]+[a-z]+");
        matcher.eq("qwerty");
        matcher.regex("[0-9]+");
        
        // Passes
        matcher.match(buildMultilineString("abc123", "123abc", "qwerty", "123456"));
        matcher.match(buildMultilineString("abc123", "0123456789abcdefghijklmnopqrstuvwxyz", "qwerty", "2039720394723049723049723049723"));
        
        // Fails
        Assertions.assertThrows(AssertionFailedError.class, () -> matcher.match(buildMultilineString("Abc123", "123abc", "qwerty", "123456")));
        Assertions.assertThrows(AssertionFailedError.class, () -> matcher.match(buildMultilineString("abc123", "abc123", "qwerty", "123456")));
        Assertions.assertThrows(AssertionFailedError.class, () -> matcher.match(buildMultilineString("abc123", "123abc", "qwert", "123456")));
        Assertions.assertThrows(AssertionFailedError.class, () -> matcher.match(buildMultilineString("abc123", "123abc", "qwerty", "123456a")));
    }
    
    /**
     * Combine the specified {@link String}s into a single multi-line {@link String} where each entry is a treated as a line, merged with new line characters in between.
     * 
     * @param lines {@link String}... that are to be joined
     * @return {@link String}
     */
    private String buildMultilineString(String... lines) {
        String result = "";
        for (String s : lines)
            result += s + System.lineSeparator();
        return result;
    }
}
