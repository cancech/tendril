package test.assertions.string;

import org.opentest4j.AssertionFailedError;

/**
 * Matcher that can be used to ensure that a class matches expectation. This can be by being an exact match (i.e.: completely equal) or by meeting an expected regular expression
 */
public abstract class StringMatcher {
	/** The expected match */
	protected final String expected;

	/**
	 * CTOR
	 * 
	 * @param expected {@link String} what is expected to match to
	 */
	StringMatcher(String expected) {
		this.expected = expected;
	}

	/**
	 * Create a matcher which matches to an expected regular expression
	 * 
	 * @param expectedRegex {@link String} the regular expression to match to
	 * @return {@link StringMatcher}
	 */
	public static StringMatcher regex(String expectedRegex) {
		return new RegexMatcher(expectedRegex);
	}

	/**
	 * Create a matcher which check for equality, such that the contents of the {@link String} is the same as per expectations
	 * 
	 * @param expected {@link String} the exact value that is expected
	 * @return {@link StringMatcher}
	 */
	public static StringMatcher eq(String expected) {
		return new EqualityMatcher(expected);
	}

	/**
	 * Verify that the actual {@link String} matches expectations
	 * 
	 * @param actual {@link String} the actual string to verify
	 */
	public void assertMatches(String actual) {
		assertMatches(actual, null);
	}

	/**
	 * Verify that the actual {@link String} matches expectations, with a {@link String} prefix that will be displayed before the generated failure method
	 * 
	 * @param actual    {@link String} the actual string to verify
	 * @param msgPrefix {@link String} to display before the failure message
	 */
	public void assertMatches(String actual, String msgPrefix) {
		if (msgPrefix == null)
			msgPrefix = "";
		else
			msgPrefix += " ";
		if (!match(actual))
			throw new AssertionFailedError(msgPrefix + "Match failure, expected \"" + expected + "\" but was \"" + actual + "\"");
	}

	/**
	 * Perform the match and check if the actual {@link String} matches expectations
	 * 
	 * @param actual {@link String} to verify
	 * @return boolean true if it matches expectations
	 */
	abstract boolean match(String actual);

}
