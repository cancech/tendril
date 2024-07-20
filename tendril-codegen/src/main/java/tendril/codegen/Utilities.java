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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility helper class
 */
public abstract class Utilities {

    /**
     * Hidden CTOR
     */
    private Utilities() {
    }

	/**
	 * Generate a time stamp (current time) per the ISO 8061 standard
	 * 
	 * @return {@link String} containing the ISO 8061 current time stamp
	 */
    public static String iso8061TimeStamp() {
        return DateTimeFormatter.ISO_DATE_TIME.format(LocalDateTime.now());
    }
    
    /**
     * Validate whether the name is considered a valid identifier
     * 
     * @param name {@link String} to check
     * @throws IllegalArgumentException if it is not a valid identifier
     */
    public static void throwIfNotValidIdentifier(String name) throws IllegalArgumentException{
		// Check basic name characteristics
		if (name == null)
			throw new IllegalArgumentException("Method name cannot be null");
		
		// Make sure that it is not an empty name
		String trimmed = name.trim();
		if (trimmed.isEmpty())
			throw new IllegalArgumentException("Method name cannot be empty");
		
		// Ensure that only valid characters are employed
		char c = trimmed.charAt(0);
		if (!Character.isJavaIdentifierStart(c))
			throw new IllegalArgumentException("Method cannot start with " + c);
		for (int i = 1; i < trimmed.length(); i++) {
			c = trimmed.charAt(i);

			if (!Character.isJavaIdentifierPart(c))
				throw new IllegalArgumentException("Method cannot contain " + c);
		}
    }
}
