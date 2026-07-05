package tendril.junit5;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import tendril.bean.duplicate.Blueprint;
import tendril.junit5.beans.DuplicateBean;
import tendril.junit5.beans.EnvABean;
import tendril.junit5.beans.TestBlueprint;
import tendril.test.TestBlueprints;
import tendril.test.assertions.ClassAssert;
import tendril.test.assertions.CollectionAssert;

/**
 * Test to ensure that the test runs properly when the test details are specified on a parent test class
 */
public class ExtendTest extends EnvATest {
	/** Counter for the number of times that getEnvABlueprints() has been called */
	protected static int timesExtendBlueprintsCalled = 0;

	/**
	 * Get the blueprint drivers for this test
	 * 
	 * @return {@link List} of {@link Blueprint}s for the test
	 */
	@TestBlueprints
	public static List<Blueprint> getEnvABlueprints() {
		timesExtendBlueprintsCalled++;
		return Arrays.asList(new TestBlueprint("extend_1"), new TestBlueprint("extend_2"));
	}

	/**
	 * @see tendril.junit5.EnvATest#reset()
	 */
	@AfterEach
	@Override
	public void reset() {
		super.reset();
		timesExtendBlueprintsCalled = 0;
	}
	
	/**
	 * @see tendril.junit5.EnvATest#getExpectedNumBeans()
	 */
	@Override
	protected int getExpectedNumBeans() {
		return 7;
	}
	
	/**
	 * @see tendril.junit5.EnvATest#getExpectedBeans()
	 */
	@Override
	protected List<Object> getExpectedBeans() {
		return Arrays.asList(ctx, randomBean, testBean, new DuplicateBean("enva_a"), new DuplicateBean("enva_b"), new DuplicateBean("extend_1"), new DuplicateBean("extend_2"));
	}
	
	/**
	 * @see tendril.junit5.EnvATest#getExpectedDuplicates()
	 */
	@Override
	protected List<DuplicateBean> getExpectedDuplicates() {
		return Arrays.asList(new DuplicateBean("enva_a"), new DuplicateBean("enva_b"), new DuplicateBean("extend_1"), new DuplicateBean("extend_2"));
	}

	/**
	 * Verify that the beans have been created as expected
	 */
	@Test
	public void testExtensionRunsProperly() {
		Assertions.assertEquals(1, timesEnvABlueprintsCalled);
		Assertions.assertEquals(1, timesExtendBlueprintsCalled);
		ClassAssert.assertInstance(EnvABean.class, testBean);
		CollectionAssert.assertEquivalent(getExpectedBeans(), allBeans);
		CollectionAssert.assertEquivalent(getExpectedDuplicates(), duplicates);
	}

}
