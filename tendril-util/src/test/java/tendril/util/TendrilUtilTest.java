/*
 * Copyright 2024 Jaroslav Bosak
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://opensource.org/license/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tendril.util;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.helper.StringPair;
import test.AbstractUnitTest;

/**
 * Test case for {@link TendrilUtil}
 */
public class TendrilUtilTest extends AbstractUnitTest {
    
    // Mocks to use for testing
    @Mock
    private Comparator<String> mockComparator;

    /**
     * @see test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        // Not required
    }

    /**
     * Verify that oneOfMany with the default {@link Comparator} works as expected
     */
    @Test
    public void testOneOfMany() {
        // Should pass
        Assertions.assertTrue(TendrilUtil.oneOfMany(StringPair.class, TendrilUtil.class, Boolean.class, StringPair.class, Integer.class));
        Assertions.assertTrue(TendrilUtil.oneOfMany("abc123", "a", "ab", "abc", "abc1", "abc12", "abc123"));
        Assertions.assertTrue(TendrilUtil.oneOfMany(321, 321));
        Assertions.assertTrue(TendrilUtil.oneOfMany(new StringPair("a", "b"), new StringPair("c", "d"), new StringPair("b", "a"), new StringPair("a","b")));
        Assertions.assertTrue(TendrilUtil.oneOfMany((String) null, "abc", "cba", null));
        
        // Should fail
        Assertions.assertFalse(TendrilUtil.oneOfMany(StringPair.class, TendrilUtil.class, Boolean.class, Integer.class));
        Assertions.assertFalse(TendrilUtil.oneOfMany("abc123", "a", "ab", "abc", "abc1", "abc12", "abc1234"));
        Assertions.assertFalse(TendrilUtil.oneOfMany(321, 123, 234, 456, 567, 678, 789));
        Assertions.assertFalse(TendrilUtil.oneOfMany(new StringPair("a", "b"), new StringPair("c", "d"), new StringPair("b", "a")));
        Assertions.assertFalse(TendrilUtil.oneOfMany((String) null, "abc", "cba"));
        Assertions.assertFalse(TendrilUtil.oneOfMany("abc", "cba", null));
    }
    
    /**
     * Verify that oneOfMany with a custom {@link Comparator} works as expected
     */
    @Test
    public void testOneOfManyWithComparator() {
        // When it fails
        when(mockComparator.compare("abc", "abc")).thenReturn(false);
        when(mockComparator.compare("abc", "123")).thenReturn(false);
        when(mockComparator.compare("abc", "abc123")).thenReturn(false);
        Assertions.assertFalse(TendrilUtil.oneOfMany(mockComparator, "abc", "abc", "123", "abc123"));
        verify(mockComparator).compare("abc", "abc");
        verify(mockComparator).compare("abc", "123");
        verify(mockComparator).compare("abc", "abc123");
        // When it fails
        when(mockComparator.compare("abc", "123")).thenReturn(true);
        Assertions.assertTrue(TendrilUtil.oneOfMany(mockComparator, "abc", "abc", "123", "abc123"));
        verify(mockComparator, times(2)).compare("abc", "abc");
        verify(mockComparator, times(2)).compare("abc", "123");
        verify(mockComparator, times(1)).compare("abc", "abc123"); // Never called a second time
    }
}
