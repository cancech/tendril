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
package tendril.codegen.field.value;

import java.util.ArrayList;
import java.util.List;

import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.PoDType;

/**
 * Factory to facilitate the creation of {@link JValue}s
 */
public class JValueFactory {

    /**
     * Create a {@link JValue} representing an {@link Enum}
     * 
     * @param value {@link Enum} the specific value
     * @return {@link JValue}
     */
    public static JValue<ClassType, Enum<?>> create(Enum<?> value) {
        return new JValueEnum(value);
    }

    /**
     * Create a {@link JValue} representing an array of {@link Enum}s
     * 
     * @param values {@link Enum}... the specific values to place in the array
     * @return {@link JValue}
     */
    @SafeVarargs
    public static JValue<ClassType, List<JValue<ClassType, Enum<?>>>> create(Enum<?>... values) {
        // TODO make this more generic such that it can be applied to any/all types
        List<JValue<ClassType, Enum<?>>> list = new ArrayList<>();
        ClassType enumType = null;

        for (Enum<?> value : values) {
            if (enumType == null)
                enumType = new ClassType(value.getClass());
            list.add(create(value));
        }
        return new JValueArray<ClassType, Enum<?>>(enumType, list);
    }

    /**
     * Create a {@link JValue} for a {@link String}
     * 
     * @param value {@link String}
     * @return {@link JValue}
     */
    public static JValue<ClassType, String> create(String value) {
        return new JValueSimple<ClassType, String>(new ClassType(String.class), value, "\"", "\"");
    }

    /**
     * Create a {@link JValue} for a {@link Character}
     * 
     * @param value char
     * @return {@link JValue}
     */
    public static JValue<PoDType, Character> create(char value) {
        return new JValueSimple<PoDType, Character>(PoDType.CHAR, value, "'", "'");
    }

    /**
     * Create a {@link JValue} for a {@link Long}
     * 
     * @param value long
     * @return {@link JValue}
     */
    public static JValue<PoDType, Long> create(long value) {
        return new JValueSimple<PoDType, Long>(PoDType.LONG, value, "", "l");
    }

    /**
     * Create a {@link JValue} for a {@link Integer}
     * 
     * @param value int
     * @return {@link JValue}
     */
    public static JValue<PoDType, Integer> create(int value) {
        return new JValueSimple<PoDType, Integer>(PoDType.INT, value);
    }

    /**
     * Create a {@link JValue} for a {@link Short}
     * 
     * @param value short
     * @return {@link JValue}
     */
    public static JValue<PoDType, Short> create(short value) {
        return new JValueSimple<PoDType, Short>(PoDType.SHORT, value, "(short) ", "");
    }

    /**
     * Create a {@link JValue} for a {@link Double}
     * 
     * @param value double
     * @return {@link JValue}
     */
    public static JValue<PoDType, Double> create(double value) {
        return new JValueSimple<PoDType, Double>(PoDType.DOUBLE, value, "", "d");
    }

    /**
     * Create a {@link JValue} for a {@link Float}
     * 
     * @param value float
     * @return {@link JValue}
     */
    public static JValue<PoDType, Float> create(float value) {
        return new JValueSimple<PoDType, Float>(PoDType.FLOAT, value, "", "f");
    }

    /**
     * Create a {@link JValue} for a {@link Boolean}
     * 
     * @param value boolean
     * @return {@link JValue}
     */
    public static JValue<PoDType, Boolean> create(boolean value) {
        return new JValueSimple<PoDType, Boolean>(PoDType.BOOLEAN, value);
    }

    /**
     * Create a {@link JValue} for a {@link Byte}
     * 
     * @param value byte
     * @return {@link JValue}
     */
    public static JValue<PoDType, Byte> create(byte value) {
        return new JValueSimple<PoDType, Byte>(PoDType.BYTE, value);
    }
}
