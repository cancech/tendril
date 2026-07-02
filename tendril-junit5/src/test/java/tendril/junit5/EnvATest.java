package tendril.junit5;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import tendril.bean.Inject;
import tendril.bean.InjectAll;
import tendril.bean.duplicate.BlueprintDriver;
import tendril.context.ApplicationContext;
import tendril.junit5.beans.DuplicateBean;
import tendril.junit5.beans.EnvABean;
import tendril.junit5.beans.RandomBean;
import tendril.junit5.beans.TestBean;
import tendril.junit5.beans.TestBlueprint;
import tendril.test.TendrilTest;
import tendril.test.TestBlueprints;
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

	/**
	 * Get the blueprint drivers for this test
	 * 
	 * @return {@link List} of {@link BlueprintDriver}s for the test
	 */
	@TestBlueprints
	public static List<BlueprintDriver> getEnvABlueprints() {
		timesEnvABlueprintsCalled++;
		return Arrays.asList(new TestBlueprint("enva_a"), new TestBlueprint("enva_b"));
	}

	/**
	 * Reset any/all variables after the conclusion of the test.
	 */
	@AfterEach
	public void reset() {
		timesEnvABlueprintsCalled = 0;
	}

	@Inject
	ApplicationContext ctx;
	@Inject
	RandomBean randomBean;
	@Inject
	TestBean testBean;
	@InjectAll
	List<DuplicateBean> duplicates;

	@InjectAll
	List<Object> allBeans;

	/**
	 * Verify that the beans have been created as expected.
	 */
	@Test
	public void testBeansCreated() {
		Assertions.assertEquals(1, timesEnvABlueprintsCalled);

		Assertions.assertNotNull(ctx);
		Assertions.assertNotNull(randomBean);
		Assertions.assertNotNull(allBeans);
		Assertions.assertNotNull(testBean);

		Assertions.assertEquals(5, allBeans.size());
		CollectionAssert.assertEquivalent(allBeans, ctx, randomBean, testBean, new DuplicateBean("enva_a"), new DuplicateBean("enva_b"));
		CollectionAssert.assertEquivalent(duplicates, new DuplicateBean("enva_a"), new DuplicateBean("enva_b"));
		ClassAssert.assertInstance(TestEngine.class, ctx);
		ClassAssert.assertInstance(EnvABean.class, testBean);
	}
}
