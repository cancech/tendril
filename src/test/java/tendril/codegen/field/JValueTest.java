/**
 * 
 */
package tendril.codegen.field;

import static org.mockito.Mockito.verifyNoInteractions;

import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.dom.type.Type;
import tendril.dom.type.core.ClassType;

/**
 * Test case for {@link JValue}
 */
public class JValueTest extends AbstractJValueTest {
    
    private static final String GENERATED_TEXT = "Generated Code";
    
    /**
     * Concrete implementation of JValue to be used for testing
     */
    private class TestJValue extends JValue<Type> {
        
        /** Counter for how many times generate() was called */
        private int timesGenerateCalled = 0;

        /**
         * CTOR 
         */
        protected TestJValue() {
            super(mockValue);
        }

        /**
         * @see tendril.codegen.field.JValue#generate(java.util.Set)
         */
        @Override
        public String generate(Set<ClassType> classImports) {
            timesGenerateCalled++;
            Assertions.assertEquals(mockImports, classImports);
            return GENERATED_TEXT;
        }
        
        /**
         * Verify that generate() was called the expected number of times
         * 
         * @param expected int times generate() should have been called
         */
        private void verifyTimesGenerateCalled(int expected) {
            Assertions.assertEquals(expected, timesGenerateCalled);
        }
        
    }
    
    // Mocks to use for testing
    @Mock
    private Type mockValue;
    
    // Instance to test
    private TestJValue value;

    /**
     * @see test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        value = new TestJValue();
    }

    /**
     * Verify that generate does what is expected
     */
    @Test
    public void testGenerate() {
        assertCode(GENERATED_TEXT, value);
        value.verifyTimesGenerateCalled(1);
        verifyNoInteractions(mockValue);
    }
}
