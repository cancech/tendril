package tendril.junit5;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;

import tendril.bean.duplicate.Blueprint;
import tendril.junit5.beans.TestBlueprint;
import tendril.test.TestBlueprints;
import tendril.test.TestProperties;

/**
 * Test to ensure that an abstract parent/middle test can be incorporated
 */
public abstract class AbstractTest extends EnvATest {
	/** Counter for how many times the getAbstractBlueprints() method was called */
	protected static int timesAbstractBlueprintsCalled = 0;
	/** Counter for how many times the getAbstractProperties() method was called */
	protected static int timesAbstractPropertiesCalled = 0;

	/**
	 * Get the blueprint drivers for this test
	 * 
	 * @return {@link List} of {@link Blueprint}s for the test
	 */
	@TestBlueprints
	public static List<Blueprint> getAbstractBlueprints() {
		timesAbstractBlueprintsCalled++;
		return Arrays.asList(new TestBlueprint("abs1"), new TestBlueprint("abs2"));
	}
	
	/**
	 * Get the system properties that should be applied for this test
	 * 
	 * @return {@link Map} of {@link String} to {@link String} defining the properties
	 */
	@TestProperties
	public static Map<String, String> getAbstractProperties() {
		timesAbstractPropertiesCalled++;
		return Map.of("1", "a", "2", "b");
	}
	
	/**
	 * @see tendril.junit5.EnvATest#getExpectedProperties()
	 */
	@Override
	protected Map<String, String> getExpectedProperties() {
		Map<String, String> props = new HashMap<>(); 
		props.putAll(super.getExpectedProperties());
		props.putAll(getAbstractProperties());
		return props;
	}

	/**
	 * Reset any/all variables after the conclusion of the test.
	 */
	@AfterEach
	@Override
	public void reset() {
		super.reset();
		timesAbstractBlueprintsCalled = 0;
		timesAbstractPropertiesCalled = 0;
	}
}
