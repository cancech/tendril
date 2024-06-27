package tendril.util;

/**
 * Works in conjunction with {@link TendrilStringUtil} to perform the conversion of an arbitrary object to a {@link String}, via a client defined mechanism.
 * 
 * @param <T> the type of {@link Object} the converter is to work with.
 */
public interface StringConverter<T> {

    /**
     * Convert the provided instance to a {@link String}.
     * 
     * @param instance T The specific instance to convert
     * @return {@link String} resulting from the conversion
     */
    String convert(T instance);
}
