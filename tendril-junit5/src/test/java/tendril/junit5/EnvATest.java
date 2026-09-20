package tendril.junit5;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import tendril.bean.Inject;
import tendril.bean.InjectAll;
import tendril.bean.duplicate.Blueprint;
import tendril.context.ApplicationContext;
import tendril.junit5.beans.DuplicateBean;
import tendril.junit5.beans.EnvABean;
import tendril.junit5.beans.RandomBean;
import tendril.junit5.beans.TestBean;
import tendril.junit5.beans.TestBlueprint;
import tendril.test.TendrilTest;
import tendril.test.TestBlueprints;
import tendril.test.TestProperties;
import tendril.test.assertions.ClassAssert;
import tendril.test.assertions.CollectionAssert;
import tendril.test.context.TestEngine;

/**
 * Test to ensure that a test can a {@link TendrilTest} can apply environments
 */
@TendrilTest(environments = "A")
public class EnvATest {
	/** Counter for how many times the getEnvABlueprints() method was called */
	protected static int timesEnvABlueprintsCalled = 0;
	/** Counter for how many times the getEnvABlueprints() method was called */
	protected static int timesEnvAPropertiesCalled = 0;

	/**
	 * Get the blueprint drivers for this test
	 * 
	 * @return {@link List} of {@link Blueprint}s for the test
	 */
	@TestBlueprints
	public static List<Blueprint> getEnvABlueprints() {
		timesEnvABlueprintsCalled++;
		return Arrays.asList(new TestBlueprint("enva_a"), new TestBlueprint("enva_b"));
	}

	/**
	 * Get the properties for this test
	 * 
	 * @return {@link List} of {@link Blueprint}s for the test
	 */
	@TestProperties
	public static Map<String, String> getEnvAProperties() {
		timesEnvAPropertiesCalled++;
		return Map.of("q", "1", "w", "2", "e", "3", "r", "4", "t", "5", "y", "6");
	}

	/**
	 * Reset any/all variables after the conclusion of the test.
	 */
	@AfterEach
	public void reset() {
		timesEnvABlueprintsCalled = 0;
		timesEnvAPropertiesCalled = 0;
	}

	@Inject
	protected ApplicationContext ctx;
	@Inject
	protected RandomBean randomBean;
	@Inject
	protected TestBean testBean;
	@InjectAll
	protected List<DuplicateBean> duplicates;

	@InjectAll
	protected List<Object> allBeans;
	
	/**
	 * Get the total number of beans that are expected to be in the application context
	 * @return int the number of beans
	 */
	protected int getExpectedNumBeans() {
		return 6;
	}
	
	/**
	 * Get a list of all of the beans that are expected to be in the application context.
	 * 
	 * @return {@link List} of {@link Object}s representing all of the beans
	 */
	protected List<Object> getExpectedBeans() {
		return Arrays.asList(ctx, randomBean, testBean, new DuplicateBean("enva_a"), new DuplicateBean("enva_b"), ctx);
	}
	
	/**
	 * Get the duplicates that are expected to be present.
	 * 
	 * @return {@link List} of {@link DuplicateBean}s representing the expected duplicates
	 */
	protected List<DuplicateBean> getExpectedDuplicates() {
		return Arrays.asList(new DuplicateBean("enva_a"), new DuplicateBean("enva_b"));
	}
	
	/**
	 * Get the properties map that is expected to be present
	 * 
	 * @return {@link Map} of {@link String} to {@link String} property mappings that are expected to be present.
	 */
	protected Map<String, String> getExpectedProperties() {
		return getEnvAProperties();
	}

	/**
	 * Verify that the beans have been created as expected.
	 */
	@Test
	public void testBeansCreated() {
		Assertions.assertEquals(1, timesEnvABlueprintsCalled);
		Assertions.assertEquals(1, timesEnvAPropertiesCalled);

		Assertions.assertNotNull(ctx);
		Assertions.assertNotNull(randomBean);
		Assertions.assertNotNull(allBeans);
		Assertions.assertNotNull(testBean);

		Assertions.assertEquals(getExpectedNumBeans(), allBeans.size());
		CollectionAssert.assertEquivalent(getExpectedBeans(), allBeans);
		CollectionAssert.assertEquivalent(getExpectedDuplicates(), duplicates);
		ClassAssert.assertInstance(TestEngine.class, ctx);
		ClassAssert.assertInstance(EnvABean.class, testBean);
	}
	
	/**
	 * Verify that the system properties have been applied.
	 */
	@Test
	public void testPropertiesApplied() {
		Assertions.assertEquals(1, timesEnvABlueprintsCalled);
		Assertions.assertEquals(1, timesEnvAPropertiesCalled);
		
		for (Map.Entry<String, String> pair: getExpectedProperties().entrySet())
			Assertions.assertEquals(pair.getValue(), System.getProperty(pair.getKey()));
	}
}
