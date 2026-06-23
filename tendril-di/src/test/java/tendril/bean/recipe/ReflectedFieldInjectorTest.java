package tendril.bean.recipe;

import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.TendrilStartupException;
import tendril.bean.Inject;
import tendril.bean.InjectAll;
import tendril.bean.qualifier.Descriptor;
import tendril.context.Engine;
import tendril.test.AbstractUnitTest;

/**
 * Test case for {@link ReflectedFieldInjector}
 */
public class ReflectedFieldInjectorTest extends AbstractUnitTest{

	private class TestBean {
		@Inject
		List<Integer> a = mockInitialList;
		@InjectAll
		List<Integer> b = mockInitialList;
	}
	
	// Mocks to use for testing
	@Mock
	private Descriptor<Integer> mockDescriptor;
	@Mock
	private Engine mockEngine;
	@Mock
	private List<Integer> mockInitialList;
	@Mock
	private List<Integer> mockListA;
	@Mock
	private List<Integer> mockListB;
	
	// Instance to test
	private TestBean bean;

	@Override
	protected void prepareTest() {
		bean = new TestBean();		
	}
	
	/**
	 * Verify injection works as expected
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testInject() {
		when(mockEngine.getAllBeans(mockDescriptor)).thenReturn(mockListA, mockListB);
		assertBean(mockInitialList, mockInitialList, bean);
		Assertions.assertThrows(TendrilStartupException.class, () -> new ReflectedFieldInjector<>("path", "a", mockDescriptor).inject(bean, mockEngine));
		assertBean(mockInitialList, mockInitialList, bean);
		new ReflectedFieldInjector<>("path", "b", mockDescriptor).inject(bean, mockEngine);
		assertBean(mockInitialList, mockListB, bean);
	}
	
	private void assertBean(List<Integer> expectedA, List<Integer> expectedB, TestBean actual) {
		Assertions.assertEquals(expectedA, actual.a);
		Assertions.assertEquals(expectedB, actual.b);
	}
}
