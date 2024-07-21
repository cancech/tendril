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
	 * @see tendril.test.assertions.matchers.StringMatcher#match(java.lang.String)
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
