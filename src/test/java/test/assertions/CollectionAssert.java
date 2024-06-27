package test.assertions;

import java.util.Collection;

import org.junit.jupiter.api.Assertions;

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
}
