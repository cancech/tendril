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
