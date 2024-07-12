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
