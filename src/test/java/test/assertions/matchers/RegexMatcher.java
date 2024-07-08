package test.assertions.matchers;

/**
 * Performs matching to see whether the actual {@link String} matches the provided regular expression
 */
class RegexMatcher extends StringMatcher {

	/**
	 * CTOR
	 * 
	 * @param regex {@link String} the regular expression expected to conform to
	 */
	RegexMatcher(String regex) {
		super(regex);
	}

	/**
	 * @see test.assertions.matchers.StringMatcher#match(java.lang.String)
	 */
	@Override
	protected boolean match(String actual) {
		if (actual == null)
			return false;

		return actual.matches(expected);
	}
}