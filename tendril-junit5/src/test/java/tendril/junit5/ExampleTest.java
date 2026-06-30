package tendril.junit5;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import tendril.bean.Inject;
import tendril.bean.InjectAll;
import tendril.context.ApplicationContext;
import tendril.test.TendrilTest;
import tendril.test.assertions.ClassAssert;
import tendril.test.assertions.CollectionAssert;
import tendril.test.context.TestEngine;

/**
 * Test to ensure that a test can a {@link TendrilTest} can be executed
 */
@TendrilTest
@TestInstance(Lifecycle.PER_CLASS)
public class ExampleTest {

	@Inject
	ApplicationContext ctx;
	@Inject
	RandomBean randomBean;
	
	@InjectAll
	List<Object> allBeans;
	
	@Test
	public void testBeansCreated() {
		Assertions.assertNotNull(ctx);
		Assertions.assertNotNull(randomBean);
		Assertions.assertNotNull(allBeans);
		
		Assertions.assertEquals(2, allBeans.size());
		CollectionAssert.assertEquivalent(allBeans, ctx, randomBean);
		ClassAssert.assertInstance(TestEngine.class, ctx);
	}
}
