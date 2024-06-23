package tendril.util;

import java.util.Collection;

public class TendrilStringUtil {

    public static <T> String join(Collection<T> items, StringConverter<T> converter) {
        return join(items, ", ", converter);
    }

    public static <T> String join(Collection<T> items, String delimiter, StringConverter<T> converter) {
        StringBuilder builder = new StringBuilder();
        
        int index = 0;
        int size = items.size();
        for (T t: items) {
            builder.append(converter.convert(t));
            
            if (++index < size)
                builder.append(delimiter);
        }
        
        return builder.toString();
    }
}
