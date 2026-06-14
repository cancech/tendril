package tendril.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.test.AbstractUnitTest;

/**
 * Test case for {@link CollectionUtil}
 */
public class CollectionUtilTest extends AbstractUnitTest {

	// Mocks to use for testing
	@Mock
	private Object mockObj1;
	@Mock
	private Object mockObj2;
	@Mock
	private Object mockObj3;
	@Mock
	private Object mockObj4;
	
	/**
	 * @see tendril.test.AbstractUnitTest#prepareTest()
	 */
	@Override
	protected void prepareTest() {
		// Not required
	}

	/**
	 * Verify {@link Collection} sizes can be compared
	 */
	@Test
	public void testSameSize() {
		Assertions.assertTrue(CollectionUtil.sameSize(Collections.emptyList(), Collections.emptyList()));
		Assertions.assertTrue(CollectionUtil.sameSize(Collections.singletonList(mockObj1), Collections.singletonList("abc")));
		Assertions.assertTrue(CollectionUtil.sameSize(Collections.singletonList(1), Collections.singletonList(mockObj2)));
		Assertions.assertTrue(CollectionUtil.sameSize(Collections.singletonList(mockObj3), Collections.singletonList(1.45f)));
		Assertions.assertTrue(CollectionUtil.sameSize(Collections.singletonList(false), Collections.singletonList(mockObj4)));
		Assertions.assertTrue(CollectionUtil.sameSize(Arrays.asList(mockObj1, mockObj2, mockObj3, mockObj4), Arrays.asList("a", "b", "c", "d")));
		Assertions.assertTrue(CollectionUtil.sameSize(Arrays.asList(1, 2, 5, 8), Arrays.asList(mockObj1, mockObj2, mockObj3, mockObj4)));

		Assertions.assertFalse(CollectionUtil.sameSize(Collections.emptyList(), Collections.singletonList("abc")));
		Assertions.assertFalse(CollectionUtil.sameSize(Collections.singletonList(mockObj1), Collections.emptyList()));
		Assertions.assertFalse(CollectionUtil.sameSize(Collections.emptyList(), Collections.singletonList(mockObj2)));
		Assertions.assertFalse(CollectionUtil.sameSize(Arrays.asList(mockObj3, mockObj4), Arrays.asList(mockObj1, mockObj2, mockObj3, mockObj4)));
		Assertions.assertFalse(CollectionUtil.sameSize(Arrays.asList(mockObj1, mockObj2, mockObj3, mockObj4), Arrays.asList(mockObj3, mockObj4)));
		Assertions.assertFalse(CollectionUtil.sameSize(Arrays.asList(mockObj1, mockObj2, mockObj3, mockObj4), Arrays.asList("a", "b", "c", "d", "e", "f")));
		Assertions.assertFalse(CollectionUtil.sameSize(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9), Arrays.asList(mockObj1, mockObj2, mockObj3, mockObj4)));
	}
	
	/**
	 * Verify that the equivalence of arrays can be determined
	 */
	@Test
	public void testEquivalent() {
		Assertions.assertTrue(CollectionUtil.equivalent(Collections.emptyList(), Collections.emptyList()));
		
		Assertions.assertTrue(CollectionUtil.equivalent(Collections.singletonList(mockObj1), Collections.singletonList(mockObj1)));
		Assertions.assertTrue(CollectionUtil.equivalent(Collections.singletonList(mockObj2), Collections.singletonList(mockObj2)));
		Assertions.assertTrue(CollectionUtil.equivalent(Collections.singletonList(mockObj3), Collections.singletonList(mockObj3)));
		Assertions.assertTrue(CollectionUtil.equivalent(Collections.singletonList(mockObj4), Collections.singletonList(mockObj4)));
		Assertions.assertTrue(CollectionUtil.equivalent(Collections.singletonList("abc"), Collections.singletonList("abc")));
		Assertions.assertTrue(CollectionUtil.equivalent(Collections.singletonList(123), Collections.singletonList(123)));

		Assertions.assertTrue(CollectionUtil.equivalent(Arrays.asList(mockObj1, mockObj2, mockObj3, mockObj4), Arrays.asList(mockObj1, mockObj2, mockObj3, mockObj4)));
		Assertions.assertTrue(CollectionUtil.equivalent(Arrays.asList(mockObj4, mockObj3, mockObj2, mockObj1), Arrays.asList(mockObj1, mockObj2, mockObj3, mockObj4)));
		Assertions.assertTrue(CollectionUtil.equivalent(Arrays.asList(mockObj1, mockObj2, mockObj3, mockObj4), Arrays.asList(mockObj4, mockObj3, mockObj2, mockObj1)));
		Assertions.assertTrue(CollectionUtil.equivalent(Arrays.asList("a", "b", "c", "d"), Arrays.asList("a", "b", "c", "d")));
		Assertions.assertTrue(CollectionUtil.equivalent(Arrays.asList("d", "c", "b", "a"), Arrays.asList("a", "b", "c", "d")));
		Assertions.assertTrue(CollectionUtil.equivalent(Arrays.asList("a", "b", "c", "d"), Arrays.asList("d", "c", "b", "a")));
		Assertions.assertTrue(CollectionUtil.equivalent(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9), Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9)));
		Assertions.assertTrue(CollectionUtil.equivalent(Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1), Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9)));
		Assertions.assertTrue(CollectionUtil.equivalent(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9), Arrays.asList(9, 8, 7, 6, 5, 4, 3, 2, 1)));

		Assertions.assertTrue(CollectionUtil.equivalent(Arrays.asList(mockObj1, mockObj1, mockObj1, mockObj1), Arrays.asList(mockObj1, mockObj1, mockObj1, mockObj1)));
		Assertions.assertTrue(CollectionUtil.equivalent(Arrays.asList("a", "a", "a", "a"), Arrays.asList("a", "a", "a", "a")));
		Assertions.assertTrue(CollectionUtil.equivalent(Arrays.asList(1, 1, 1, 1, 1, 1), Arrays.asList(1, 1, 1, 1, 1, 1)));
	}
	
	/**
	 * Verify that the equivalence of arrays can be determined
	 */
	@Test
	public void testNotEquivalent() {
		Assertions.assertFalse(CollectionUtil.equivalent(Collections.emptyList(), Collections.singletonList(mockObj1)));
		Assertions.assertFalse(CollectionUtil.equivalent(Collections.singletonList(mockObj1), Collections.emptyList()));

		Assertions.assertFalse(CollectionUtil.equivalent(Collections.singletonList(mockObj1), Collections.singletonList(mockObj4)));
		Assertions.assertFalse(CollectionUtil.equivalent(Collections.singletonList(mockObj2), Collections.singletonList(mockObj3)));
		Assertions.assertFalse(CollectionUtil.equivalent(Collections.singletonList(mockObj3), Collections.singletonList(mockObj2)));
		Assertions.assertFalse(CollectionUtil.equivalent(Collections.singletonList(mockObj4), Collections.singletonList(mockObj1)));
		Assertions.assertFalse(CollectionUtil.equivalent(Collections.singletonList("abc"), Collections.singletonList("cba")));
		Assertions.assertFalse(CollectionUtil.equivalent(Collections.singletonList(123), Collections.singletonList(321)));

		Assertions.assertFalse(CollectionUtil.equivalent(Arrays.asList(mockObj1, mockObj2, mockObj3, mockObj4), Arrays.asList(mockObj1, mockObj2, mockObj3)));
		Assertions.assertFalse(CollectionUtil.equivalent(Arrays.asList(mockObj1, mockObj2, mockObj3), Arrays.asList(mockObj1, mockObj2, mockObj3, mockObj4)));
		Assertions.assertFalse(CollectionUtil.equivalent(Arrays.asList(mockObj1, mockObj1, mockObj1, mockObj1), Arrays.asList(mockObj1, mockObj1, mockObj2, mockObj1)));
		Assertions.assertFalse(CollectionUtil.equivalent(Arrays.asList(mockObj1, mockObj2), Arrays.asList(mockObj3, mockObj4)));
		Assertions.assertFalse(CollectionUtil.equivalent(Arrays.asList("a", "b", "c", "d"), Arrays.asList()));
		Assertions.assertFalse(CollectionUtil.equivalent(Arrays.asList("a", "b"), Arrays.asList("a", "b", "c", "d")));
		Assertions.assertFalse(CollectionUtil.equivalent(Arrays.asList("a", "a", "a", "a"), Arrays.asList("b", "a", "a", "a")));
		Assertions.assertFalse(CollectionUtil.equivalent(Arrays.asList("b", "a", "a", "a"), Arrays.asList("a", "a", "a", "a")));
		Assertions.assertFalse(CollectionUtil.equivalent(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9), Arrays.asList(1)));
		Assertions.assertFalse(CollectionUtil.equivalent(Arrays.asList(1), Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9)));
		Assertions.assertFalse(CollectionUtil.equivalent(Arrays.asList(1, 1, 1, 1, 1, 1), Arrays.asList(1, 1, 2, 1, 1, 1)));
		Assertions.assertFalse(CollectionUtil.equivalent(Arrays.asList(1, 1, 1, 1, 2, 1), Arrays.asList(1, 1, 3, 1, 1, 1)));
	}
}
