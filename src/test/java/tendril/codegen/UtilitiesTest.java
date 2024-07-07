package tendril.codegen;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

/**
 * Test case for {@link Utilities}
 */
public class UtilitiesTest {

	/**
	 * Verify that the time stamp is properly prepared
	 */
	@Test
	public void testIso8061TimeStamp() {
		String actual = Utilities.iso8061TimeStamp();
		Assertions.assertNotNull(actual);
		Assertions.assertTrue(actual.matches("\\\\d{4}-\\\\d{2}-\\\\d{2}T\\\\d{2}:\\\\d{2}:\\\\d{2}.\\\\d{7}"), actual + " does not meet regular expression ####-##-##T##:##:##.#######");
	}

	/**
	 * Verify that identifiers are correctly confirmed as valid or not
	 */
	@Test
	public void testValidIdentifiers() {
		// Valid identifiers
		verifyIdentifier("doSomething", false);
		verifyIdentifier("_123Abc", false);
		verifyIdentifier("ABCD", false);
		verifyIdentifier("a_b_c_d", false);
		verifyIdentifier("________", false);
		verifyIdentifier("_0123", false);
		verifyIdentifier("qwerty", false);
		verifyIdentifier("po_P_F_SPed_as123", false);
		verifyIdentifier("_something_", false);
		verifyIdentifier("$something$", false);

		// Invalid identifiers
		verifyIdentifier("1", true);
		verifyIdentifier("2", true);
		verifyIdentifier("3", true);
		verifyIdentifier("4", true);
		verifyIdentifier("5", true);
		verifyIdentifier("6", true);
		verifyIdentifier("7", true);
		verifyIdentifier("8", true);
		verifyIdentifier("9", true);
		verifyIdentifier("0", true);

		verifyIdentifier("-", true);
		verifyIdentifier("!", true);
		verifyIdentifier("@", true);
		verifyIdentifier("#", true);
		verifyIdentifier("%", true);
		verifyIdentifier("^", true);
		verifyIdentifier("&", true);
		verifyIdentifier("*", true);
		verifyIdentifier("(", true);
		verifyIdentifier(")", true);
		verifyIdentifier("=", true);
		verifyIdentifier("+", true);
		verifyIdentifier("[", true);
		verifyIdentifier("]", true);
		verifyIdentifier("{", true);
		verifyIdentifier("}", true);
		verifyIdentifier(";", true);
		verifyIdentifier(":", true);
		verifyIdentifier("\'", true);
		verifyIdentifier("\"", true);
		verifyIdentifier("\\", true);
		verifyIdentifier(",", true);
		verifyIdentifier("<", true);
		verifyIdentifier(".", true);
		verifyIdentifier(">", true);
		verifyIdentifier("/", true);
		verifyIdentifier("|", true);
		verifyIdentifier("`", true);
		verifyIdentifier("~", true);

		verifyIdentifier("a-", true);
		verifyIdentifier("a!", true);
		verifyIdentifier("a@", true);
		verifyIdentifier("a#", true);
		verifyIdentifier("a%", true);
		verifyIdentifier("a^", true);
		verifyIdentifier("a&", true);
		verifyIdentifier("a*", true);
		verifyIdentifier("a(", true);
		verifyIdentifier("a)", true);
		verifyIdentifier("a=", true);
		verifyIdentifier("a+", true);
		verifyIdentifier("a[", true);
		verifyIdentifier("a]", true);
		verifyIdentifier("a{", true);
		verifyIdentifier("a}", true);
		verifyIdentifier("a;", true);
		verifyIdentifier("a:", true);
		verifyIdentifier("a\'", true);
		verifyIdentifier("a\"", true);
		verifyIdentifier("a\\", true);
		verifyIdentifier("a,", true);
		verifyIdentifier("a<", true);
		verifyIdentifier("a.", true);
		verifyIdentifier("a>", true);
		verifyIdentifier("a/", true);
		verifyIdentifier("a|", true);
		verifyIdentifier("a`", true);
		verifyIdentifier("a~", true);
	}

	/**
	 * Helper to shorthand the verifications
	 * 
	 * @param name      {@link String} to verify
	 * @param doesThrow boolean true if the exception should be thrown
	 */
	private void verifyIdentifier(String name, boolean doesThrow) {
		Executable ex = () -> Utilities.throwIfNotValidIdentifier(name);

		if (doesThrow)
			Assertions.assertThrows(IllegalArgumentException.class, ex);
		else
			Assertions.assertDoesNotThrow(ex);
	}
}
