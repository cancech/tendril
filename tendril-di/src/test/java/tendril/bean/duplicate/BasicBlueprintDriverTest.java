package tendril.bean.duplicate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test case for the {@link BasicBlueprintDriver}
 */
public class BasicBlueprintDriverTest {

	/**
	 * Verify that the name can be applied and retrieved
	 */
	@Test
	public void testName() {
		Assertions.assertEquals("myName", new BasicBlueprintDriver("myName").getName());
		Assertions.assertEquals("abc123", new BasicBlueprintDriver("abc123").getName());
		Assertions.assertEquals("sdfsdf", new BasicBlueprintDriver("sdfsdf").getName());
		Assertions.assertEquals("124978", new BasicBlueprintDriver("124978").getName());
		Assertions.assertEquals("dfkh?{}:", new BasicBlueprintDriver("dfkh?{}:").getName());
	}
}
