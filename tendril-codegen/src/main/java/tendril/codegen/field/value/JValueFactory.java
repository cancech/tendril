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

import tendril.codegen.DefinitionException;
import tendril.codegen.classes.EnumerationEntry;
import tendril.codegen.field.type.ArrayType;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.PrimitiveType;
import tendril.codegen.field.type.Type;

/**
 * Factory to facilitate the creation of {@link JValue}s
 */
public class JValueFactory {

    /**
     * Hidden CTOR
     */
    private JValueFactory() {
    }

    /**
     * Create a {@link JValue} representing an array of VALUEs
     * 
     * @param <DATA_TYPE> extends {@link Type} representing what data type is contained within the array
     * @param <VALUE>     representing the specific Java data type containing the desired values
     * @param values      {@link Enum}... the specific values to place in the array
     * @return {@link JValue}
     */
    @SafeVarargs
    public static <DATA_TYPE extends Type, VALUE> JValue<ArrayType<DATA_TYPE>, List<JValue<DATA_TYPE, VALUE>>> createArray(VALUE... values) {
        List<JValue<DATA_TYPE, VALUE>> list = new ArrayList<>();
        DATA_TYPE type = null;

        for (VALUE value : values) {
            JValue<DATA_TYPE, VALUE> valueWrapper = create(value);
            if (type == null)
                type = valueWrapper.getType();
            list.add(valueWrapper);
        }
        return new JValueArray<DATA_TYPE, VALUE>(type, list);
    }

    /**
     * Create a {@link JValue} for the generic value provided
     * 
     * @param <DATA_TYPE> extends {@link Type} indicating which data type is expected to be contained in resulting {@link JValue}
     * @param <VALUE>     the type of value that is to be stored
     * @param value       VALUE that is to be stored
     * @return {@link JValue}
     */
    @SuppressWarnings("unchecked")
    public static <DATA_TYPE extends Type, VALUE> JValue<DATA_TYPE, VALUE> create(VALUE value) {
    	if (value instanceof ClassType cType)
    		return (JValue<DATA_TYPE, VALUE>) new JValueClass(cType); 
    				
        Class<?> cls = value.getClass();
        
        JValue<?, ?> createdValue = null;
        if (cls.isEnum() || cls.equals(Enum.class))
            createdValue = create((Enum<?>) value);
        else if (value instanceof EnumerationEntry)
            createdValue = create((EnumerationEntry) value);
        else if (cls.equals(String.class))
            createdValue = create((String) value);
        else if (cls.equals(Boolean.class))
            createdValue = create((boolean) value);
        else if (cls.equals(Byte.class))
            createdValue = create((byte) value);
        else if (cls.equals(Character.class))
            createdValue = create((char) value);
        else if (cls.equals(Double.class))
            createdValue = create((double) value);
        else if (cls.equals(Float.class))
            createdValue = create((float) value);
        else if (cls.equals(Integer.class))
            createdValue = create((int) value);
        else if (cls.equals(Long.class))
            createdValue = create((long) value);
        else if (cls.equals(Short.class))
            createdValue = create((short) value);
        else
            throw new DefinitionException("No factory method exists to create a JValue for " + value);

        return (JValue<DATA_TYPE, VALUE>) createdValue;
    }

    /**
     * Create a {@link JValue} representing an {@link Enum}
     * 
     * @param value {@link Enum} the specific value
     * @return {@link JValue}
     */
    public static JValue<ClassType, EnumerationEntry> create(Enum<?> value) {
        return new JValueEnum(value);
    }

    /**
     * Create a {@link JValue} representing an Enum as defined in an {@link EnumerationEntry}
     * 
     * @param value {@link EnumerationEntry} describing the enum
     * @return {@link JValue}
     */
    public static JValue<ClassType, EnumerationEntry> create(EnumerationEntry value) {
        return new JValueEnum(value);
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
    public static JValue<PrimitiveType, Character> create(char value) {
        return new JValueSimple<PrimitiveType, Character>(PrimitiveType.CHAR, value, "'", "'");
    }

    /**
     * Create a {@link JValue} for a {@link Long}
     * 
     * @param value long
     * @return {@link JValue}
     */
    public static JValue<PrimitiveType, Long> create(long value) {
        return new JValueSimple<PrimitiveType, Long>(PrimitiveType.LONG, value, "", "l");
    }

    /**
     * Create a {@link JValue} for a {@link Integer}
     * 
     * @param value int
     * @return {@link JValue}
     */
    public static JValue<PrimitiveType, Integer> create(int value) {
        return new JValueSimple<PrimitiveType, Integer>(PrimitiveType.INT, value);
    }

    /**
     * Create a {@link JValue} for a {@link Short}
     * 
     * @param value short
     * @return {@link JValue}
     */
    public static JValue<PrimitiveType, Short> create(short value) {
        return new JValueSimple<PrimitiveType, Short>(PrimitiveType.SHORT, value, "(short) ", "");
    }

    /**
     * Create a {@link JValue} for a {@link Double}
     * 
     * @param value double
     * @return {@link JValue}
     */
    public static JValue<PrimitiveType, Double> create(double value) {
        return new JValueSimple<PrimitiveType, Double>(PrimitiveType.DOUBLE, value, "", "d");
    }

    /**
     * Create a {@link JValue} for a {@link Float}
     * 
     * @param value float
     * @return {@link JValue}
     */
    public static JValue<PrimitiveType, Float> create(float value) {
        return new JValueSimple<PrimitiveType, Float>(PrimitiveType.FLOAT, value, "", "f");
    }

    /**
     * Create a {@link JValue} for a {@link Boolean}
     * 
     * @param value boolean
     * @return {@link JValue}
     */
    public static JValue<PrimitiveType, Boolean> create(boolean value) {
        return new JValueSimple<PrimitiveType, Boolean>(PrimitiveType.BOOLEAN, value);
    }

    /**
     * Create a {@link JValue} for a {@link Byte}
     * 
     * @param value byte
     * @return {@link JValue}
     */
    public static JValue<PrimitiveType, Byte> create(byte value) {
        return new JValueSimple<PrimitiveType, Byte>(PrimitiveType.BYTE, value);
    }
}
