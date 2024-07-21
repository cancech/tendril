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
package tendril.test.assertions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

/**
 * Test case for {@link CollectionAssert}
 */
public class CollectionAssertTest {

    /**
     * Verify that assertEmpty properly determines whether a {@link Collection} is empty
     */
    @Test
    public void testAssertEmpty() {
        // Pass
        CollectionAssert.assertEmpty(Collections.emptyList());
        CollectionAssert.assertEmpty(Collections.emptySet());
        CollectionAssert.assertEmpty(new ArrayList<>());
        CollectionAssert.assertEmpty(new HashSet<>());
        
        // Fail
        Assertions.assertThrows(AssertionFailedError.class, () -> CollectionAssert.assertEmpty(Collections.singleton("str")));
        Assertions.assertThrows(AssertionFailedError.class, () -> CollectionAssert.assertEmpty(Collections.singletonList("abc123")));
        Assertions.assertThrows(AssertionFailedError.class, () -> CollectionAssert.assertEmpty(Arrays.asList("a", "b", "c", "d")));

        Set<String> set = new HashSet<>();
        set.add("a");
        set.add("b");
        Assertions.assertThrows(AssertionFailedError.class, () -> CollectionAssert.assertEmpty(set));
    }
    
    /**
     * Verify that the size is properly of collections is properly verified
     */
    @Test
    public void testAssertSize() {
        // Pass
        CollectionAssert.assertSize(Collections.emptyList(), Collections.emptySet());
        CollectionAssert.assertSize(Collections.singleton("str"), Collections.singletonList("abc123"));
        CollectionAssert.assertSize(Arrays.asList("a", "b", "c", "d"), Arrays.asList(1, 2, 3, 4));
        
        // Fail
        Assertions.assertThrows(AssertionFailedError.class, () -> CollectionAssert.assertSize(Collections.emptyList(), Collections.singletonList("abc123")));
        Assertions.assertThrows(AssertionFailedError.class, () -> CollectionAssert.assertSize(Arrays.asList(1, 2, 3, 4), Collections.singleton("abc123")));
        Assertions.assertThrows(AssertionFailedError.class, () -> CollectionAssert.assertSize(Arrays.asList("a", "b", "c", "d"), Collections.emptySet()));
    }
    
    /**
     * Verify that the test for array equality works as expected
     */
    @Test
    public void testAssertEquals() {
        // Pass
        CollectionAssert.assertEquals(Collections.emptyList(), Collections.emptySet());
        CollectionAssert.assertEquals(Collections.emptyList());
        CollectionAssert.<String>assertEquals(Collections.singleton("str"), Collections.singletonList("str"));
        CollectionAssert.<String>assertEquals(Collections.singleton("str"), "str");
        CollectionAssert.<String>assertEquals(Arrays.asList("a", "b", "c", "d"), Arrays.asList("a", "b", "c", "d"));
        CollectionAssert.<String>assertEquals(Arrays.asList("a", "b", "c", "d"), "a", "b", "c", "d");
        CollectionAssert.<Integer>assertEquals(Arrays.asList(1, 2, 3, 4), Arrays.asList(1, 2, 3, 4));
        CollectionAssert.<Integer>assertEquals(Arrays.asList(1, 2, 3, 4), 1, 2, 3, 4);
        
        // Fail
        Assertions.assertThrows(AssertionFailedError.class, () -> CollectionAssert.<String>assertEquals(Collections.emptyList(), Collections.singletonList("abc123")));
        Assertions.assertThrows(AssertionFailedError.class, () -> CollectionAssert.<String>assertEquals(Collections.emptyList(), "abc123"));
        Assertions.assertThrows(AssertionFailedError.class, () -> CollectionAssert.<String>assertEquals(Collections.singletonList("abc123"), Collections.emptyList()));
        Assertions.assertThrows(AssertionFailedError.class, () -> CollectionAssert.<String>assertEquals(Collections.singletonList("abc123")));
        Assertions.assertThrows(AssertionFailedError.class, () -> CollectionAssert.<Object>assertEquals(Arrays.asList("a", "b", "c", "d"), Arrays.asList(1, 2, 3, 4)));
        Assertions.assertThrows(AssertionFailedError.class, () -> CollectionAssert.<Object>assertEquals(Arrays.asList("a", "b", "c", "d"), 1, 2, 3, 4));
        Assertions.assertThrows(AssertionFailedError.class, () -> CollectionAssert.<Object>assertEquals(Arrays.asList("a", "b", "c", "d"), Arrays.asList("a", "d", "b", "c")));
        Assertions.assertThrows(AssertionFailedError.class, () -> CollectionAssert.<Object>assertEquals(Arrays.asList("a", "b", "c", "d"), "a", "d", "b", "c"));
        
        Set<String> set = new HashSet<>();
        set.add("d");
        set.add("c");
        set.add("b");
        set.add("a");
        Assertions.assertThrows(AssertionFailedError.class, () -> CollectionAssert.assertEquals(set, "d", "c", "b", "a"));
    }
    
    /**
     * Verify that test for array equivalence works as expected
     */
    @Test
    public void testAssertEquivalent() {
        // Pass
        CollectionAssert.assertEquivalent(Collections.emptyList(), Collections.emptySet());
        CollectionAssert.assertEquivalent(Collections.emptyList());
        CollectionAssert.<String>assertEquivalent(Collections.singleton("str"), Collections.singletonList("str"));
        CollectionAssert.<String>assertEquivalent(Collections.singleton("str"), "str");
        CollectionAssert.<String>assertEquivalent(Arrays.asList("a", "b", "c", "d"), Arrays.asList("a", "b", "c", "d"));
        CollectionAssert.<String>assertEquivalent(Arrays.asList("a", "b", "c", "d"), Arrays.asList("a", "d", "b", "c"));
        CollectionAssert.<String>assertEquivalent(Arrays.asList("a", "b", "c", "d"), "a", "b", "c", "d");
        CollectionAssert.<String>assertEquivalent(Arrays.asList("a", "b", "c", "d"), "a", "d", "b", "c");
        CollectionAssert.<Integer>assertEquivalent(Arrays.asList(1, 2, 3, 4), Arrays.asList(1, 2, 3, 4));
        CollectionAssert.<Integer>assertEquivalent(Arrays.asList(1, 2, 3, 4), Arrays.asList(1, 3, 2, 4));
        CollectionAssert.<Integer>assertEquivalent(Arrays.asList(1, 2, 3, 4), 1, 2, 3, 4);
        CollectionAssert.<Integer>assertEquivalent(Arrays.asList(1, 2, 3, 4), 3, 1, 2, 4);
        
        Set<String> set = new HashSet<>();
        set.add("d");
        set.add("c");
        set.add("b");
        set.add("a");
        CollectionAssert.assertEquivalent(set, "d", "c", "b", "a");
        
        // Fail
        Assertions.assertThrows(AssertionFailedError.class, () -> CollectionAssert.<String>assertEquivalent(Collections.emptyList(), Collections.singletonList("str")));
        Assertions.assertThrows(AssertionFailedError.class, () -> CollectionAssert.<String>assertEquivalent(Collections.emptyList(), "str"));
        Assertions.assertThrows(AssertionFailedError.class, () -> CollectionAssert.<String>assertEquivalent(Collections.singletonList("str"), Collections.emptyList()));
        Assertions.assertThrows(AssertionFailedError.class, () -> CollectionAssert.<String>assertEquivalent(Collections.singletonList("str"), Collections.singleton("rts")));
        Assertions.assertThrows(AssertionFailedError.class, () -> CollectionAssert.<String>assertEquivalent(Collections.singletonList("str"), "rts"));
        Assertions.assertThrows(AssertionFailedError.class, () -> CollectionAssert.<String>assertEquivalent(Collections.singletonList("str"), Arrays.asList("str", "str", "str")));
        Assertions.assertThrows(AssertionFailedError.class, () -> CollectionAssert.<String>assertEquivalent(Collections.singletonList("str"), "str", "str", "str"));
        Assertions.assertThrows(AssertionFailedError.class, () -> CollectionAssert.<String>assertEquivalent(Arrays.asList("str", "abc"), Arrays.asList("str", "str", "str")));
        Assertions.assertThrows(AssertionFailedError.class, () -> CollectionAssert.<String>assertEquivalent(Arrays.asList("str", "abc"), "str", "str"));
        
    }
}
