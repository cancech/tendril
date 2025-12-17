/*
 * Copyright 2024 Jaroslav Bosak
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://opensource.org/license/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tendril.codegen.field.type;

import static org.mockito.Mockito.verify;

import java.awt.List;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import tendril.codegen.CodeBuilder;
import tendril.codegen.DefinitionException;
import tendril.codegen.JBase;
import tendril.codegen.JGeneric;
import tendril.codegen.VisibilityType;
import tendril.codegen.annotation.JAnnotation;
import tendril.codegen.classes.JClass;
import tendril.codegen.classes.JParameter;
import tendril.codegen.field.JField;
import tendril.codegen.field.JType;
import tendril.codegen.field.JVisibleType;
import tendril.codegen.field.value.JValue;
import tendril.codegen.field.value.JValueFactory;
import tendril.codegen.generics.GenericFactory;
import tendril.test.AbstractUnitTest;
import tendril.test.helper.assertions.TendrilAssert;

/**
 * Test case for {@link ClassType}
 */
public class ClassTypeTest extends SharedTypeTest<ClassType> {

	/**
	 * @see tendril.test.AbstractUnitTest#prepareTest()
	 */
	@Override
	protected void prepareTest() {
		type = new ClassType(VisibilityType.class);
	}

	/**
	 * Verify that the correct import is registered
	 */
	@Test
	public void testRegisterImports() {
		type.registerImport(mockImports);
		verify(mockImports).add(type);
		verifyDataState();
	}

	/** Listing of all class types that are to be used for testing asValue */
	private static final Class<?>[] allToTest = { VisibilityType.class, String.class, Boolean.class, Byte.class, Character.class, Double.class, Float.class, Integer.class, Long.class, Short.class };
	/** Listing of all values that are to be passed for testing asValue */
	private static final Object[] testValues = { VisibilityType.PUBLIC, "abc123", true, Byte.valueOf("10"), 'b', 1.23d, 2.34f, 345, 456l, Short.valueOf((short) 567) };

	/**
	 * Verify that the appropriate Class element is created
	 */
	@Test
	public void testAsValue() {
		// Enum, String, and Primitives properly generate a values (of the correct type)
		verifyAsValue(VisibilityType.class, JValueFactory.create((VisibilityType) testValues[0]));
		verifyAsValue(String.class, JValueFactory.create((String) testValues[1]));
		verifyAsValue(Boolean.class, JValueFactory.create((boolean) testValues[2]));
		verifyAsValue(Byte.class, JValueFactory.create((byte) testValues[3]));
		verifyAsValue(Character.class, JValueFactory.create((char) testValues[4]));
		verifyAsValue(Double.class, JValueFactory.create((double) testValues[5]));
		verifyAsValue(Float.class, JValueFactory.create((float) testValues[6]));
		verifyAsValue(Integer.class, JValueFactory.create((int) testValues[7]));
		verifyAsValue(Long.class, JValueFactory.create((long) testValues[8]));
		verifyAsValue(Short.class, JValueFactory.create((short) testValues[9]));
		verifyAsValue(ClassType.class, JValueFactory.create(new ClassType(getClass())));

		// Everything else generates an exception
		type = new ClassType(JBase.class);
		verifyDataState(JBase.class);
		Assertions.assertThrows(DefinitionException.class, () -> type.asValue(new ClassType(getClass())));
	}

	/**
	 * Verify that isTypeOf properly resolves
	 */
	@Test
	public void testIsTypeOf() {
		// Expected to be false
		Assertions.assertFalse(type.isTypeOf(new ClassType(getClass())));
		Assertions.assertFalse(type.isTypeOf(123));
		Assertions.assertFalse(type.isTypeOf("abc123"));
		Assertions.assertFalse(new ClassType(Integer.class).isTypeOf("abc123"));
		Assertions.assertFalse(new ClassType(String.class).isTypeOf(123));
		Assertions.assertFalse(new ClassType(ClassType.class).isTypeOf(new JGeneric()));
		Assertions.assertFalse(new ClassType(JGeneric.class).isTypeOf(new ClassType(JGeneric.class)));

		// Expected to be true
		for (VisibilityType t : VisibilityType.values())
			Assertions.assertTrue(type.isTypeOf(t));
		Assertions.assertTrue(new ClassType(Integer.class).isTypeOf(123));
		Assertions.assertTrue(new ClassType(String.class).isTypeOf("abc123"));
		Assertions.assertTrue(new ClassType(ClassType.class).isTypeOf(new ClassType(JGeneric.class)));
		Assertions.assertTrue(new ClassType(JGeneric.class).isTypeOf(new JGeneric()));
	}

	/**
	 * Verify that isTypeOf properly resolves
	 */
	@Test
	public void testIsTypeOfWhenClass() {
		type = buildClass(JBase.class);

		// Expected to be false
		Assertions.assertFalse(type.isTypeOf(new ClassType(getClass())));
		Assertions.assertFalse(type.isTypeOf(123));
		Assertions.assertFalse(type.isTypeOf("abc123"));
		Assertions.assertFalse(new ClassType(Integer.class).isTypeOf("abc123"));
		Assertions.assertFalse(new ClassType(String.class).isTypeOf(123));
		Assertions.assertFalse(new ClassType(ClassType.class).isTypeOf(new JGeneric()));
		Assertions.assertFalse(new ClassType(JGeneric.class).isTypeOf(new ClassType(JGeneric.class)));
		Assertions.assertFalse(type.isTypeOf(new ClassType(JGeneric.class)));
		Assertions.assertFalse(type.isTypeOf(buildClass(new ClassType("a.b.c.D"))));
		Assertions.assertFalse(type.isTypeOf(new JBase("") {
			@Override
			public String generateSelf(Set<ClassType> classImports) {
				return null;
			}

			@Override
			protected void appendSelf(CodeBuilder builder, Set<ClassType> classImports) {
			}
		}));

		// Expected to be true
		Assertions.assertTrue(type.isTypeOf(new ClassType(JBase.class)));
		Assertions.assertTrue(type.isTypeOf(new ClassType(JAnnotation.class)));
		Assertions.assertTrue(type.isTypeOf(new ClassType(JType.class)));
		Assertions.assertTrue(type.isTypeOf(new ClassType(JParameter.class)));
		Assertions.assertTrue(type.isTypeOf(new ClassType(JVisibleType.class)));
		Assertions.assertTrue(type.isTypeOf(new ClassType(JClass.class)));
		Assertions.assertTrue(type.isTypeOf(buildClass(JBase.class)));
		Assertions.assertTrue(type.isTypeOf(buildClass(JAnnotation.class)));
		Assertions.assertTrue(type.isTypeOf(buildClass(JType.class)));
		Assertions.assertTrue(type.isTypeOf(buildClass(JParameter.class)));
		Assertions.assertTrue(type.isTypeOf(buildClass(JVisibleType.class)));
		Assertions.assertTrue(type.isTypeOf(buildClass(JClass.class)));
	}

	/**
	 * Verify that the correct {@link JValue} is created or exception thrown when using {@link ClassType} to generate a {@link JValue}
	 * 
	 * @param <T>           the type of class which is to be stored in the {@link ClassType}
	 * @param valueClass    {@link Class} where the class represented by the {@link ClassType} is defined
	 * @param expectedValue {@link JValue} that is expected to be produced
	 */
	private <T> void verifyAsValue(Class<T> valueClass, JValue<?, ?> expectedValue) {
		type = new ClassType(valueClass);
		verifyDataState(valueClass);

		for (int i = 0; i < allToTest.length; i++) {
			Class<?> test = allToTest[i];
			Object value = testValues[i];

			if ((valueClass.isEnum() && test.equals(Enum.class)) || test.equals(valueClass))
				TendrilAssert.assertJValue(expectedValue, type.asValue(value));
			else
				Assertions.assertThrows(DefinitionException.class, () -> type.asValue(value));
		}

		verifyDataState(valueClass);
	}

	/**
	 * Verify that the various CTORs work as expected
	 */
	@Test
	public void testCtor() {
		TendrilAssert.assertImportData(JType.class, new ClassType(JType.class));
		TendrilAssert.assertImportData("a.b.c.d", "EfGh", new ClassType("a.b.c.d.EfGh"));
		TendrilAssert.assertImportData("1.2.3.4", "Abcd", new ClassType("1.2.3.4", "Abcd"));
	}

	/**
	 * Verify that a new class data is properly generated from an existing one when a class name suffix is supplied
	 */
	@Test
	public void testGenerateFromSuffix() {
		TendrilAssert.assertImportData(VisibilityType.class.getPackageName(), VisibilityType.class.getSimpleName() + "Suffix", type.generateFromClassSuffix("Suffix"));
		TendrilAssert.assertImportData("a.b.c.d", "EfGhQwerty", new ClassType("a.b.c.d.EfGh").generateFromClassSuffix("Qwerty"));
		TendrilAssert.assertImportData("1.2.3.4", "AbcdEfgh", new ClassType("1.2.3.4", "Abcd").generateFromClassSuffix("Efgh"));
	}

	/**
	 * Verify that the assignment check is properly performed
	 */
	@Test
	public void testAssignableFrom() {
		ClassType lhs = new ClassType("a.b.c.d.E");

		// These are expected to fail
		for (PrimitiveType pd : PrimitiveType.values())
			Assertions.assertFalse(lhs.isAssignableFrom(pd));
		Assertions.assertFalse(lhs.isAssignableFrom(VoidType.INSTANCE));
		Assertions.assertFalse(lhs.isAssignableFrom(new ClassType("a.b.c.D")));
		Assertions.assertFalse(lhs.isAssignableFrom(new ClassType(JType.class)));
		Assertions.assertFalse(lhs.isAssignableFrom(new ClassType("a.b.c.d.e")));
		Assertions.assertFalse(lhs.isAssignableFrom(new ClassType(JVisibleType.class)));
		Assertions.assertFalse(lhs.isAssignableFrom(new ClassType(JField.class)));

		// These are expected to pass
		Assertions.assertTrue(lhs.isAssignableFrom(new ClassType("a.b.c.d.E")));
		Assertions.assertTrue(lhs.isAssignableFrom(new ClassType("a.b.c.d", "E")));
		lhs = new ClassType(JType.class);
		Assertions.assertTrue(lhs.isAssignableFrom(new ClassType(JType.class)));
		Assertions.assertTrue(lhs.isAssignableFrom(new ClassType(JType.class.getName())));
		Assertions.assertTrue(lhs.isAssignableFrom(new ClassType(JType.class.getPackageName(), JType.class.getSimpleName())));
		Assertions.assertTrue(lhs.isAssignableFrom(new ClassType(JVisibleType.class)));
		Assertions.assertTrue(lhs.isAssignableFrom(new ClassType(JField.class)));
	}

	/**
	 * When the class is Class (i.e.: Class<Type>) check against the nested generic class
	 */
	@Test
	public void testAssignableFromWhenClass() {
		// Class<JType>
		ClassType lhs = buildClass(new ClassType(JType.class));

		// Expected to fail
		for (PrimitiveType pd : PrimitiveType.values())
			Assertions.assertFalse(lhs.isAssignableFrom(pd));
		Assertions.assertFalse(lhs.isAssignableFrom(VoidType.INSTANCE));
		Assertions.assertFalse(lhs.isAssignableFrom(new ClassType("a.b.c.D")));
		Assertions.assertFalse(lhs.isAssignableFrom(buildClass(new ClassType("a.b.c.D"))));
		Assertions.assertFalse(lhs.isAssignableFrom(buildClass(VoidType.class)));

		// Expected to pass
		Assertions.assertTrue(lhs.isAssignableFrom(new ClassType(JType.class)));
		Assertions.assertTrue(lhs.isAssignableFrom(new ClassType(JVisibleType.class)));
		Assertions.assertTrue(lhs.isAssignableFrom(new ClassType(JField.class)));
		Assertions.assertTrue(lhs.isAssignableFrom(buildClass(JType.class)));
		Assertions.assertTrue(lhs.isAssignableFrom(buildClass(JVisibleType.class)));
		Assertions.assertTrue(lhs.isAssignableFrom(buildClass(JField.class)));

		// When generics are different, can't assign
		lhs = new ClassType(List.class);
		lhs.addGeneric(GenericFactory.create(new ClassType(JType.class)));
		ClassType rhs = new ClassType(List.class);
		rhs.addGeneric(GenericFactory.create(new ClassType(Integer.class)));
		Assertions.assertFalse(lhs.isAssignableFrom(rhs));
		// And must have the same number of generics
		rhs = new ClassType(List.class);
		rhs.addGeneric(GenericFactory.create(new ClassType(JType.class)));
		rhs.addGeneric(GenericFactory.create(new ClassType(JType.class)));
		Assertions.assertFalse(lhs.isAssignableFrom(rhs));
		// Must be identical, assignable is insufficient
		rhs = new ClassType(List.class);
		rhs.addGeneric(GenericFactory.create(new ClassType(JVisibleType.class)));
		Assertions.assertFalse(lhs.isAssignableFrom(rhs));

		// When generics are the same, can assign
		rhs = new ClassType(List.class);
		rhs.addGeneric(GenericFactory.create(new ClassType(JType.class)));
		Assertions.assertTrue(lhs.isAssignableFrom(rhs));
	}

	/**
	 * Helper for building representation of a class Class<generic>
	 * 
	 * @param klass {@link Class} that the class is "wrap"
	 * @return {@link ClassType} representation
	 */
	private ClassType buildClass(Class<?> klass) {
		return buildClass(new ClassType(klass));
	}

	/**
	 * Helper for building representation of a class Class<generic>
	 * 
	 * @param generic {@link Type} that the class is to "wrap"
	 * @return {@link ClassType} representation
	 */
	private ClassType buildClass(Type generic) {
		ClassType type = new ClassType(Class.class);
		type.addGeneric(GenericFactory.create(generic));
		return type;
	}

	/**
	 * Verify that the data state is correct
	 */
	private void verifyDataState() {
		verifyDataState(VisibilityType.class);
	}

	/**
	 * Verify that the data state is correct
	 */
	private void verifyDataState(Class<?> expectedClass) {
		verifyDataState(expectedClass.getSimpleName(), false);
	}

	/**
	 * Verify that the fully qualified name is proper generated
	 */
	@Test
	public void testFullyQualifiedName() {
		ClassType data = new ClassType(JType.class);
		Assertions.assertEquals(JType.class.getName(), data.getFullyQualifiedName());
		Assertions.assertEquals(JType.class.getName(), data.toString());

		data = new ClassType("a.b.c.d.EfGh");
		Assertions.assertEquals("a.b.c.d.EfGh", data.getFullyQualifiedName());
		Assertions.assertEquals("a.b.c.d.EfGh", data.toString());

		data = new ClassType("1.2.3.4", "Abcd");
		Assertions.assertEquals("1.2.3.4.Abcd", data.getFullyQualifiedName());
		Assertions.assertEquals("1.2.3.4.Abcd", data.toString());
	}

	/**
	 * Verify that a class that appears in the default package generates an exception
	 */
	@Test
	public void testClassInDefaultPackage() {
		Assertions.assertThrows(DefinitionException.class, () -> new ClassType("SomeClass"));
		Assertions.assertThrows(DefinitionException.class, () -> new ClassType(null, "SomeOtherClass"));
		Assertions.assertThrows(DefinitionException.class, () -> new ClassType("", "YetOtherClass"));
		Assertions.assertThrows(DefinitionException.class, () -> new ClassType(" ", "MyClass"));
		Assertions.assertThrows(DefinitionException.class, () -> new ClassType("        ", "MyOtherClass"));
	}

	/**
	 * Verify that the hash code is properly generated
	 */
	@Test
	public void testHashCode() {
		String packageName = "my.package.com.abc123";
		String className = "MyClassName";
		Assertions.assertEquals(packageName.hashCode() + className.hashCode(), new ClassType(packageName, className).hashCode());
	}

	/**
	 * Verify that equals works as expected
	 */
	@SuppressWarnings("unlikely-arg-type")
	@Test
	public void testEquals() {
		ClassType lhs = new ClassType("a.b.c.d.E");

		// These are expected to fail
		Assertions.assertFalse(lhs.equals("abc123"));
		Assertions.assertFalse(lhs.equals(new ClassType("a.b.c.D")));
		Assertions.assertFalse(lhs.equals(new ClassType(JType.class)));
		Assertions.assertFalse(lhs.equals(new ClassType("a.b.c.d.e")));

		// These are expected to pass
		Assertions.assertTrue(lhs.equals(new ClassType("a.b.c.d.E")));
		Assertions.assertTrue(lhs.equals(new ClassType("a.b.c.d", "E")));
		lhs = new ClassType(JType.class);
		Assertions.assertTrue(lhs.equals(new ClassType(JType.class)));
		Assertions.assertTrue(lhs.equals(new ClassType(JType.class.getName())));
		Assertions.assertTrue(lhs.equals(new ClassType(JType.class.getPackageName(), JType.class.getSimpleName())));
	}

	/**
	 * Verify that the correct Class is retrieved
	 */
	@Test
	public void testGetDefinedClass() {
		// For a valid class... no exception is to be thrown
		try {
			Assertions.assertEquals(ClassType.class, new ClassType(ClassType.class).getDefinedClass());
			Assertions.assertEquals(Integer.class, new ClassType(Integer.class).getDefinedClass());
			Assertions.assertEquals(Type.class, new ClassType(Type.class).getDefinedClass());
			Assertions.assertEquals(Override.class, new ClassType(Override.class).getDefinedClass());
			Assertions.assertEquals(AbstractUnitTest.class, new ClassType(AbstractUnitTest.class).getDefinedClass());
		} catch (ClassNotFoundException e) {
			Assertions.fail(e);
		}
	}

	/**
	 * Verify that the asClassType simply returns itself
	 */
	@Test
	public void testAsClassType() {
		Assertions.assertTrue(type == type.asClassType());
	}
}
