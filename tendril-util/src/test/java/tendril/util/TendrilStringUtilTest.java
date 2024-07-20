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

import java.util.Arrays;
import java.util.Collection;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link TendrilStringUtil}
 */
public class TendrilStringUtilTest {

    /**
     * Helper class to use for the purpose of verifying {@link TendrilStringUtil}
     */
    private class RandomTestObject {
        /** Value that is applied to the object */
        private final String value;

        /**
         * CTOR
         * 
         * @param value {@link String} to apply to the object
         */
        private RandomTestObject(String value) {
            this.value = value;
        }

        /**
         * Performs an arbitrary {@link String} operation, only purpose is to act as something different to {@code toString()}
         * 
         * @return {@link String} derived from the stored value
         */
        public String otherMethod() {
            return "_" + value.toUpperCase() + "_";
        }

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return value;
        }
    }

    // Objects to use for testing
    private RandomTestObject mockObj1 = new RandomTestObject("mockObj1");
    private RandomTestObject mockObj2 = new RandomTestObject("mockObj2");
    private RandomTestObject mockObj3 = new RandomTestObject("mockObj3");
    private RandomTestObject mockObj4 = new RandomTestObject("mockObj4");

    /**
     * Verify that the {@link TendrilStringUtil} with all defaults works as expected
     */
    @Test
    public void testJoinWithJustCollection() {
        Collection<RandomTestObject> collection = Arrays.asList(mockObj1, mockObj2, mockObj3, mockObj4);
        Assertions.assertEquals("mockObj1, mockObj2, mockObj3, mockObj4", TendrilStringUtil.join(collection));
    }

    /**
     * Verify that the {@link TendrilStringUtil} with custom delimiter works as expected
     */
    @Test
    public void testJoinWithCollectionAndDelimiter() {
        Collection<RandomTestObject> collection = Arrays.asList(mockObj1, mockObj2, mockObj3, mockObj4);
        Assertions.assertEquals("mockObj1-@-mockObj2-@-mockObj3-@-mockObj4", TendrilStringUtil.join(collection, "-@-"));
    }

    /**
     * Verify that the {@link TendrilStringUtil} with custom {@link StringConverter} works as expected
     */
    @Test
    public void testJoinWithCollectionAndConverter() {
        Collection<RandomTestObject> collection = Arrays.asList(mockObj1, mockObj2, mockObj3, mockObj4);
        Assertions.assertEquals("_MOCKOBJ1_, _MOCKOBJ2_, _MOCKOBJ3_, _MOCKOBJ4_", TendrilStringUtil.join(collection, RandomTestObject::otherMethod));
    }

    /**
     * Verify that the {@link TendrilStringUtil} with custom delimiter and {@link StringConverter} works as expected
     */
    @Test
    public void testJoinWithCollectionConverterAndDelimiter() {
        Collection<RandomTestObject> collection = Arrays.asList(mockObj1, mockObj2, mockObj3, mockObj4);
        Assertions.assertEquals("_MOCKOBJ1_-mockObj1~:~_MOCKOBJ2_-mockObj2~:~_MOCKOBJ3_-mockObj3~:~_MOCKOBJ4_-mockObj4",
                TendrilStringUtil.join(collection, "~:~", obj -> obj.otherMethod() + "-" + obj.toString()));
    }
}
