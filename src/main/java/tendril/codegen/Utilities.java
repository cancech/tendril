package tendril.codegen;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility helper class
 */
public class Utilities {

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
