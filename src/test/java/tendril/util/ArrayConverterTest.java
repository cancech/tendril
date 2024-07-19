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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import tendril.codegen.VisibilityType;
import tendril.codegen.field.type.ClassType;

/**
 * Test case for {@link ArrayConverter}
 */
public class ArrayConverterTest {

    /**
     * Verify that object arrays are properly converted
     */
    @Test
    public void testObjectArrayConversion() {
        // Conversions that pass
        Assertions.assertArrayEquals(new Boolean[] { true, false, true, false }, ArrayConverter.toObjectArray(new Boolean[] { true, false, true, false }));
        Assertions.assertArrayEquals(new Byte[] { Byte.valueOf("0"), Byte.valueOf("10") }, ArrayConverter.toObjectArray(new Byte[] { Byte.valueOf("0"), Byte.valueOf("10") }));
        Assertions.assertArrayEquals(new Character[] { 'a', 'b', 'c', 'd' }, ArrayConverter.toObjectArray(new Character[] { 'a', 'b', 'c', 'd' }));
        Assertions.assertArrayEquals(new Double[] { 1.23, 2.34, 3.45 }, ArrayConverter.toObjectArray(new Double[] { 1.23, 2.34, 3.45 }));
        Assertions.assertArrayEquals(new Float[] { 1.23f, 2.34f, 3.45f }, ArrayConverter.toObjectArray(new Float[] { 1.23f, 2.34f, 3.45f }));
        Assertions.assertArrayEquals(new Integer[] { 5, 4, 3, 2, 1 }, ArrayConverter.toObjectArray(new Integer[] { 5, 4, 3, 2, 1 }));
        Assertions.assertArrayEquals(new Long[] { 100l, 200l, 300l, 400l }, ArrayConverter.toObjectArray(new Long[] { 100l, 200l, 300l, 400l }));
        Assertions.assertArrayEquals(new Short[] { 2, 4, 6, 8 }, ArrayConverter.toObjectArray(new Short[] { 2, 4, 6, 8 }));
        Assertions.assertArrayEquals(new String[] { "123", "abc", "abc123" }, ArrayConverter.toObjectArray(new String[] { "123", "abc", "abc123" }));
        Assertions.assertArrayEquals(VisibilityType.values(), ArrayConverter.toObjectArray(VisibilityType.values()));
        Assertions.assertArrayEquals(new ClassType[] { new ClassType("a", "b") }, ArrayConverter.toObjectArray(new ClassType[] { new ClassType("a", "b") }));

        // Conversions that fail
        Assertions.assertThrows(ClassCastException.class, () -> ArrayConverter.toObjectArray("abc123"));
        Assertions.assertThrows(ClassCastException.class, () -> ArrayConverter.toObjectArray(new ClassType("a", "b")));
    }

    /**
     * Verify that primitive arrays are properly converted 
     */
    @Test
    public void testPrimitiveArrayConversion() {
        Assertions.assertArrayEquals(new Boolean[] { true, false, true, false }, ArrayConverter.toObjectArray(new boolean[] { true, false, true, false }));
        Assertions.assertArrayEquals(new Byte[] { Byte.valueOf("0"), Byte.valueOf("10") }, ArrayConverter.toObjectArray(new byte[] { Byte.valueOf("0"), Byte.valueOf("10") }));
        Assertions.assertArrayEquals(new Character[] { 'a', 'b', 'c', 'd' }, ArrayConverter.toObjectArray(new char[] { 'a', 'b', 'c', 'd' }));
        Assertions.assertArrayEquals(new Double[] { 1.23, 2.34, 3.45 }, ArrayConverter.toObjectArray(new double[] { 1.23, 2.34, 3.45 }));
        Assertions.assertArrayEquals(new Float[] { 1.23f, 2.34f, 3.45f }, ArrayConverter.toObjectArray(new float[] { 1.23f, 2.34f, 3.45f }));
        Assertions.assertArrayEquals(new Integer[] { 5, 4, 3, 2, 1 }, ArrayConverter.toObjectArray(new int[] { 5, 4, 3, 2, 1 }));
        Assertions.assertArrayEquals(new Long[] { 100l, 200l, 300l, 400l }, ArrayConverter.toObjectArray(new long[] { 100l, 200l, 300l, 400l }));
        Assertions.assertArrayEquals(new Short[] { 2, 4, 6, 8 }, ArrayConverter.toObjectArray(new short[] { 2, 4, 6, 8 }));
    }
}
