/**
 * 
 */
package tendril.codegen.field;

import org.junit.jupiter.api.Test;

/**
 * Test case for {@link JValueSimple}
 */
public class JValueSimpleTest extends AbstractJValueTest {

    /**
     * @see test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        // Nothing to do
    }
    
    /**
     * Verify that the appropriate code is generated
     */
    @Test
    public void testGenerate() {
        assertCode("`dsf'", new JValueSimple<String>("dsf", "`", "'"));
        assertCode("abc123efg", new JValueSimple<Integer>(123, "abc", "efg"));
        assertCode("1.23", new JValueSimple<Double>(1.23, "", ""));
        assertCode("321", new JValueSimple<Short>((short) 321));
    }

}
