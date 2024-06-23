package tendril.util;

public interface StringConverter<T> {

    String convert(T instance);
}
