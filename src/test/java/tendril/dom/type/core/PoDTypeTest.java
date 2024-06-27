package tendril.dom.type.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link PoDType}
 */
public class PoDTypeTest {

    /**
     * Verify elements produce the proper string
     */
    @Test
    public void testToString() {
        Assertions.assertEquals(8, PoDType.values().length);
        Assertions.assertEquals("boolean", PoDType.BOOLEAN.toString());
        Assertions.assertEquals("byte", PoDType.BYTE.toString());
        Assertions.assertEquals("char", PoDType.CHAR.toString());
        Assertions.assertEquals("double", PoDType.DOUBLE.toString());
        Assertions.assertEquals("float", PoDType.FLOAT.toString());
        Assertions.assertEquals("int", PoDType.INT.toString());
        Assertions.assertEquals("long", PoDType.LONG.toString());
        Assertions.assertEquals("short", PoDType.SHORT.toString());
    }
    
    /**
     * Verify that the assignability is properly configured
     */
    @Test
    public void testAssignable() {
        for (PoDType i: PoDType.values()) {
            for (PoDType j: PoDType.values())
                Assertions.assertEquals(i == j, i.isAssignableTo(j));
        }
    }
}
