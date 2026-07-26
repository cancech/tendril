package tendril.junit5;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;

import tendril.bean.duplicate.Blueprint;
import tendril.junit5.beans.TestBlueprint;
import tendril.test.TestBlueprints;

/**
 * Test to ensure that an abstract parent/middle test can be incorporated
 */
public abstract class AbstractTest extends EnvATest {
	/** Counter for how many times the getAbstractBlueprints() method was called */
	protected static int timesAbstractBlueprintsCalled = 0;

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
	 * Reset any/all variables after the conclusion of the test.
	 */
	@AfterEach
	@Override
	public void reset() {
		super.reset();
		timesAbstractBlueprintsCalled = 0;
	}
}
