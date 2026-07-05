package tendril.processor;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;

import javax.annotation.processing.Generated;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.bean.duplicate.Duplicate;
import tendril.codegen.JBase;
import tendril.codegen.annotation.JAnnotation;
import tendril.codegen.classes.method.JMethod;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.TypeFactory;
import tendril.codegen.field.value.JValue;
import tendril.codegen.field.value.JValueClass;
import tendril.codegen.generics.GenericType;
import tendril.test.AbstractUnitTest;

/**
 * Test case for {@link BlueprintHelper}
 */
public class BlueprintHelperTest extends AbstractUnitTest {

	// Mocks to use for testing
	@Mock
	private JBase mockBase;
	@Mock
	private JAnnotation mockInvalidAnnotation1;
	@Mock
	private JAnnotation mockInvalidAnnotation2;
	@Mock
	private JAnnotation mockInvalidAnnotation3;
	@Mock
	private JAnnotation mockValidAnnotation;
	@Mock
	private JMethod<?> mockInvalidMethod1;
	@Mock
	private JMethod<?> mockInvalidMethod2;
	@Mock
	private JMethod<?> mockInvalidMethod3;
	@Mock
	private JMethod<?> mockValidMethod;
	@Mock
	private JValue<?, ?> mockValue;
	@Mock
	private JValueClass mockValueClass;
	@Mock
	private ClassType mockValueClassType;
	@Mock
	private GenericType mockGenericType;
	@Mock
	private ClassType mockClassType1;
	@Mock
	private ClassType mockClassType2;

	/**
	 * @see tendril.test.AbstractUnitTest#prepareTest()
	 */
	@Override
	protected void prepareTest() {
		lenient().when(mockInvalidAnnotation1.getType()).thenReturn(TypeFactory.createClassType(Mock.class));
		lenient().when(mockInvalidAnnotation1.getType()).thenReturn(TypeFactory.createClassType(Generated.class));
		lenient().when(mockInvalidAnnotation1.getType()).thenReturn(TypeFactory.createClassType(Override.class));
		lenient().when(mockValidAnnotation.getType()).thenReturn(TypeFactory.createClassType(Duplicate.class));
		lenient().when(mockInvalidMethod1.getName()).thenReturn("blah");
		lenient().when(mockInvalidMethod2.getName()).thenReturn("abc123");
		lenient().when(mockInvalidMethod3.getName()).thenReturn("values");
		lenient().when(mockValidMethod.getName()).thenReturn("value");
	}

	/**
	 * Verify no blueprint retrieved when no (valid) annotation is present
	 */
	@Test
	public void testRetrieveBlueprintNoAnnotation() {
		// No annotations
		when(mockBase.getAnnotations()).thenReturn(Collections.emptyList());
		Assertions.assertNull(BlueprintHelper.retrieveBlueprint(mockBase));
		verify(mockBase).getAnnotations();
	}

	/**
	 * Verify no blueprint retrieved when no (valid) annotation is present
	 */
	@Test
	public void testRetrieveBlueprintNoValidAnnotation() {
		// Unrelated annotations
		when(mockBase.getAnnotations()).thenReturn(Arrays.asList(mockInvalidAnnotation1, mockInvalidAnnotation2, mockInvalidAnnotation3));
		Assertions.assertNull(BlueprintHelper.retrieveBlueprint(mockBase));
		verify(mockBase).getAnnotations();
		verify(mockInvalidAnnotation1).getType();
		verify(mockInvalidAnnotation2).getType();
		verify(mockInvalidAnnotation3).getType();
	}

	/**
	 * Verify no blueprint retrieved when the valid annotation has different methods
	 */
	@Test
	public void testRetrieveBlueprintNoMethod() {
		// No method is present
		when(mockBase.getAnnotations()).thenReturn(Arrays.asList(mockInvalidAnnotation1, mockInvalidAnnotation2, mockValidAnnotation, mockInvalidAnnotation3));
		when(mockValidAnnotation.getAttributes()).thenReturn(Collections.emptyList());
		
		Assertions.assertNull(BlueprintHelper.retrieveBlueprint(mockBase));
		verify(mockBase).getAnnotations();
		verify(mockInvalidAnnotation1).getType();
		verify(mockInvalidAnnotation2).getType();
		verify(mockValidAnnotation).getType();
		verify(mockValidAnnotation).getAttributes();
		verify(mockInvalidAnnotation3).getType();
	}

	/**
	 * Verify no blueprint retrieved when the valid annotation has different methods
	 */
	@Test
	public void testRetrieveBlueprintNoValidMethod() {
		// Invalid methods are present
		when(mockBase.getAnnotations()).thenReturn(Arrays.asList(mockValidAnnotation, mockInvalidAnnotation1, mockInvalidAnnotation2, mockInvalidAnnotation3));
		when(mockValidAnnotation.getAttributes()).thenReturn(Arrays.asList(mockInvalidMethod1, mockInvalidMethod2, mockInvalidMethod3));
		
		Assertions.assertNull(BlueprintHelper.retrieveBlueprint(mockBase));
		verify(mockBase).getAnnotations();
		verify(mockInvalidAnnotation1).getType();
		verify(mockInvalidAnnotation2).getType();
		verify(mockValidAnnotation).getType();
		verify(mockValidAnnotation).getAttributes();
		verify(mockInvalidMethod1).getName();
		verify(mockInvalidMethod2).getName();
		verify(mockInvalidMethod3).getName();
		verify(mockInvalidAnnotation3).getType();
	}

	/**
	 * Verify no blueprint retrieved if it returns the wrong type
	 */
	@Test
	public void testRetrieveBlueprintWrongType() {
		// Invalid methods are present
		when(mockBase.getAnnotations()).thenReturn(Arrays.asList(mockValidAnnotation, mockInvalidAnnotation1, mockInvalidAnnotation2, mockInvalidAnnotation3));
		when(mockValidAnnotation.getAttributes()).thenReturn(Arrays.asList(mockInvalidMethod1, mockInvalidMethod2, mockValidMethod, mockInvalidMethod3));
		doReturn(mockValue).when(mockValidAnnotation).getValue(mockValidMethod);
		
		Assertions.assertNull(BlueprintHelper.retrieveBlueprint(mockBase));
		verify(mockBase).getAnnotations();
		verify(mockInvalidAnnotation1).getType();
		verify(mockInvalidAnnotation2).getType();
		verify(mockValidAnnotation).getType();
		verify(mockValidAnnotation).getAttributes();
		verify(mockInvalidMethod1).getName();
		verify(mockInvalidMethod2).getName();
		verify(mockValidMethod).getName();
		verify(mockValidAnnotation).getValue(mockValidMethod);
		verify(mockInvalidMethod3).getName();
		verify(mockInvalidAnnotation3).getType();
	}
	
	/**
	 * Verify the blueprint can be retrieved successfully from the correct method
	 */
	@Test
	public void testRetrieveBlueprint() {
		// Invalid methods are present
		when(mockBase.getAnnotations()).thenReturn(Arrays.asList(mockValidAnnotation, mockInvalidAnnotation1, mockInvalidAnnotation2, mockInvalidAnnotation3));
		when(mockValidAnnotation.getAttributes()).thenReturn(Arrays.asList(mockValidMethod, mockInvalidMethod1, mockInvalidMethod2, mockInvalidMethod3));
		doReturn(mockValueClass).when(mockValidAnnotation).getValue(mockValidMethod);
		when(mockValueClass.getValue()).thenReturn(mockClassType1);
		when(mockClassType1.getGenerics()).thenReturn(Collections.singletonList(mockGenericType));
		when(mockGenericType.asClassType()).thenReturn(mockClassType2);
		
		Assertions.assertEquals(mockClassType2, BlueprintHelper.retrieveBlueprint(mockBase));
		verify(mockBase).getAnnotations();
		verify(mockValidAnnotation).getType();
		verify(mockValidAnnotation).getAttributes();
		verify(mockValidMethod).getName();
		verify(mockValidAnnotation).getValue(mockValidMethod);
		verify(mockValueClass).getValue();
		verify(mockValueClass).getValue();
		verify(mockClassType1).getGenerics();
		verify(mockGenericType).asClassType();
	}
}
