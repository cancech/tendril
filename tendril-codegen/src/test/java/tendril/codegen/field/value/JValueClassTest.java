package tendril.codegen.field.value;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import tendril.codegen.classes.JClass;
import tendril.codegen.field.JType;
import tendril.codegen.field.JVisibleType;
import tendril.codegen.field.type.ArrayType;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.PrimitiveType;
import tendril.codegen.field.type.TypeFactory;
import tendril.codegen.field.type.VoidType;
import tendril.codegen.generics.GenericFactory;

/**
 * Test case for {@link JValueClass}
 */
public class JValueClassTest extends SharedJValueTest {

	/** Tracks which import should have been last registered */
	private ClassType currentImport = null;

	/**
	 * @see tendril.codegen.field.value.SharedJValueTest#verifyMockImports()
	 */
	@Override
	protected void verifyMockImports() {
		verify(mockImports).add(currentImport);
	}

	/**
	 * Verify that the appropriate code is generated
	 */
	@Test
	public void testGenerateFromClass() {
		testClassCode(Object.class);
		testClassCode(Double.class);
		testClassCode(ClassType.class);
	}

	/**
	 * Helper to shorthand the creation of the {@link JValueClass} and the necessary import
	 * 
	 * @param <T>   {@link Class} to use
	 * @param klass {@link Class} to use for the test
	 */
	private <T> void testClassCode(Class<T> klass) {
		currentImport = TypeFactory.createClassType(klass);
		assertCode(klass.getSimpleName() + ".class", new JValueClass(currentImport));
	}

	/**
	 * Verify that instance of properly reports
	 */
	@Test
	public void testInstanceOf() {
		JValueClass value = new JValueClass(TypeFactory.createClassType(JType.class));

		// Expected to be false
		Assertions.assertFalse(value.isInstanceOf(null));
		for (PrimitiveType t : PrimitiveType.values())
			Assertions.assertFalse(value.isInstanceOf(t));
		Assertions.assertFalse(value.isInstanceOf(VoidType.INSTANCE));
		Assertions.assertFalse(value.isInstanceOf(new ArrayType<>(PrimitiveType.BOOLEAN)));
		Assertions.assertFalse(value.isInstanceOf(TypeFactory.createClassType(Double.class)));
		Assertions.assertFalse(value.isInstanceOf(buildClass(Double.class)));
		Assertions.assertFalse(value.isInstanceOf(buildClass(JVisibleType.class)));
		Assertions.assertFalse(value.isInstanceOf(buildClass(JClass.class)));
		
		// Expected to be true
		Assertions.assertTrue(value.isInstanceOf(buildClass(JType.class)));
	}

	/**
	 * Build the class reference (i.e.: Class<type>)
	 * 
	 * @param klass {@link Class} referencing the type of class
	 * @return {@link ClassType}
	 */
	private ClassType buildClass(Class<?> klass) {
		return TypeFactory.createClassType(Class.class, GenericFactory.create(TypeFactory.createClassType(klass)));
	}
}
