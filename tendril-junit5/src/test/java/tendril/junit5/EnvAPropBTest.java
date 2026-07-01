package tendril.junit5;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import tendril.bean.Inject;
import tendril.bean.InjectAll;
import tendril.context.ApplicationContext;
import tendril.junit5.beans.EnvABean;
import tendril.junit5.beans.PropBBean;
import tendril.junit5.beans.RandomBean;
import tendril.junit5.beans.TestBean;
import tendril.test.TendrilTest;
import tendril.test.assertions.ClassAssert;
import tendril.test.assertions.CollectionAssert;
import tendril.test.context.TestEngine;

/**
 * Test to ensure that a test can a {@link TendrilTest} can apply environments and properties
 */
@TendrilTest(environments = "A", properties = "B")
public class EnvAPropBTest {

	@Inject
	ApplicationContext ctx;
	@Inject
	RandomBean randomBean;
	@InjectAll
	List<TestBean> testBeans;
	@Inject
	EnvABean envBean;
	@Inject
	PropBBean propBean;
	
	@InjectAll
	List<Object> allBeans;
	
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
