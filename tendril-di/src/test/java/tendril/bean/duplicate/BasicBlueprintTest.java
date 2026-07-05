package tendril.bean.duplicate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test case for the {@link BasicBlueprint}
 */
public class BasicBlueprintTest {

	/**
	 * Verify that the name can be applied and retrieved
	 */
	@Test
	public void testName() {
		Assertions.assertEquals("myName", new BasicBlueprint("myName").getName());
		Assertions.assertEquals("abc123", new BasicBlueprint("abc123").getName());
		Assertions.assertEquals("sdfsdf", new BasicBlueprint("sdfsdf").getName());
		Assertions.assertEquals("124978", new BasicBlueprint("124978").getName());
		Assertions.assertEquals("dfkh?{}:", new BasicBlueprint("dfkh?{}:").getName());
	}
}
