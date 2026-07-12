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
	public void testValidIdentifiersForClass() {
		// Valid identifiers
		verifyIdentifierForClass("doSomething", false);
		verifyIdentifierForClass("_123Abc", false);
		verifyIdentifierForClass("ABCD", false);
		verifyIdentifierForClass("a_b_c_d", false);
		verifyIdentifierForClass("________", false);
		verifyIdentifierForClass("_0123", false);
		verifyIdentifierForClass("qwerty", false);
		verifyIdentifierForClass("po_P_F_SPed_as123", false);
		verifyIdentifierForClass("_something_", false);
		verifyIdentifierForClass("$something$", false);
		verifyIdentifierForClass("a.b.c", false);

		// Invalid identifiers
		verifyIdentifierForClass("1", true);
		verifyIdentifierForClass("2", true);
		verifyIdentifierForClass("3", true);
		verifyIdentifierForClass("4", true);
		verifyIdentifierForClass("5", true);
		verifyIdentifierForClass("6", true);
		verifyIdentifierForClass("7", true);
		verifyIdentifierForClass("8", true);
		verifyIdentifierForClass("9", true);
		verifyIdentifierForClass("0", true);

		verifyIdentifierForClass("-", true);
		verifyIdentifierForClass("!", true);
		verifyIdentifierForClass("@", true);
		verifyIdentifierForClass("#", true);
		verifyIdentifierForClass("%", true);
		verifyIdentifierForClass("^", true);
		verifyIdentifierForClass("&", true);
		verifyIdentifierForClass("*", true);
		verifyIdentifierForClass("(", true);
		verifyIdentifierForClass(")", true);
		verifyIdentifierForClass("=", true);
		verifyIdentifierForClass("+", true);
		verifyIdentifierForClass("[", true);
		verifyIdentifierForClass("]", true);
		verifyIdentifierForClass("{", true);
		verifyIdentifierForClass("}", true);
		verifyIdentifierForClass(";", true);
		verifyIdentifierForClass(":", true);
		verifyIdentifierForClass("\'", true);
		verifyIdentifierForClass("\"", true);
		verifyIdentifierForClass("\\", true);
		verifyIdentifierForClass(",", true);
		verifyIdentifierForClass("<", true);
		verifyIdentifierForClass(".", true);
		verifyIdentifierForClass(">", true);
		verifyIdentifierForClass("/", true);
		verifyIdentifierForClass("|", true);
		verifyIdentifierForClass("`", true);
		verifyIdentifierForClass("~", true);

		verifyIdentifierForClass(".a", true);
		verifyIdentifierForClass("a-", true);
		verifyIdentifierForClass("a!", true);
		verifyIdentifierForClass("a@", true);
		verifyIdentifierForClass("a#", true);
		verifyIdentifierForClass("a%", true);
		verifyIdentifierForClass("a^", true);
		verifyIdentifierForClass("a&", true);
		verifyIdentifierForClass("a*", true);
		verifyIdentifierForClass("a(", true);
		verifyIdentifierForClass("a)", true);
		verifyIdentifierForClass("a=", true);
		verifyIdentifierForClass("a+", true);
		verifyIdentifierForClass("a[", true);
		verifyIdentifierForClass("a]", true);
		verifyIdentifierForClass("a{", true);
		verifyIdentifierForClass("a}", true);
		verifyIdentifierForClass("a;", true);
		verifyIdentifierForClass("a:", true);
		verifyIdentifierForClass("a\'", true);
		verifyIdentifierForClass("a\"", true);
		verifyIdentifierForClass("a\\", true);
		verifyIdentifierForClass("a,", true);
		verifyIdentifierForClass("a<", true);
		verifyIdentifierForClass("a.", true);
		verifyIdentifierForClass("a>", true);
		verifyIdentifierForClass("a/", true);
		verifyIdentifierForClass("a|", true);
		verifyIdentifierForClass("a`", true);
		verifyIdentifierForClass("a~", true);
		verifyIdentifierForClass(null, true);
		verifyIdentifierForClass("", true);
		verifyIdentifierForClass("     ", true);
		verifyIdentifierForClass("          ", true);
	}
	
	/**
	 * Verify that identifiers are correctly confirmed as valid or not
	 */
	@Test
	public void testValidIdentifiersForGeneric() {
		// Valid identifiers
		verifyIdentifierOther("doSomething", false);
		verifyIdentifierOther("_123Abc", false);
		verifyIdentifierOther("ABCD", false);
		verifyIdentifierOther("a_b_c_d", false);
		verifyIdentifierOther("________", false);
		verifyIdentifierOther("_0123", false);
		verifyIdentifierOther("qwerty", false);
		verifyIdentifierOther("po_P_F_SPed_as123", false);
		verifyIdentifierOther("_something_", false);
		verifyIdentifierOther("$something$", false);
		verifyIdentifierOther("a.b.c", true);

		// Invalid identifiers
		verifyIdentifierOther("1", true);
		verifyIdentifierOther("2", true);
		verifyIdentifierOther("3", true);
		verifyIdentifierOther("4", true);
		verifyIdentifierOther("5", true);
		verifyIdentifierOther("6", true);
		verifyIdentifierOther("7", true);
		verifyIdentifierOther("8", true);
		verifyIdentifierOther("9", true);
		verifyIdentifierOther("0", true);

		verifyIdentifierOther("-", true);
		verifyIdentifierOther("!", true);
		verifyIdentifierOther("@", true);
		verifyIdentifierOther("#", true);
		verifyIdentifierOther("%", true);
		verifyIdentifierOther("^", true);
		verifyIdentifierOther("&", true);
		verifyIdentifierOther("*", true);
		verifyIdentifierOther("(", true);
		verifyIdentifierOther(")", true);
		verifyIdentifierOther("=", true);
		verifyIdentifierOther("+", true);
		verifyIdentifierOther("[", true);
		verifyIdentifierOther("]", true);
		verifyIdentifierOther("{", true);
		verifyIdentifierOther("}", true);
		verifyIdentifierOther(";", true);
		verifyIdentifierOther(":", true);
		verifyIdentifierOther("\'", true);
		verifyIdentifierOther("\"", true);
		verifyIdentifierOther("\\", true);
		verifyIdentifierOther(",", true);
		verifyIdentifierOther("<", true);
		verifyIdentifierOther(".", true);
		verifyIdentifierOther(">", true);
		verifyIdentifierOther("/", true);
		verifyIdentifierOther("|", true);
		verifyIdentifierOther("`", true);
		verifyIdentifierOther("~", true);

		verifyIdentifierOther(".a", true);
		verifyIdentifierOther("a-", true);
		verifyIdentifierOther("a!", true);
		verifyIdentifierOther("a@", true);
		verifyIdentifierOther("a#", true);
		verifyIdentifierOther("a%", true);
		verifyIdentifierOther("a^", true);
		verifyIdentifierOther("a&", true);
		verifyIdentifierOther("a*", true);
		verifyIdentifierOther("a(", true);
		verifyIdentifierOther("a)", true);
		verifyIdentifierOther("a=", true);
		verifyIdentifierOther("a+", true);
		verifyIdentifierOther("a[", true);
		verifyIdentifierOther("a]", true);
		verifyIdentifierOther("a{", true);
		verifyIdentifierOther("a}", true);
		verifyIdentifierOther("a;", true);
		verifyIdentifierOther("a:", true);
		verifyIdentifierOther("a\'", true);
		verifyIdentifierOther("a\"", true);
		verifyIdentifierOther("a\\", true);
		verifyIdentifierOther("a,", true);
		verifyIdentifierOther("a<", true);
		verifyIdentifierOther("a.", true);
		verifyIdentifierOther("a>", true);
		verifyIdentifierOther("a/", true);
		verifyIdentifierOther("a|", true);
		verifyIdentifierOther("a`", true);
		verifyIdentifierOther("a~", true);
		verifyIdentifierOther(null, true);
		verifyIdentifierOther("", true);
		verifyIdentifierOther("     ", true);
		verifyIdentifierOther("          ", true);
	}

	/**
	 * Helper to shorthand the verifications when checking a class name
	 * 
	 * @param name      {@link String} to verify
	 * @param doesThrow boolean true if the exception should be thrown
	 */
	private void verifyIdentifierForClass(String name, boolean doesThrow) {
		verifyIdentifier(name, true, doesThrow);
	}

	/**
	 * Helper to shorthand the verifications when checking other types of names (i.e.: not class name)
	 * 
	 * @param name      {@link String} to verify
	 * @param doesThrow boolean true if the exception should be thrown
	 */
	private void verifyIdentifierOther(String name, boolean doesThrow) {
		verifyIdentifier(name, false, doesThrow);
	}

	/**
	 * Helper to shorthand the verifications when checking names
	 * 
	 * @param name      {@link String} to verify
	 * @param isClass   boolean true if the name under test is a class name
	 * @param doesThrow boolean true if the exception should be thrown
	 */
	private void verifyIdentifier(String name, boolean isClass, boolean doesThrow) {
		Executable ex = () -> Utilities.throwIfNotValidIdentifier(name, isClass);

		if (doesThrow)
			Assertions.assertThrows(DefinitionException.class, ex);
		else
			Assertions.assertDoesNotThrow(ex);
	}
}
