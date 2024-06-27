package tendril.dom.annotation;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.dom.method.MethodElement;
import tendril.dom.type.core.ClassType;
import tendril.dom.type.value.ValueElement;
import test.AbstractUnitTest;
import test.assertions.CollectionAssert;
import test.assertions.TendrilAssert;

/**
 * Test case for {@link AppliedAnnotation}
 */
public class AppliedAnnotationTest extends AbstractUnitTest {

    // Mocks to use for testing
    @Mock
    private MethodElement<?> mockMethod1;
    @Mock
    private MethodElement<?> mockMethod2;
    @Mock
    private MethodElement<?> mockMethod3;
    @Mock
    private ValueElement<?, ?> mockMethod1Data;
    @Mock
    private ValueElement<?, ?> mockMethod2Data;
    @Mock
    private ValueElement<?, ?> mockMethod3Data;
    
    // Instance to test
    private AppliedAnnotation annotation;
    
    /**
     * @see test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        annotation = new AppliedAnnotation(TestAnnotation.class);
    }
    
    /**
     * Verify that if there are no parameters present, nothing is returned
     */
    @Test
    public void testNoParameters() {
        CollectionAssert.assertEmpty(annotation.getParameters());
        Assertions.assertNull(annotation.getValue(mockMethod1));
        Assertions.assertNull(annotation.getValue(mockMethod2));
        Assertions.assertNull(annotation.getValue(mockMethod3));
    }
    
    /**
     * Verify that if there is a single parameter, it can be retrieved
     */
    @Test
    public void testSingleParameter() {
        annotation.addParameter(mockMethod1, mockMethod1Data);
        Assertions.assertEquals(Collections.singletonList(mockMethod1), annotation.getParameters());
        Assertions.assertEquals(mockMethod1Data, annotation.getValue(mockMethod1));
        Assertions.assertNull(annotation.getValue(mockMethod2));
        Assertions.assertNull(annotation.getValue(mockMethod3));
    }
    
    /**
     * Verify that if there are multiple parameters, they can all be retrieved
     */
    @Test
    public void testMultipleParameters() {
        annotation.addParameter(mockMethod1, mockMethod1Data);
        annotation.addParameter(mockMethod2, mockMethod2Data);
        annotation.addParameter(mockMethod3, mockMethod3Data);
        Assertions.assertEquals(Arrays.asList(mockMethod1, mockMethod2, mockMethod3), annotation.getParameters());
        Assertions.assertEquals(mockMethod1Data, annotation.getValue(mockMethod1));
        Assertions.assertEquals(mockMethod2Data, annotation.getValue(mockMethod2));
        Assertions.assertEquals(mockMethod3Data, annotation.getValue(mockMethod3));
    }

    /**
     * Verify that the different CTORs produce the expected results
     */
    @Test
    public void testCtors() {
        TendrilAssert.assertImportData("tendril.dom.annotation", "TestAnnotation", new AppliedAnnotation(TestAnnotation.class));
        TendrilAssert.assertImportData("my.package.name", "MyAnnotation", new AppliedAnnotation(new ClassType("my.package.name", "MyAnnotation")));
        TendrilAssert.assertImportData("something.else.named.here", "SomeAnnotation", new AppliedAnnotation("something.else.named.here.SomeAnnotation"));
        TendrilAssert.assertImportData("mypackage", "MyClass", new AppliedAnnotation("mypackage", "MyClass"));
    }
}
