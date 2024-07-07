package test.assertions;

import org.junit.jupiter.api.Assertions;

/**
 * Assertion helper for verifying and validating class information in unit tests
 */
public class ClassAssert {

	/**
	 * Verify that a given object is of an expected type
	 * 
	 * @param expectedType {@link Class} that is expected
	 * @param actual       {@link Object} the instance to check
	 */
	public static void assertInstance(Class<?> expectedType, Object actual) {
		Assertions.assertTrue(expectedType.isInstance(actual), "Not an instance: expected " + expectedType.getSimpleName() + " but was " + actual.getClass().getSimpleName());
	}
}
