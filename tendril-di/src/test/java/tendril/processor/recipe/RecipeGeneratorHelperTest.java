package tendril.processor.recipe;

import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.codegen.DefinitionException;
import tendril.codegen.VisibilityType;
import tendril.codegen.classes.JClass;
import tendril.codegen.field.JContainedType;
import tendril.codegen.field.JVisibleType;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.TypeFactory;
import tendril.codegen.generics.GenericFactory;
import tendril.codegen.generics.GenericType;
import tendril.test.AbstractUnitTest;

/**
 * Test case for {@link RecipeGeneratorHelper}
 */
public class RecipeGeneratorHelperTest extends AbstractUnitTest {
	
	private static final String typeFactoryCreate = TypeFactory.class.getName() + ".createClassType(";
	private static final String genericFactoryCreate = GenericFactory.class.getName() + ".create(";

	// Mocks to use for testing
	@Mock
	private ClassType mockClassType;
	@Mock
	private ClassType mockClassTypeAsType;
	@Mock
	private GenericType mockGeneric1;
	@Mock
	private GenericType mockGeneric2;
	@Mock
	private ClassType mockGenericType1;
	@Mock
	private ClassType mockGenericType2;
	@Mock
	private JVisibleType<?> mockElement;
	@Mock
	private JClass mockClassContainer;
	@Mock
	private ClassType mockContainerType;
	
	/**
	 * @see tendril.test.AbstractUnitTest#prepareTest()
	 */
	@Override
	protected void prepareTest() {
		// Not required
	}

	/**
	 * Verify the class reference is properly prepared
	 */
	@Test
	public void testClassReferenceNotGeneric() {
		when(mockClassType.asClassType()).thenReturn(mockClassTypeAsType);
		when(mockClassTypeAsType.getFullyQualifiedName()).thenReturn("ClassName");
		when(mockClassType.hasGenerics()).thenReturn(false);
		Assertions.assertEquals("ClassName.class", RecipeGeneratorHelper.getClassReference(mockClassType));
	}

	/**
	 * Verify the class reference is properly prepared
	 */
	@Test
	public void testClassReferenceWithSingleConcreteGeneric() {
		when(mockClassType.hasGenerics()).thenReturn(true);
		when(mockClassType.getGenerics()).thenReturn(Collections.singletonList(mockGeneric1));
		when(mockClassType.getFullyQualifiedName()).thenReturn("ClassType");
		when(mockGeneric1.asClassType()).thenReturn(mockGenericType1);
		when(mockGenericType1.hasGenerics()).thenReturn(false);
		when(mockGenericType1.getFullyQualifiedName()).thenReturn("GenericType");
		Assertions.assertEquals(typeFactoryCreate + "ClassType.class, " + genericFactoryCreate + typeFactoryCreate + "GenericType.class)))", RecipeGeneratorHelper.getClassReference(mockClassType));
	}

	/**
	 * Verify the class reference is properly prepared
	 */
	@Test
	public void testClassReferenceWithTwoConcreteGenerics() {
		when(mockClassType.hasGenerics()).thenReturn(true);
		when(mockClassType.getGenerics()).thenReturn(Arrays.asList(mockGeneric1, mockGeneric2));
		when(mockClassType.getFullyQualifiedName()).thenReturn("ClassType");
		when(mockGeneric1.asClassType()).thenReturn(mockGenericType1);
		when(mockGeneric2.asClassType()).thenReturn(mockGenericType2);
		when(mockGenericType1.hasGenerics()).thenReturn(false);
		when(mockGenericType1.getFullyQualifiedName()).thenReturn("GenericType1");
		when(mockGenericType2.hasGenerics()).thenReturn(false);
		when(mockGenericType2.getFullyQualifiedName()).thenReturn("GenericType2");
		Assertions.assertEquals(typeFactoryCreate + "ClassType.class, " + genericFactoryCreate + typeFactoryCreate + "GenericType1.class)), " + genericFactoryCreate + typeFactoryCreate + "GenericType2.class)))", RecipeGeneratorHelper.getClassReference(mockClassType));
	}

	/**
	 * Verify the class reference is properly prepared
	 */
	@Test
	public void testClassReferenceWithSingleWildcardGeneric() {
		when(mockClassType.hasGenerics()).thenReturn(true);
		when(mockClassType.getGenerics()).thenReturn(Collections.singletonList(mockGeneric1));
		when(mockClassType.getFullyQualifiedName()).thenReturn("ClassType");
		when(mockGeneric1.asClassType()).thenThrow(new DefinitionException(""));
		Assertions.assertEquals(typeFactoryCreate + "ClassType.class, " + genericFactoryCreate + typeFactoryCreate + Object.class.getName() + ".class)))", RecipeGeneratorHelper.getClassReference(mockClassType));
	}

	/**
	 * Verify the class reference is properly prepared
	 */
	@Test
	public void testClassReferenceWithTwoWildcardGenerics() {
		when(mockClassType.hasGenerics()).thenReturn(true);
		when(mockClassType.getGenerics()).thenReturn(Arrays.asList(mockGeneric1, mockGeneric2));
		when(mockClassType.getFullyQualifiedName()).thenReturn("ClassType");
		when(mockGeneric1.asClassType()).thenThrow(new DefinitionException(""));
		when(mockGeneric2.asClassType()).thenThrow(new DefinitionException(""));
		Assertions.assertEquals(typeFactoryCreate + "ClassType.class, " + genericFactoryCreate + typeFactoryCreate + Object.class.getName() + ".class)), " + genericFactoryCreate + typeFactoryCreate + Object.class.getName() + ".class)))", RecipeGeneratorHelper.getClassReference(mockClassType));
	}

	/**
	 * Verify the class reference is properly prepared
	 */
	@Test
	public void testClassReferenceWithMixedGenerics() {
		when(mockClassType.hasGenerics()).thenReturn(true);
		when(mockClassType.getGenerics()).thenReturn(Arrays.asList(mockGeneric1, mockGeneric2));
		when(mockClassType.getFullyQualifiedName()).thenReturn("ClassType");
		when(mockGeneric1.asClassType()).thenReturn(mockGenericType1);
		when(mockGenericType1.hasGenerics()).thenReturn(false);
		when(mockGenericType1.getFullyQualifiedName()).thenReturn("GenericType");
		when(mockGeneric2.asClassType()).thenThrow(new DefinitionException(""));
		Assertions.assertEquals(typeFactoryCreate + "ClassType.class, " + genericFactoryCreate + typeFactoryCreate + "GenericType.class)), " + genericFactoryCreate + typeFactoryCreate + Object.class.getName() + ".class)))", RecipeGeneratorHelper.getClassReference(mockClassType));
	}

	/**
	 * Verify the class reference is properly prepared
	 */
	@Test
	public void testClassReferenceWithNestedConcreteGenerics() {
		when(mockClassType.hasGenerics()).thenReturn(true);
		when(mockClassType.getGenerics()).thenReturn(Collections.singletonList(mockGeneric1));
		when(mockClassType.getFullyQualifiedName()).thenReturn("ClassType");
		when(mockGeneric1.asClassType()).thenReturn(mockGenericType1);
		when(mockGeneric2.asClassType()).thenReturn(mockGenericType2);
		when(mockGenericType1.hasGenerics()).thenReturn(true);
		when(mockGenericType1.getGenerics()).thenReturn(Collections.singletonList(mockGeneric2));
		when(mockGenericType1.getFullyQualifiedName()).thenReturn("GenericType1");
		when(mockGenericType2.hasGenerics()).thenReturn(false);
		when(mockGenericType2.getFullyQualifiedName()).thenReturn("GenericType2");
		Assertions.assertEquals(typeFactoryCreate + "ClassType.class, " + genericFactoryCreate + typeFactoryCreate + "GenericType1.class, " + genericFactoryCreate + typeFactoryCreate + "GenericType2.class)))))", RecipeGeneratorHelper.getClassReference(mockClassType));
	}

	/**
	 * Verify the class reference is properly prepared
	 */
	@Test
	public void testClassReferenceWithNestedMixedGenerics() {
		when(mockClassType.hasGenerics()).thenReturn(true);
		when(mockClassType.getGenerics()).thenReturn(Collections.singletonList(mockGeneric1));
		when(mockClassType.getFullyQualifiedName()).thenReturn("ClassType");
		when(mockGeneric1.asClassType()).thenReturn(mockGenericType1);
		when(mockGeneric2.asClassType()).thenReturn(mockGenericType2);
		when(mockGenericType1.hasGenerics()).thenReturn(true);
		when(mockGenericType1.getGenerics()).thenReturn(Collections.singletonList(mockGeneric2));
		when(mockGenericType1.getFullyQualifiedName()).thenReturn("GenericType1");
		when(mockGeneric2.asClassType()).thenThrow(new DefinitionException(""));
		Assertions.assertEquals(typeFactoryCreate + "ClassType.class, " + genericFactoryCreate + typeFactoryCreate + "GenericType1.class, " + genericFactoryCreate + typeFactoryCreate + Object.class.getName() + ".class)))))", RecipeGeneratorHelper.getClassReference(mockClassType));
	}
	
	/**
	 * Verify that the need for reflection can be properly determined
	 */
	@Test
	public void testRequireReflectionNoContainer() {
		when(mockElement.getContainer()).thenReturn(null);
		Assertions.assertFalse(RecipeGeneratorHelper.requiresReflection(mockClassType, mockElement));
	}
	
	/**
	 * Verify that the need for reflection can be properly determined
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testRequireReflectionNoClassContainer() {
		when(mockElement.getContainer()).thenReturn((JContainedType) mockElement);
		Assertions.assertTrue(RecipeGeneratorHelper.requiresReflection(mockClassType, mockElement));
	}
	
	/**
	 * Verify that the need for reflection can be properly determined
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testRequireReflectionSamePackage() {
		when(mockElement.getContainer()).thenReturn((JContainedType) mockClassContainer);
		when(mockClassContainer.getType()).thenReturn(mockContainerType);
		when(mockContainerType.getPackageName()).thenReturn("a");
		when(mockClassType.getPackageName()).thenReturn("a");

		when(mockElement.getVisibility()).thenReturn(VisibilityType.PRIVATE);
		Assertions.assertTrue(RecipeGeneratorHelper.requiresReflection(mockClassType, mockElement));
		when(mockElement.getVisibility()).thenReturn(VisibilityType.PACKAGE_PRIVATE);
		Assertions.assertFalse(RecipeGeneratorHelper.requiresReflection(mockClassType, mockElement));
		when(mockElement.getVisibility()).thenReturn(VisibilityType.PROTECTED);
		Assertions.assertFalse(RecipeGeneratorHelper.requiresReflection(mockClassType, mockElement));
		when(mockElement.getVisibility()).thenReturn(VisibilityType.PUBLIC);
		Assertions.assertFalse(RecipeGeneratorHelper.requiresReflection(mockClassType, mockElement));
	}
	
	/**
	 * Verify that the need for reflection can be properly determined
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testRequireReflectionDifferentPackage() {
		when(mockElement.getContainer()).thenReturn((JContainedType) mockClassContainer);
		when(mockClassContainer.getType()).thenReturn(mockContainerType);
		when(mockContainerType.getPackageName()).thenReturn("a");
		when(mockClassType.getPackageName()).thenReturn("b");

		when(mockElement.getVisibility()).thenReturn(VisibilityType.PRIVATE);
		Assertions.assertTrue(RecipeGeneratorHelper.requiresReflection(mockClassType, mockElement));
		when(mockElement.getVisibility()).thenReturn(VisibilityType.PACKAGE_PRIVATE);
		Assertions.assertTrue(RecipeGeneratorHelper.requiresReflection(mockClassType, mockElement));
		when(mockElement.getVisibility()).thenReturn(VisibilityType.PROTECTED);
		Assertions.assertTrue(RecipeGeneratorHelper.requiresReflection(mockClassType, mockElement));
		when(mockElement.getVisibility()).thenReturn(VisibilityType.PUBLIC);
		Assertions.assertFalse(RecipeGeneratorHelper.requiresReflection(mockClassType, mockElement));
	}
}
