/**
 * 
 */
package test.assertions.matchers;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;

/**
 * Matcher for multi-line {@link String}s. Each expected line of the {@link String} is added as a separate entry into the matcher, which then compares each line by line to the provided actual.
 */
public class MultiLineStringMatcher {
    /** All registered lines that are expected */
    private final List<StringMatcher> expectedLines = new ArrayList<>();

    /**
     * Add a line that should be matched exactly.
     * 
     * @param expected {@link String} the exact expected line
     */
    public void eq(String expected) {
        expectedLines.add(StringMatcher.eq(expected));
    }

    /**
     * Add a line that should match the provided regular expression.
     * 
     * @param expectedRegex {@link String} the regular expression to match
     */
    public void regex(String expectedRegex) {
        expectedLines.add(StringMatcher.regex(expectedRegex));
    }

    /**
     * Match the expected line to the actual multi-line {@link String}
     * 
     * @param actualMultiline {@link String} with multiple lines which are to be checked against expectations
     */
    public void match(String actualMultiline) {
        String[] actual = actualMultiline.split(System.lineSeparator());

        Assertions.assertEquals(expectedLines.size(), actual.length);
        for (int i = 0; i < expectedLines.size(); i++) {
            expectedLines.get(i).assertMatches(actual[i], "Line " + (i + 1));
        }
    }
}
