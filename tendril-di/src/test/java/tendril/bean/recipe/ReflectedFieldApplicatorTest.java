package tendril.bean.recipe;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import tendril.TendrilStartupException;
import tendril.bean.Inject;
import tendril.bean.InjectAll;

/**
 * Test case for {@link ReflectedFieldApplicator}
 */
public class ReflectedFieldApplicatorTest {

	private class TestBean {
		@Inject
		Integer a = -1;
		@InjectAll
		Integer b = -1;
	}
	
	/**
	 * Verify injection works as expected
	 */
	@Test
	public void testInject() {
		TestBean bean = new TestBean();
		assertBean(-1, -1, bean);
		new ReflectedFieldApplicator<>("path", "a").apply(bean, 1);
		assertBean(1, -1, bean);
		Assertions.assertThrows(TendrilStartupException.class, () -> new ReflectedFieldApplicator<>("path", "b").apply(bean, 2));
		assertBean(1, -1, bean);
	}
	
	private void assertBean(int expectedA, int expectedB, TestBean actual) {
		Assertions.assertEquals(expectedA, actual.a);
		Assertions.assertEquals(expectedB, actual.b);
	}
}
