/**
 * 
 */
package tendril.codegen.field;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.dom.type.Type;
import tendril.dom.type.core.ClassType;
import test.AbstractUnitTest;

/**
 * Test case for {@link JValueArray}
 */
public class JValueArrayTest extends AbstractUnitTest {
    
    // Mocks to use for testing
    @Mock
    private JValue<Type> mockValue1;
    @Mock
    private JValue<Type> mockValue2;
    @Mock
    private JValue<Type> mockValue3;
    @Mock
    private Set<ClassType> mockImports;

    // Instance to use for testing
    private JValueArray<Type> value;
    
    /**
     * @see test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        value = new JValueArray<Type>(Arrays.asList(mockValue1, mockValue2, mockValue3));
    }
    
    /**
     * Verify that generate produces the appropriate code
     */
    @Test
    public void testGenerate() {
        when(mockValue1.generate(mockImports)).thenReturn("mockValue1");
        when(mockValue2.generate(mockImports)).thenReturn("mockValue2");
        when(mockValue3.generate(mockImports)).thenReturn("mockValue3");
        
        Assertions.assertEquals("{mockValue1, mockValue2, mockValue3}", value.generate(mockImports));
        verify(mockValue1).generate(mockImports);
        verify(mockValue2).generate(mockImports);
        verify(mockValue3).generate(mockImports);
    }

}
