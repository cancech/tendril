package tendril;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.test.AbstractUnitTest;

public class BeanReplacementExceptionTest extends AbstractUnitTest {
    
    // Mocks to use for testing
    @Mock
    private Exception mockCause;
    

    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
    }

    /**
     * Verify that the exception properly presents the message
     */
    @Test
    public void testWithMessage() {
    	BeanReplacementException ex = new BeanReplacementException("Message");
        Assertions.assertEquals("Message", ex.getMessage());
        Assertions.assertNull(ex.getCause());
    }

    /**
     * Verify that the exception properly presents the cause
     */
    @Test
    public void testWithCause() {
    	BeanReplacementException ex = new BeanReplacementException("SomeMessage", mockCause);
        Assertions.assertEquals("SomeMessage", ex.getMessage());
        Assertions.assertEquals(mockCause, ex.getCause());
    }
    
}
