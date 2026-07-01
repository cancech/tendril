package tendril.junit5;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import tendril.bean.Inject;
import tendril.bean.InjectAll;
import tendril.context.ApplicationContext;
import tendril.junit5.beans.EnvABean;
import tendril.junit5.beans.EnvBean;
import tendril.junit5.beans.RandomBean;
import tendril.test.TendrilTest;
import tendril.test.assertions.ClassAssert;
import tendril.test.assertions.CollectionAssert;
import tendril.test.context.TestEngine;

/**
 * Test to ensure that a test can a {@link TendrilTest} can apply environments
 */
@TendrilTest(environments = "A")
public class EnvATest {

	@Inject
	ApplicationContext ctx;
	@Inject
	RandomBean randomBean;
	@Inject
	EnvBean envBean;
	
	@InjectAll
	List<Object> allBeans;
	
	@Test
	public void testBeansCreated() {
		Assertions.assertNotNull(ctx);
		Assertions.assertNotNull(randomBean);
		Assertions.assertNotNull(allBeans);
		Assertions.assertNotNull(envBean);
		
		Assertions.assertEquals(3, allBeans.size());
		CollectionAssert.assertEquivalent(allBeans, ctx, randomBean, envBean);
		ClassAssert.assertInstance(TestEngine.class, ctx);
		ClassAssert.assertInstance(EnvABean.class, envBean);
	}
}
