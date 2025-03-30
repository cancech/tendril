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
import java.util.LinkedHashMap;
import java.util.Map;

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
     * Verify that the join collection with all defaults works as expected
     */
    @Test
    public void testJoinWithJustCollection() {
        Collection<RandomTestObject> collection = Arrays.asList(mockObj1, mockObj2, mockObj3, mockObj4);
        Assertions.assertEquals("mockObj1, mockObj2, mockObj3, mockObj4", TendrilStringUtil.join(collection));
    }

    /**
     * Verify that the join collection with custom delimiter works as expected
     */
    @Test
    public void testJoinWithCollectionAndDelimiter() {
        Collection<RandomTestObject> collection = Arrays.asList(mockObj1, mockObj2, mockObj3, mockObj4);
        Assertions.assertEquals("mockObj1-@-mockObj2-@-mockObj3-@-mockObj4", TendrilStringUtil.join(collection, "-@-"));
    }

    /**
     * Verify that the join collection with custom {@link StringConverter} works as expected
     */
    @Test
    public void testJoinWithCollectionAndConverter() {
        Collection<RandomTestObject> collection = Arrays.asList(mockObj1, mockObj2, mockObj3, mockObj4);
        Assertions.assertEquals("_MOCKOBJ1_, _MOCKOBJ2_, _MOCKOBJ3_, _MOCKOBJ4_", TendrilStringUtil.join(collection, RandomTestObject::otherMethod));
    }

    /**
     * Verify that the join collection with custom delimiter and {@link StringConverter} works as expected
     */
    @Test
    public void testJoinWithCollectionConverterAndDelimiter() {
        Collection<RandomTestObject> collection = Arrays.asList(mockObj1, mockObj2, mockObj3, mockObj4);
        Assertions.assertEquals("_MOCKOBJ1_-mockObj1~:~_MOCKOBJ2_-mockObj2~:~_MOCKOBJ3_-mockObj3~:~_MOCKOBJ4_-mockObj4",
                TendrilStringUtil.join(collection, "~:~", obj -> obj.otherMethod() + "-" + obj.toString()));
    }
    
    /**
     * Verify that the join map with all defaults works as expected
     */
    @Test
    public void testJoinWithJustMap() {
        Map<RandomTestObject, RandomTestObject> map = buildMap(mockObj1, mockObj2, mockObj3, mockObj4);
        Assertions.assertEquals("mockObj1 = mockObj2, mockObj2 = mockObj3, mockObj3 = mockObj4, mockObj4 = mockObj1", TendrilStringUtil.join(map));
    }
    
    /**
     * Verify that the join map with explicit delimiter works as expected
     */
    @Test
    public void testJoinWithMapAndDelimiter() {
        Map<RandomTestObject, RandomTestObject> map = buildMap(mockObj1, mockObj2, mockObj3, mockObj4);
        Assertions.assertEquals("mockObj1 = mockObj2<-->mockObj2 = mockObj3<-->mockObj3 = mockObj4<-->mockObj4 = mockObj1", TendrilStringUtil.join(map, "<-->"));
    }
    
    /**
     * Verify that the join map with custom converter works as expected
     */
    @Test
    public void testJoinWithMapAndCustomConverter() {
        Map<RandomTestObject, RandomTestObject> map = buildMap(mockObj1, mockObj2, mockObj3, mockObj4);
        Assertions.assertEquals("_MOCKOBJ1_ = _MOCKOBJ2_, _MOCKOBJ2_ = _MOCKOBJ3_, _MOCKOBJ3_ = _MOCKOBJ4_, _MOCKOBJ4_ = _MOCKOBJ1_",
                TendrilStringUtil.join(map, k -> k.otherMethod(), v -> v.otherMethod()));
    }
    
    /**
     * Verify that the join map with custom converter and delimiter works as expected
     */
    @Test
    public void testJoinWithMapAndCustomConverterAndDelimiter() {
        Map<RandomTestObject, RandomTestObject> map = buildMap(mockObj1, mockObj2, mockObj3, mockObj4);
        Assertions.assertEquals("_MOCKOBJ1_ = _MOCKOBJ2_(^v^)_MOCKOBJ2_ = _MOCKOBJ3_(^v^)_MOCKOBJ3_ = _MOCKOBJ4_(^v^)_MOCKOBJ4_ = _MOCKOBJ1_",
                TendrilStringUtil.join(map, "(^v^)", k -> k.otherMethod(), v -> v.otherMethod()));
    }
    
    /**
     * Verify that the join map with a custom binary converter works as expected
     */
    @Test
    public void testJoinWithMapAndBinaryConverter() {
        Map<RandomTestObject, RandomTestObject> map = buildMap(mockObj1, mockObj2, mockObj3, mockObj4);
        Assertions.assertEquals("mockObj1<_MOCKOBJ2_>, mockObj2<_MOCKOBJ3_>, mockObj3<_MOCKOBJ4_>, mockObj4<_MOCKOBJ1_>", TendrilStringUtil.join(map,
                (key, value) -> key + "<" + value.otherMethod() + ">"));
    }
    
    /**
     * Verify that the join map with a custom binary converter and delimiter works as expected
     */
    @Test
    public void testJoinWithMapAndBinaryConverterAndDelimiter() {
        Map<RandomTestObject, RandomTestObject> map = buildMap(mockObj1, mockObj2, mockObj3, mockObj4);
        Assertions.assertEquals("_MOCKOBJ1_<mockObj2>///_MOCKOBJ2_<mockObj3>///_MOCKOBJ3_<mockObj4>///_MOCKOBJ4_<mockObj1>", TendrilStringUtil.join(map, "///",
                (key, value) -> key.otherMethod() + "<" + value + ">"));
    }
    
    /**
     * Helper to build a map of elements, such that obj1 = obj2,..., objN = obj1
     * 
     * @param objs {@link RandomTestObject}... to populate the map with
     * @return {@link Map} such that the order of keys is maintained
     */
    private Map<RandomTestObject, RandomTestObject> buildMap(RandomTestObject...objs) {
        Map<RandomTestObject, RandomTestObject> map = new LinkedHashMap<>();
        for (int i = 0; i < objs.length; i++) {
            int valueIndex = (i + 1) % objs.length;
            map.put(objs[i], objs[valueIndex]);
        }
        return map;
    }
}
