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
package tendril.test.assertions;

import org.junit.jupiter.api.Assertions;

/**
 * Assertion helper for verifying and validating class information in unit tests
 */
public abstract class ClassAssert {

    /**
     * Hidden CTOR
     */
    private ClassAssert() {
    }

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
