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
		Assertions.assertTrue(actual.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d+"), actual + " does not meet regular expression ####-##-##T##:##:##.#######");
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
		verifyIdentifier(null, true);
        verifyIdentifier("", true);
        verifyIdentifier("     ", true);
        verifyIdentifier("          ", true);
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
