package tendril.codegen;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link VisibilityType}
 */
public class VisibilityTypeTest {
	
	/**
	 * Verify that the items produce the correct code
	 */
	@Test
	public void testItems() {
		Assertions.assertEquals("public", VisibilityType.PUBLIC.toString());
		Assertions.assertEquals("private", VisibilityType.PRIVATE.toString());
		Assertions.assertEquals("", VisibilityType.PACKAGE_PRIVATE.toString());
		Assertions.assertEquals("protected", VisibilityType.PROTECTED.toString());
	}

}
