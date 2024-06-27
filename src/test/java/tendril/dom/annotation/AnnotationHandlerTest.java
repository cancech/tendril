package tendril.dom.annotation;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import test.AbstractUnitTest;

/**
 * Test case for {@link AnnotationHandler}
 */
public class AnnotationHandlerTest extends AbstractUnitTest {

    // Mocks to use for testing
    @Mock
    private AppliedAnnotation mockAnnotation1;
    @Mock
    private AppliedAnnotation mockAnnotation2;
    @Mock
    private AppliedAnnotation mockAnnotation3;
    
    // The instance to test
    private AnnotationHandler handler;
    
    /**
     * @see test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        handler = new AnnotationHandler();
    }
    
    /**
     * No annotation registered, none returned
     */
    @Test
    public void testNoAnnotation() {
        Assertions.assertEquals(Collections.emptyList(), handler.getAnnotations());
    }
    
    /**
     * A single annotation registered
     */
    @Test
    public void testSingleAnnotation() {
        handler.addAnnotation(mockAnnotation1);
        Assertions.assertEquals(Collections.singletonList(mockAnnotation1), handler.getAnnotations());
    }
    
    /**
     * Multiple annotations registered
     */
    @Test
    public void testMultipleAnnotations() {
        handler.addAnnotation(mockAnnotation1);
        handler.addAnnotation(mockAnnotation2);
        handler.addAnnotation(mockAnnotation3);
        Assertions.assertEquals(Arrays.asList(mockAnnotation1, mockAnnotation2, mockAnnotation3), handler.getAnnotations());
    }
}
