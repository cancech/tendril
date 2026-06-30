package tendril.test.context;

import java.lang.reflect.InvocationTargetException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import tendril.TendrilStartupException;
import tendril.test.AbstractUnitTest;
import tendril.test.assertions.ClassAssert;

/**
 * Test case for {@link TestEngine}
 */
public class TestEngineTest extends AbstractUnitTest {

	// Instance to test
	private TestEngine engine;

	/**
	 * @see tendril.test.AbstractUnitTest#prepareTest()
	 */
	@Override
	protected void prepareTest() {
		engine = new TestEngine(DummyClass.class);
	}

	/**
	 * Verify that the identified test class can be created and retrieved
	 */
	@Test
	public void testGetTestRunner() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, SecurityException, ClassNotFoundException {
		ClassAssert.assertInstance(DummyClass.class, engine.getTestRunner());
	}

	/**
	 * Verify that start does nothing
	 */
	@Test
	public void testStart() {
		Assertions.assertThrows(TendrilStartupException.class, () -> engine.start());
	}
}
