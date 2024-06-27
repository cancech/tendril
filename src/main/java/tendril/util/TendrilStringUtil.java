package tendril.util;

import java.util.Collection;

/**
 * A collection of {@link String} Utilities to facilitate {@link String} manipulation required, and which are not available elsewhere.
 */
public class TendrilStringUtil {

    /**
     * Joins a {@link Collection} of items to produce a comma (", ") delimited {@link String} of the items. The conversion to {@link String} is performed by using the items' {@code toString()} method.
     * 
     * @param <T>   The type of the items which are to be joined
     * @param items {@link Collection} whose contents is to be joined
     * @return {@link String} resulting from joining the items.
     */
    public static <T> String join(Collection<T> items) {
        return join(items, i -> i.toString());
    }

    /**
     * Joins a {@link Collection} of items to produce a comma (", ") delimited {@link String} of the items. The provided {@link StringConverter} is employed to perform the conversion, allowing any
     * client defined {@link String} conversion/manipulation to take place.
     * 
     * @param <T>       The type of the items which are to be joined
     * @param items     {@link Collection} whose contents is to be joined
     * @param converter {@link StringConverter} which converts each item to a {@link String} representation
     * @return {@link String} resulting from joining the items.
     */
    public static <T> String join(Collection<T> items, StringConverter<T> converter) {
        return join(items, ", ", converter);
    }

    /**
     * Joins a {@link Collection} of items to produce a delimited {@link String} of the items, using the specified delimiter. The conversion to {@link String} is performed by using the items'
     * {@code toString()} method.
     * 
     * @param <T>       The type of the items which are to be joined
     * @param items     {@link Collection} whose contents is to be joined
     * @param delimiter {@link String} to place in between each item in the {@link Collection}
     * @return {@link String} resulting from joining the items.
     */
    public static <T> String join(Collection<T> items, String delimiter) {
        return join(items, delimiter, i -> i.toString());
    }

    /**
     * Joins a {@link Collection} of items to produce a delimited {@link String} of the items, using the specified delimiter. The provided {@link StringConverter} is employed to perform the
     * conversion, allowing any client defined {@link String} conversion/manipulation to take place.
     * 
     * @param <T>       The type of the items which are to be joined
     * @param items     {@link Collection} whose contents is to be joined
     * @param delimiter {@link String} to place in between each item in the {@link Collection}
     * @param converter {@link StringConverter} which converts each item to a {@link String} representation
     * @return {@link String} resulting from joining the items.
     */
    public static <T> String join(Collection<T> items, String delimiter, StringConverter<T> converter) {
        StringBuilder builder = new StringBuilder();

        int index = 0;
        int size = items.size();
        for (T t : items) {
            builder.append(converter.convert(t));

            if (++index < size)
                builder.append(delimiter);
        }

        return builder.toString();
    }
}
