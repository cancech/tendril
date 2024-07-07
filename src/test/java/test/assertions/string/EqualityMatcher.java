package test.assertions.string;

/**
 * Matcher for ensuring that the actual {@link String} is fully equal to expectations
 */
public class EqualityMatcher extends StringMatcher {

	/**
	 * CTOR
	 * 
	 * @param expected {@link String} the exact {@link String} to match
	 */
	EqualityMatcher(String expected) {
		super(expected);
	}

	/**
	 * @see test.assertions.string.StringMatcher#match(java.lang.String)
	 */
	@Override
	boolean match(String actual) {
		if (actual == null && expected == null)
			return true;

		if (actual != null)
			return actual.equals(expected);

		return false;
	}

}
