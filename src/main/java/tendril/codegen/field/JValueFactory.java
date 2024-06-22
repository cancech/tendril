package tendril.codegen.field;

import java.util.ArrayList;
import java.util.List;

public class JValueFactory {
    
    public static <E extends Enum<E>> JValue<E> from(E value) {
        return new JValueEnum<E>(value);
    }
    
    @SafeVarargs
    public static <E extends Enum<E>> JValue<List<JValue<E>>> from(E...values) {
        List<JValue<E>> list = new ArrayList<JValue<E>>();
        for (E value: values)
            list.add(from(value));
        return new JValueArray<E>(list);
    }
    
    public static JValue<String> from(String value) {
        return new JValueString(value);
    }
    
    public static JValue<Character> from(char value) {
        return new JValueChar(value);
    }
    
    public static JValue<Long> from(long value) {
        return new JValuePoD<Long>(value);
    }
    
    public static JValue<Integer> from(int value) {
        return new JValuePoD<Integer>(value);
    }
    
    public static JValue<Short> from(short value) {
        return new JValuePoD<Short>(value);
    }
    
    public static JValue<Double> from(double value) {
        return new JValuePoD<Double>(value);
    }
    
    public static JValue<Float> from(float value) {
        return new JValuePoD<Float>(value);
    }
    
    public static JValue<Boolean> from(boolean value) {
        return new JValuePoD<Boolean>(value);
    }
}
