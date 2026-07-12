package tendril.processor.recipe;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.codegen.VisibilityType;
import tendril.codegen.classes.JClass;
import tendril.codegen.field.JContainedType;
import tendril.codegen.field.JVisibleType;
import tendril.codegen.field.type.ClassType;
import tendril.test.AbstractUnitTest;

/**
 * Test case for {@link RecipeGeneratorHelper}
 */
public class RecipeGeneratorHelperTest extends AbstractUnitTest {

	// Mocks to use for testing
	@Mock
	private ClassType mockClassType;
	@Mock
	private ClassType mockClassTypeAsType;
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
	public void testClassReferenceWithGeneric() {
		when(mockClassType.asClassType()).thenReturn(mockClassTypeAsType);
		when(mockClassTypeAsType.getFullyQualifiedName()).thenReturn("Other");
		when(mockClassType.hasGenerics()).thenReturn(true);
		when(mockClassType.getCodeName()).thenReturn("Simple");
		Assertions.assertEquals("(Class<Simple>) (Class<?>) Other.class", RecipeGeneratorHelper.getClassReference(mockClassType));
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
