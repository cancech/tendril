package tendril.codegen.field.type;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.codegen.DefinitionException;
import tendril.codegen.field.value.JValue;
import tendril.codegen.field.value.JValueArray;
import tendril.codegen.generics.GenericType;
import tendril.test.AbstractUnitTest;
import tendril.test.assertions.ClassAssert;

/**
 * Test case for {@link ArrayClassType}
 */
public class ArrayClassTypeTest extends AbstractUnitTest {
	
	// Mocks to use for testing
	@Mock
	private GenericType mockGeneric;

	// Instance to test
	private ArrayClassType type;
	
	/**
	 * @see tendril.test.AbstractUnitTest#prepareTest()
	 */
	@Override
	protected void prepareTest() {
		type = new ArrayClassType("a.b.c", "D");
	}

	/**
	 * Verify that the class details are correct.
	 */
	@Test
	public void testClassDetailsNoGenerics() {
		Assertions.assertEquals("D[]", type.getClassName());
		Assertions.assertEquals("a.b.c.D[]", type.getFullyQualifiedName());
		Assertions.assertEquals("a.b.c.D[]", type.getCodeName());
	}

	/**
	 * Verify that the class details are correct.
	 */
	@Test
	public void testClassDetailsWithGenerics() {
		when(mockGeneric.generateApplication()).thenReturn("G");
		type.addGeneric(mockGeneric);
		
		Assertions.assertEquals("D[]", type.getClassName());
		Assertions.assertEquals("a.b.c.D[]", type.getFullyQualifiedName());
		verifyAllChecked();
		
		Assertions.assertEquals("a.b.c.D<G>[]", type.getCodeName());
		verify(mockGeneric).generateApplication();
	}
	
	/**
	 * Verify that comparisons can be properly determined
	 */
	@Test
	public void testComparisons() {
		// Check assignability
		Assertions.assertFalse(type.isAssignableFrom(new ClassType("a.b.c", "D")));
		Assertions.assertTrue(type.isAssignableFrom(new ArrayClassType("a.b.c", "D")));
		
		// Check equality
		Assertions.assertFalse(type.equals(new ClassType("a.b.c", "D")));
		Assertions.assertTrue(type.equals(new ArrayClassType("a.b.c", "D")));
		
		// Check type of
		Assertions.assertFalse(type.isTypeOf(new ClassType("a.b.c", "D")));
		Assertions.assertTrue(type.isTypeOf(new ArrayClassType("a.b.c", "D")));
	}
	
	/**
	 * Verify that can convert to a value.
	 */
	@Test
	public void testAsValue() {
		type = new ArrayClassType(String.class.getPackageName(), String.class.getSimpleName());
		
		// Expected to fail, not an array
		Assertions.assertThrows(DefinitionException.class, () -> type.asValue("abc"));
		
		// Expected to pass, is an array
		JValue<?, ?> val = type.asValue(new String[] {"a", "b", "c"});
		ClassAssert.assertInstance(JValueArray.class, val);
		Assertions.assertEquals("{\"a\", \"b\", \"c\"}", val.generate());
	}
}
