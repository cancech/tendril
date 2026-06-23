package tendril.bean.recipe;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import tendril.TendrilStartupException;
import tendril.bean.Inject;

/**
 * Test case for {@link ReflectedFieldInjectionHandler}
 */
public class ReflectedFieldInjectionHandlerTest {

	class Base {
		@Inject
		private Integer a = -1;
		@Deprecated
		Integer b = -1;
		@Deprecated
		String c = null;
	}
	
	class Mid extends Base {
		@Deprecated
		private Integer a = -2;
		@Inject
		private Integer b = -2;
		@Inject
		String c = "";
	}
	
	class Final extends Mid {
		Integer a = -3;
		Integer b = -3;
		String c = "empty";
	}
	
	/**
	 * Verify that the appropriate field is updated.
	 */
	@Test
	public void testInjectBase() {
		// Inject annotated
		final Base i = new Base();
		new ReflectedFieldInjectionHandler<Base>("path", "a", Inject.class).injectBean(i, 123);
		Assertions.assertThrows(TendrilStartupException.class, () -> new ReflectedFieldInjectionHandler<Base>("path", "b", Inject.class).injectBean(i, 123));
		Assertions.assertThrows(TendrilStartupException.class, () -> new ReflectedFieldInjectionHandler<Base>("path", "c", Inject.class).injectBean(i, "abc"));
		assertBase(123, -1, null, i);

		// Deprecated annotated
		final Base d = new Base();
		Assertions.assertThrows(TendrilStartupException.class, () -> new ReflectedFieldInjectionHandler<Base>("path", "a", Deprecated.class).injectBean(d, 123));
		new ReflectedFieldInjectionHandler<Base>("path", "b", Deprecated.class).injectBean(d, 123);
		new ReflectedFieldInjectionHandler<Base>("path", "c", Deprecated.class).injectBean(d, "abc");
		assertBase(-1, 123, "abc", d);
	}
	
	/**
	 * Verify that the appropriate field is updated.
	 */
	@Test
	public void testInjectMid() {
		// Inject annotated
		final Mid i = new Mid();
		new ReflectedFieldInjectionHandler<Base>("path", "a", Inject.class).injectBean(i, 123);
		new ReflectedFieldInjectionHandler<Base>("path", "b", Inject.class).injectBean(i, 123);
		new ReflectedFieldInjectionHandler<Base>("path", "c", Inject.class).injectBean(i, "abc");
		assertBase(123, -1, null, i);
		assertMid(-2, 123, "abc", i);

		// Deprecated annotated
		final Mid d = new Mid();
		new ReflectedFieldInjectionHandler<Base>("path", "a", Deprecated.class).injectBean(d, 123);
		new ReflectedFieldInjectionHandler<Base>("path", "b", Deprecated.class).injectBean(d, 123);
		new ReflectedFieldInjectionHandler<Base>("path", "c", Deprecated.class).injectBean(d, "abc");
		assertBase(-1, 123, "abc", d);
		assertMid(123, -2, "", d);
	}
	
	/**
	 * Verify that the appropriate field is updated.
	 */
	@Test
	public void testInjectFinal() {
		// Inject annotated
		final Final i = new Final();
		new ReflectedFieldInjectionHandler<Base>("path", "a", Inject.class).injectBean(i, 123);
		new ReflectedFieldInjectionHandler<Base>("path", "b", Inject.class).injectBean(i, 123);
		new ReflectedFieldInjectionHandler<Base>("path", "c", Inject.class).injectBean(i, "abc");
		assertBase(123, -1, null, i);
		assertMid(-2, 123, "abc", i);
		assertFinal(-3, -3, "empty", i);

		// Deprecated annotated
		final Final d = new Final();
		new ReflectedFieldInjectionHandler<Base>("path", "a", Deprecated.class).injectBean(d, 123);
		new ReflectedFieldInjectionHandler<Base>("path", "b", Deprecated.class).injectBean(d, 123);
		new ReflectedFieldInjectionHandler<Base>("path", "c", Deprecated.class).injectBean(d, "abc");
		assertBase(-1, 123, "abc", d);
		assertFinal(-3, -3, "empty", d);
	}
	
	private void assertBase(int expectedA, int expectedB, String expectedC, Base actual) {
		Assertions.assertEquals(expectedA, actual.a);
		Assertions.assertEquals(expectedB, actual.b);
		Assertions.assertEquals(expectedC, actual.c);
	}
	
	private void assertMid(int expectedA, int expectedB, String expectedC, Mid actual) {
		Assertions.assertEquals(expectedA, actual.a);
		Assertions.assertEquals(expectedB, actual.b);
		Assertions.assertEquals(expectedC, actual.c);
	}
	
	private void assertFinal(int expectedA, int expectedB, String expectedC, Final actual) {
		Assertions.assertEquals(expectedA, actual.a);
		Assertions.assertEquals(expectedB, actual.b);
		Assertions.assertEquals(expectedC, actual.c);
	}
}
