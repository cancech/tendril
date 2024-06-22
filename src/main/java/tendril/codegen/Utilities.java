package tendril.codegen;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Utilities {

    public static String iso8061TimeStamp() {
        return DateTimeFormatter.ISO_DATE_TIME.format(LocalDateTime.now());
    }
}
