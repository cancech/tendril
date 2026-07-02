package tendril.junit5;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import tendril.bean.Inject;
import tendril.bean.InjectAll;
import tendril.context.ApplicationContext;
import tendril.junit5.beans.EnvBBean;
import tendril.junit5.beans.PropABean;
import tendril.junit5.beans.RandomBean;
import tendril.junit5.beans.TestBean;
import tendril.test.TendrilTest;
import tendril.test.assertions.ClassAssert;
import tendril.test.assertions.CollectionAssert;
import tendril.test.context.TestEngine;

/**
 * Test to ensure that a test can a {@link TendrilTest} can apply environments and properties
 */
@TendrilTest(environments = "B", properties = "A")
public class EnvBPropATest {

	@Inject
	ApplicationContext ctx;
	@Inject
	RandomBean randomBean;
	@InjectAll
	List<TestBean> testBeans;
	@Inject
	EnvBBean envBean;
	@Inject
	PropABean propBean;
	
	@InjectAll
	List<Object> allBeans;
	
	/**
	 * Verify that the beans have been created as expected
	 */
	@Test
	public void testBeansCreated() {
		Assertions.assertNotNull(ctx);
		Assertions.assertNotNull(randomBean);
		Assertions.assertNotNull(allBeans);
		Assertions.assertNotNull(testBeans);
		
		Assertions.assertEquals(4, allBeans.size());
		CollectionAssert.assertEquivalent(allBeans, ctx, randomBean, envBean, propBean);
		CollectionAssert.assertEquivalent(testBeans, envBean, propBean);
		ClassAssert.assertInstance(TestEngine.class, ctx);
	}
}
