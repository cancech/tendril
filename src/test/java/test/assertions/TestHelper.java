package test.assertions;

import org.junit.jupiter.api.Assertions;

public class TestHelper {

    static void assertEquals(String expected, String actual, String msgIntro) {
        Assertions.assertEquals(expected, actual, msgIntro + ", expected \"" + expected + "\", but was \"" + actual + "\"");
    }
}
