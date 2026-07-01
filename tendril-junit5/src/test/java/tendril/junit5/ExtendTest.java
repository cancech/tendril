package tendril.junit5;

import org.junit.jupiter.api.Test;

import tendril.junit5.beans.EnvABean;
import tendril.test.assertions.ClassAssert;

/**
 * Test to ensure that the test runs properly when the test details are specified on a parent test class
 */
public class ExtendTest extends EnvATest {
	
	/**
	 * Verify the details from the parent are correct
	 */
	@Test
	public void testExtensionRunsProperly() {
		ClassAssert.assertInstance(EnvABean.class, envBean);
	}

}
