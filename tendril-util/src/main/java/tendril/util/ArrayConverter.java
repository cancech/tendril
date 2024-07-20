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

import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

/**
 * Utility for performing array conversions
 */
public abstract class ArrayConverter {

    /**
     * Hidden CTOR
     */
    private ArrayConverter() {
    }

    /**
     * Convert the specified {@link Object} to an {@link Object} array. If it is an array of primitives it is converted to an {@link Object} array of the appropriate type (i.e.: int[] to
     * {@link Integer}[]), otherwise it is simply case to {@link Object}[].
     * 
     * @param array {@link Object} to be converted to {@link Object}[]
     * @return {@link Object}[]
     */
    public static Object[] toObjectArray(Object array) {

        if (array instanceof boolean[])
            return toBooleanArray((boolean[]) array);
        if (array instanceof byte[])
            return toByteArray((byte[]) array);
        if (array instanceof char[])
            return toCharacterArray((char[]) array);
        if (array instanceof double[])
            return toDoubleArray((double[]) array);
        if (array instanceof float[])
            return toFloatArray((float[]) array);
        if (array instanceof int[])
            return toIntegerArray((int[]) array);
        if (array instanceof long[])
            return toLongArray((long[]) array);
        if (array instanceof short[])
            return toShortArray((short[]) array);

        return (Object[]) array;
    }

    /**
     * Convert the provided boolean[] to {@link Boolean}[]
     * 
     * @param bools boolean[] array to be converted
     * @return {@link Boolean}[] conversion
     */
    public static Boolean[] toBooleanArray(boolean[] bools) {
        Boolean[] objArr = new Boolean[bools.length];

        for (int i = 0; i < bools.length; i++)
            objArr[i] = bools[i];

        return objArr;
    }

    /**
     * Convert the provided byte[] to {@link Byte}[]
     * 
     * @param bytes byte[] array to be converted
     * @return {@link Byte}[] conversion
     */
    public static Byte[] toByteArray(byte[] bytes) {
        Byte[] objArr = new Byte[bytes.length];

        for (int i = 0; i < bytes.length; i++)
            objArr[i] = bytes[i];

        return objArr;
    }

    /**
     * Convert the provided char[] to {@link Character}[]
     * 
     * @param chars char[] array to be converted
     * @return {@link Character}[] conversion
     */
    public static Character[] toCharacterArray(char[] chars) {
        Character[] objArr = new Character[chars.length];

        for (int i = 0; i < chars.length; i++)
            objArr[i] = chars[i];

        return objArr;
    }

    /**
     * Convert the provided double[] to {@link Double}[]
     * 
     * @param dbls double[] array to be converted
     * @return {@link Double}[] conversion
     */
    public static Double[] toDoubleArray(double[] dbls) {
        return DoubleStream.of(dbls).boxed().toArray(Double[]::new);
    }

    /**
     * Convert the provided float[] to {@link Float}[]
     * 
     * @param floats float[] array to be converted
     * @return {@link Float}[] conversion
     */
    public static Float[] toFloatArray(float[] floats) {
        Float[] objArr = new Float[floats.length];

        for (int i = 0; i < floats.length; i++)
            objArr[i] = floats[i];

        return objArr;
    }

    /**
     * Convert the provided int[] to {@link Integer}[]
     * 
     * @param ints int[] array to be converted
     * @return {@link Integer}[] conversion
     */
    public static Integer[] toIntegerArray(int[] ints) {
        return IntStream.of(ints).boxed().toArray(Integer[]::new);
    }

    /**
     * Convert the provided long[] to {@link Long}[]
     * 
     * @param longs long[] array to be converted
     * @return {@link Long}[] conversion
     */
    public static Long[] toLongArray(long[] longs) {
        return LongStream.of(longs).boxed().toArray(Long[]::new);
    }

    /**
     * Convert the provided short[] to {@link Short}[]
     * 
     * @param shorts short[] array to be converted
     * @return {@link Short}[] conversion
     */
    public static Short[] toShortArray(short[] shorts) {
        Short[] objArr = new Short[shorts.length];

        for (int i = 0; i < shorts.length; i++)
            objArr[i] = shorts[i];

        return objArr;
    }
}
