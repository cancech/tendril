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
	 * @see tendril.test.assertions.matchers.StringMatcher#match(java.lang.String)
	 */
	@Override
	protected boolean match(String actual) {
		if (expected == null || actual == null)
			return false;

		return actual.matches(expected);
	}
}