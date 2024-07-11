package tendril.codegen.field;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory to facilitate the creation of {@link JValue}s
 */
public class JValueFactory {
    
    /**
     * Create a {@link JValue} representing an {@link Enum}
     * 
     * @param <E> the type of {@link Enum}
     * @param value E the specific value
     * @return {@link JValue}
     */
    public static <E extends Enum<E>> JValue<E> from(E value) {
        return new JValueEnum<E>(value);
    }
    
    /**
     * Create a {@link JValue} representing an array of {@link Enum}s
     * 
     * @param <E> the type of {@link Enum}
     * @param values E... the specific values to place in the array
     * @return {@link JValue}
     */
    @SafeVarargs
    public static <E extends Enum<E>> JValue<List<JValue<E>>> from(E...values) {
        List<JValue<E>> list = new ArrayList<JValue<E>>();
        for (E value: values)
            list.add(from(value));
        return new JValueArray<E>(list);
    }
    
    /**
     * Create a {@link JValue} for a {@link String}
     * 
     * @param value {@link String}
     * @return {@link JValue}
     */
    public static JValue<String> from(String value) {
        return new JValueSimple<String>(value, "\"", "\"");
    }
    
    /**
     * Create a {@link JValue} for a {@link Character}
     * 
     * @param value char
     * @return {@link JValue}
     */
    public static JValue<Character> from(char value) {
        return new JValueSimple<Character>(value, "'", "'");
    }

    /**
     * Create a {@link JValue} for a {@link Long}
     * 
     * @param value long
     * @return {@link JValue}
     */
    public static JValue<Long> from(long value) {
        return new JValueSimple<Long>(value, "", "l");
    }

    /**
     * Create a {@link JValue} for a {@link Integer}
     * 
     * @param value int
     * @return {@link JValue}
     */
    public static JValue<Integer> from(int value) {
        return new JValueSimple<Integer>(value);
    }

    /**
     * Create a {@link JValue} for a {@link Short}
     * 
     * @param value short
     * @return {@link JValue}
     */
    public static JValue<Short> from(short value) {
        return new JValueSimple<Short>(value);
    }

    /**
     * Create a {@link JValue} for a {@link Double}
     * 
     * @param value double
     * @return {@link JValue}
     */
    public static JValue<Double> from(double value) {
        return new JValueSimple<Double>(value, "", "d");
    }

    /**
     * Create a {@link JValue} for a {@link Float}
     * 
     * @param value float
     * @return {@link JValue}
     */
    public static JValue<Float> from(float value) {
        return new JValueSimple<Float>(value, "", "f");
    }

    /**
     * Create a {@link JValue} for a {@link Boolean}
     * 
     * @param value boolean
     * @return {@link JValue}
     */
    public static JValue<Boolean> from(boolean value) {
        return new JValueSimple<Boolean>(value);
    }
}
