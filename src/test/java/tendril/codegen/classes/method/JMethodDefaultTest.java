/**
 * 
 */
package tendril.codegen.classes.method;

import java.util.Collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import tendril.dom.type.Type;

/**
 * Test case for {@link JMethodDefault}
 */
public class JMethodDefaultTest extends SharedJMethodTest {

    /**
     * @see tendril.codegen.classes.method.SharedJMethodTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        super.prepareTest();
    }

    /**
     * Verify that the appropriate method signature start is generated
     */
    @Test
    public void testSignatureStart() {
        JMethodDefault<Type> method = new JMethodDefault<>(mockVisibility, mockMethodElement, Collections.emptyList());
        verifyMethodInit(method);

        Assertions.assertEquals("mockVisibility abstract ", method.generateSignatureStart(false));
        Assertions.assertEquals("mockVisibility ", method.generateSignatureStart(true));
    }
}
