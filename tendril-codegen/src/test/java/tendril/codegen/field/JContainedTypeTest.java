package tendril.codegen.field;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.codegen.CodeBuilder;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.Type;
import tendril.test.AbstractUnitTest;

/**
 * Test case for {@link JContainedType}
 */
public class JContainedTypeTest extends AbstractUnitTest {
	
	private class TestJContainedType extends JContainedType<Type> {

        /**
         * CTOR
         */
        public TestJContainedType() {
            super(mockType, "ContainedElement");
        }

        /**
         * @see tendril.codegen.JBase#appendSelf(tendril.codegen.CodeBuilder, java.util.Set)
         */
        @Override
        protected void appendSelf(CodeBuilder builder, Set<ClassType> classImports) {
            Assertions.fail("Should not be called");
        }

        /**
         * @see tendril.codegen.JBase#generateSelf(java.util.Set)
         */
        @Override
        public String generateSelf(Set<ClassType> classImports) {
            Assertions.fail("Should not be called");
            return null;
        }
		
	}
    
    // Mocks to use for testing
    @Mock
    private Type mockType;
    @Mock
    private JContainedType<Type> mockContainer1;
    @Mock
    private JContainedType<Type> mockContainer2;
    
    // Instance to test
    private JContainedType<Type> element;

    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
	@Override
	protected void prepareTest() {
		element = new TestJContainedType();
	}
	
	/**
	 * Verify that the container can be applied and retrieved
	 */
	@Test
	public void testContainer() {
		// Null by default
		Assertions.assertFalse(element.isInContainer());
		
		// Can be set
		element.setContainer(mockContainer1);
		Assertions.assertTrue(element.isInContainer());
		
		// Can be overridden
		element.setContainer(mockContainer2);
		Assertions.assertTrue(element.isInContainer());
		
		// Can be reset
		element.setContainer(null);
		Assertions.assertFalse(element.isInContainer());
	}
	
	/**
	 * Verify that the container path can be derived
	 */
	@Test
	public void testFullPathNoContainer() {
		Assertions.assertEquals("ContainedElement", element.getFullElementPath());
	}
	
	/**
	 * Verify that the container path can be derived
	 */
	@Test
	public void testFullPathHasContainer() {
		when(mockContainer1.getFullElementPath()).thenReturn("CONTAINER");
		element.setContainer(mockContainer1);
		Assertions.assertEquals("CONTAINER::ContainedElement", element.getFullElementPath());
		verify(mockContainer1).getFullElementPath();
	}

}
