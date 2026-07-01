package tendril.test.context;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import tendril.TendrilStartupException;
import tendril.test.AbstractUnitTest;
import tendril.test.assertions.ClassAssert;
import tendril.test.assertions.CollectionAssert;

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
	
	/**
	 * Verify that custom properties can be added
	 */
	@Test
	public void testCustomProperties() {
		// Verify the initial list
		CollectionAssert.assertEquivalent(getExpectedProperties(), engine.systemPropertyList());
		
		// Add one item
		engine.setProperties("A");
		CollectionAssert.assertEquivalent(getExpectedProperties("A"), engine.systemPropertyList());
		
		// Add two items
		engine.setProperties("B", "C");
		CollectionAssert.assertEquivalent(getExpectedProperties("B", "C"), engine.systemPropertyList());
		
		// Add more items
		engine.setProperties("1", "2", "3", "4", "5");
		CollectionAssert.assertEquivalent(getExpectedProperties("1", "2", "3", "4", "5"), engine.systemPropertyList());
	}
	
	/**
	 * Builds the list of expected properties (includes the default system properties)
	 * 
	 * @param additional {@link String}... properties that are expected to be added
	 * @return {@link List} of {@link String}s that are expected to be present
	 */
	private List<String> getExpectedProperties(String...additional) {
		List<String> expected = new ArrayList<>();
		for (Object o: System.getProperties().keySet())
			expected.add(o.toString());
		for (String s: additional)
			expected.add(s);
		return expected;
	}
}
