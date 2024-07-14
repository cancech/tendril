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
package test.assertions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.junit.jupiter.api.Assertions;

import tendril.util.TendrilStringUtil;

/**
 * Assertion helper for handling common asserts for {@link Collection}s
 */
public class CollectionAssert {

    /**
     * Verify that a collection is empty
     * 
     * @param <T>        what the collection contains
     * @param collection {@link Collection} to check if empty
     */
    public static <T> void assertEmpty(Collection<T> collection) {
        Assertions.assertEquals(0, collection.size(), "Collection expected to be empty but has " + collection.size() + " items");
    }

    /**
     * Verify that two collections have the same number of elements
     * 
     * @param <T>      what the collections contain
     * @param expected {@link Collection} that is expected
     * @param actual   {@link Collection} that was actually produced
     */
    public static <T> void assertSize(Collection<T> expected, Collection<T> actual) {
        int expectedSize = expected.size();
        int actualSize = actual.size();
        Assertions.assertEquals(expectedSize, actualSize, "Collections have different number of elements. Expected " + expectedSize + " but was " + actualSize);
    }

    /**
     * Verify that the actual {@link Collection} has the specific element in the specified order
     * 
     * @param <T>      what the collections contain
     * @param actual   {@link Collection} that was actually produced
     * @param expected T... elements that the {@link Collection} is expected to contain (in the specified order)
     */
    @SafeVarargs
    public static <T> void assertEquals(Collection<T> actual, T... expected) {
        assertEquals(Arrays.asList(expected), actual);
    }

    /**
     * Verify that two collections have the same elements in the same order
     * 
     * @param <T>      what the collections contain
     * @param expected {@link Collection} that is expected
     * @param actual   {@link Collection} that was actually produced
     */
    public static <T> void assertEquals(Collection<T> expected, Collection<T> actual) {
        assertSize(expected, actual);
        Iterator<T> expectedIter = expected.iterator();
        Iterator<T> actualIter = expected.iterator();
        while (expectedIter.hasNext() && actualIter.hasNext()) {
            Assertions.assertEquals(expectedIter.next(), actualIter.next());
        }
    }

    /**
     * Verify that two collections have the same element regardless of order
     * 
     * @param <T>      what the collections contain
     * @param actual   {@link Collection} that was actually produced
     * @param expected T... elements that the {@link Collection} is expected to contain (order not with standing)
     */
    @SafeVarargs
    public static <T> void assertEquivalent(Collection<T> actual, T... expected) {
        assertEquivalent(Arrays.asList(expected), actual);
    }
    
    /**
     * Verify that two collections have the same element regardless of order
     * 
     * @param <T>      what the collections contain
     * @param expected {@link Collection} that is expected
     * @param actual   {@link Collection} that was actually produced
     */
    public static <T> void assertEquivalent(Collection<T> expected, Collection<T> actual) {
        assertSize(expected, actual);
        ArrayList<T> actualArr = new ArrayList<>(actual);

        for (T element : expected)
            Assertions.assertTrue(actualArr.remove(element), "Actual expected to contain [" + element + "] but was missing");

        Assertions.assertTrue(actualArr.isEmpty(), "Actual has " + actualArr.size() + " additional elements [" + TendrilStringUtil.join(actualArr) + "]");
    }
}
