package tendril.processor;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import tendril.bean.Bean;

/**
 * Test case for {@link AbstractBeanProcessor}
 */
public class AbstractBeanProcessorTest {

	/**
	 * Verify the overrides are correct
	 */
	@Test
	public void testOverrides() {
		AbstractBeanProcessor processor = new AbstractBeanProcessor(Bean.class) {
		};

		Assertions.assertNull(processor.getClassOverrideType());
		Assertions.assertNull(processor.getMethodOverrideType());
	}
}
