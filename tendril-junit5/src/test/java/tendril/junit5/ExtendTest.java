package tendril.junit5;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import tendril.bean.duplicate.Blueprint;
import tendril.junit5.beans.DuplicateBean;
import tendril.junit5.beans.EnvABean;
import tendril.junit5.beans.TestBlueprint;
import tendril.test.TestBlueprints;
import tendril.test.TestProperties;
import tendril.test.assertions.ClassAssert;
import tendril.test.assertions.CollectionAssert;

/**
 * Test to ensure that the test runs properly when the test details are specified on a parent test class
 */
public class ExtendTest extends AbstractTest {
	/** Counter for the number of times that getEnvABlueprints() has been called */
	protected static int timesExtendBlueprintsCalled = 0;
	/** Counter for the number of times that getEnvAProperties() has been called */
	protected static int timesExtendPropertiesCalled = 0;

	/**
	 * Get the blueprint drivers for this test
	 * 
	 * @return {@link List} of {@link Blueprint}s for the test
	 */
	@TestBlueprints
	public static List<Blueprint> getExtendBlueprints() {
		timesExtendBlueprintsCalled++;
		return Arrays.asList(new TestBlueprint("extend_1"), new TestBlueprint("extend_2"));
	}

	/**
	 * Get the properties for this test
	 * 
	 * @return {@link Map} of {@link String} to {@link String} properties for the test
	 */
	@TestProperties
	public static Map<String, String> getExtendProperties() {
		timesExtendPropertiesCalled++;
		return Map.of("abc", "123");
	}
	
	/**
	 * @see tendril.junit5.EnvATest#getExpectedProperties()
	 */
	@Override
	protected Map<String, String> getExpectedProperties() {
		Map<String, String> props = new HashMap<>(); 
		props.putAll(super.getExpectedProperties());
		props.putAll(getExtendProperties());
		return props;
	}

	/**
	 * @see tendril.junit5.EnvATest#reset()
	 */
	@AfterEach
	@Override
	public void reset() {
		super.reset();
		timesExtendBlueprintsCalled = 0;
		timesExtendPropertiesCalled = 0;
	}
	
	/**
	 * @see tendril.junit5.EnvATest#getExpectedNumBeans()
	 */
	@Override
	protected int getExpectedNumBeans() {
		return 10;
	}
	
	/**
	 * @see tendril.junit5.EnvATest#getExpectedBeans()
	 */
	@Override
	protected List<Object> getExpectedBeans() {
		return Arrays.asList(ctx, randomBean, testBean, new DuplicateBean("enva_a"), new DuplicateBean("enva_b"), new DuplicateBean("extend_1"), new DuplicateBean("extend_2"), new DuplicateBean("abs1"), new DuplicateBean("abs2"), ctx);
	}
	
	/**
	 * @see tendril.junit5.EnvATest#getExpectedDuplicates()
	 */
	@Override
	protected List<DuplicateBean> getExpectedDuplicates() {
		return Arrays.asList(new DuplicateBean("enva_a"), new DuplicateBean("enva_b"), new DuplicateBean("extend_1"), new DuplicateBean("extend_2"), new DuplicateBean("abs1"), new DuplicateBean("abs2"));
	}

	/**
	 * Verify that the beans have been created as expected
	 */
	@Test
	public void testExtensionRunsProperly() {
		Assertions.assertEquals(1, timesEnvABlueprintsCalled);
		Assertions.assertEquals(1, timesAbstractBlueprintsCalled);
		Assertions.assertEquals(1, timesExtendBlueprintsCalled);

		Assertions.assertEquals(1, timesEnvAPropertiesCalled);
		Assertions.assertEquals(1, timesAbstractPropertiesCalled);
		Assertions.assertEquals(1, timesExtendPropertiesCalled);
		
		ClassAssert.assertInstance(EnvABean.class, testBean);
		CollectionAssert.assertEquivalent(getExpectedBeans(), allBeans);
		CollectionAssert.assertEquivalent(getExpectedDuplicates(), duplicates);
	}

}
