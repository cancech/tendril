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

import java.util.Collection;
import java.util.Map;

/**
 * A collection of {@link String} Utilities to facilitate {@link String} manipulation required, and which are not available elsewhere.
 */
public abstract class TendrilStringUtil {
    /** The delimiter to be employed if none is explicitely specified by the user */
    private static final String DEFAULT_DELIMITER = ", ";

    /**
     * Hidden CTOR
     */
    private TendrilStringUtil() {
    }

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
        return join(items, DEFAULT_DELIMITER, converter);
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
    
    /**
     * Joins the elements in a {@link Map} such that a comma delimited "key = value" pairing of all elements is returned. Both keys and values are converted to {@link String} by way of their
     * respective {@code toString()} methods.
     * 
     * @param <K> type of key that is employed in the map
     * @param <V> type of value that is employed in the map
     * @param items {@link Map} to be joined into a single {@link String}
     * @return {@link String} representation of the map
     */
    public static <K, V> String join(Map<K, V> items) {
        return join(items, DEFAULT_DELIMITER);
    }

    /**
     * Joins the elements in a {@link Map} such that a delimited "key = value" pairing of all elements is returned, with the delimiter specified by the client code.  Both keys and values
     * are converted to {@link String} by way of their respective {@code toString()} methods.
     * 
     * @param <K> type of key that is employed in the map
     * @param <V> type of value that is employed in the map
     * @param items {@link Map} to be joined into a single {@link String}
     * @param delimiter {@link String} to be placed between every "key = value" pair
     * @return {@link String} representation of the map
     */
    public static <K, V> String join(Map<K, V> items, String delimiter) {
        return join(items, delimiter, key -> key.toString(), value -> value.toString());
    }

    /**
     * Joins the elements in a {@link Map} such that a comma delimited "key = value" pairing of all elements is returned. A separate converter is required for both the key and value, which
     * is then used to convert both to a {@link String} representation
     * 
     * @param <K> type of key that is employed in the map
     * @param <V> type of value that is employed in the map
     * @param items {@link Map} to be joined into a single {@link String}
     * @param keyConverter {@link StringConverter} for converting the key to a {@link String}
     * @param valueConverter {@link StringConverter} for converting the value to a {@link String}
     * @return {@link String} representation of the map
     */
    public static <K, V> String join(Map<K, V> items, StringConverter<K> keyConverter, StringConverter<V> valueConverter) {
        return join(items, DEFAULT_DELIMITER, keyConverter, valueConverter);
    }

    /**
     * Joins the elements in a {@link Map} such that a delimited "key = value" pairing of all elements is returned, with the delimiter specified by the client code. A separate converter
     * is required for both the key and value, which is then used to convert both to a {@link String} representation
     * 
     * @param <K> type of key that is employed in the map
     * @param <V> type of value that is employed in the map
     * @param items {@link Map} to be joined into a single {@link String}
     * @param delimiter {@link String} to be placed between every "key = value" pair
     * @param keyConverter {@link StringConverter} for converting the key to a {@link String}
     * @param valueConverter {@link StringConverter} for converting the value to a {@link String}
     * @return {@link String} representation of the map
     */
    public static <K, V> String join(Map<K, V> items, String delimiter, StringConverter<K> keyConverter, StringConverter<V> valueConverter) {
        return join(items, delimiter, (key, value) -> keyConverter.convert(key) + " = " + valueConverter.convert(items.get(key)));
    }

    /**
     * Joins the elements in a {@link Map} such that a comma delimited pairing of all elements is returned. The specified converter is employed to combine both elements (key, value) into
     * a single {@link String} at the same time.
     * 
     * @param <K> type of key that is employed in the map
     * @param <V> type of value that is employed in the map
     * @param items {@link Map} to be joined into a single {@link String}
     * @param converter {@link BinaryStringConverter} for converting the key/value pair to a {@link String}
     * @return {@link String} representation of the map
     */
    public static <K, V> String join(Map<K, V> items, BinaryStringConverter<K, V> converter) {
        return join(items, DEFAULT_DELIMITER, converter);
    }

    /**
     * Joins the elements in a {@link Map} such that a delimited pairing of all elements is returned, with the delimiter specified by the client code. The specified converter is employed
     * to combine both elements (key, value) into a single {@link String} at the same time.
     * 
     * @param <K> type of key that is employed in the map
     * @param <V> type of value that is employed in the map
     * @param items {@link Map} to be joined into a single {@link String}
     * @param delimiter {@link String} to be placed between every "key = value" pair
     * @param converter {@link BinaryStringConverter} for converting the key/value pair to a {@link String}
     * @return {@link String} representation of the map
     */
    public static <K, V> String join(Map<K, V> items,  String delimiter, BinaryStringConverter<K, V> converter) {
        return join(items.keySet(), delimiter, key -> converter.convert(key, items.get(key)));
    }
}
