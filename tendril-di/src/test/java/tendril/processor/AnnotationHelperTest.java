package tendril.processor;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.bean.Bean;
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
 * Test case for {@link AnnotationHelper}
 */
public class AnnotationHelperTest extends AbstractUnitTest {

	// Mocks to use for testing
	@Mock
	private JBase mockBase;
	@Mock
	private ClassType mockAnnotationType;
	@Mock
	private JAnnotation mockAnnotation;
	@Mock
	private JMethod<?> mockMethod1;
	@Mock
	private JMethod<?> mockMethod2;
	@Mock
	private JMethod<?> mockMethod3;
	@Mock
	private JMethod<?> mockMethod4;
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
		lenient().when(mockMethod1.getName()).thenReturn("blah");
		lenient().when(mockMethod2.getName()).thenReturn("abc123");
		lenient().when(mockMethod3.getName()).thenReturn("values");
		lenient().when(mockMethod4.getName()).thenReturn("value");
	}

	/**
	 * Verify no value retrieved when no (valid) annotation is present
	 */
	@Test
	public void testRetrieveClassValuetNoAnnotation() {
		// No annotations
		when(mockBase.getAnnotation(mockAnnotationType)).thenReturn(null);
		Assertions.assertNull(AnnotationHelper.retrieveClassType(mockBase, mockAnnotationType, null));
		verify(mockBase).getAnnotation(mockAnnotationType);
	}

	/**
	 * Verify no value retrieved when the valid annotation has different methods
	 */
	@Test
	public void testRetrieveClassValueNoMethod() {
		// No method is present
		when(mockBase.getAnnotation(mockAnnotationType)).thenReturn(mockAnnotation);
		when(mockAnnotation.getAttributes()).thenReturn(Collections.emptyList());

		Assertions.assertNull(AnnotationHelper.retrieveClassType(mockBase, mockAnnotationType, null));
		verify(mockBase).getAnnotation(mockAnnotationType);
		verify(mockAnnotation).getAttributes();
	}

	/**
	 * Verify no value retrieved when the valid annotation has different methods
	 */
	@Test
	public void testRetrieveClassValueNoValidMethod() {
		// Invalid methods are present
		when(mockBase.getAnnotation(mockAnnotationType)).thenReturn(mockAnnotation);
		when(mockAnnotation.getAttributes()).thenReturn(Arrays.asList(mockMethod1, mockMethod2, mockMethod3, mockMethod4));

		Assertions.assertNull(AnnotationHelper.retrieveClassType(mockBase, mockAnnotationType, "qwerty"));
		verify(mockBase).getAnnotation(mockAnnotationType);
		verify(mockAnnotation).getAttributes();
		verify(mockMethod1).getName();
		verify(mockMethod2).getName();
		verify(mockMethod3).getName();
		verify(mockMethod4).getName();
	}

	/**
	 * Verify no value retrieved if it returns the wrong type
	 */
	@Test
	public void testRetrieveClassValueWrongType() {
		// Invalid methods are present
		when(mockBase.getAnnotation(mockAnnotationType)).thenReturn(mockAnnotation);
		when(mockAnnotation.getAttributes()).thenReturn(Arrays.asList(mockMethod1, mockMethod2, mockMethod3, mockMethod4));
		doReturn(mockValue).when(mockAnnotation).getValue(mockMethod2);

		Assertions.assertNull(AnnotationHelper.retrieveClassType(mockBase, mockAnnotationType, "abc123"));
		verify(mockBase).getAnnotation(mockAnnotationType);
		verify(mockAnnotation).getAttributes();
		verify(mockMethod1).getName();
		verify(mockMethod2).getName();
		verify(mockAnnotation).getValue(mockMethod2);
		verify(mockMethod3).getName();
		verify(mockMethod4).getName();
	}

	/**
	 * Verify the value can be retrieved successfully from the correct method
	 */
	@Test
	public void testRetrieveClassValue() {
		// Invalid methods are present
		when(mockBase.getAnnotation(mockAnnotationType)).thenReturn(mockAnnotation);
		when(mockAnnotation.getAttributes()).thenReturn(Arrays.asList(mockMethod1, mockMethod2, mockMethod3, mockMethod4));
		doReturn(mockValueClass).when(mockAnnotation).getValue(mockMethod3);
		when(mockValueClass.getValue()).thenReturn(mockClassType1);
		when(mockClassType1.getGenerics()).thenReturn(Collections.singletonList(mockGenericType));
		when(mockGenericType.asClassType()).thenReturn(mockClassType2);

		Assertions.assertEquals(mockClassType2, AnnotationHelper.retrieveClassType(mockBase, mockAnnotationType, "values"));
		verify(mockBase).getAnnotation(mockAnnotationType);
		verify(mockAnnotation).getAttributes();
		verify(mockMethod1).getName();
		verify(mockMethod2).getName();
		verify(mockMethod3).getName();
		verify(mockAnnotation).getValue(mockMethod3);
		verify(mockValueClass).getValue();
		verify(mockValueClass).getValue();
		verify(mockClassType1).getGenerics();
		verify(mockGenericType).asClassType();
	}

	/**
	 * Verify the blueprint values can be retrieved successfully from the correct method
	 */
	@Test
	public void testRetrieveBlueprintValue() {
		testSpecificHelper(Duplicate.class, (e) -> AnnotationHelper.retrieveDuplicateBlueprint(e));
	}

	/**
	 * Verify the bean override values can be retrieved successfully from the correct method
	 */
	@Test
	public void testRetrieveOverrideValue() {
		testSpecificHelper(Bean.class, (e) -> AnnotationHelper.retrieveBeanOverride(e));
	}

	/**
	 * Helper to obscure what method is called
	 */
	private interface Caller {
		ClassType getClassType(JBase element);
	}

	/**
	 * Perform the test for calling a specific {@link ClassType} retrieval method
	 * 
	 * @param annotationClass {@link Class} extending {@link Annotation} of the annotation which is to be looked for
	 * @param methodCall      {@link Caller} which calls the specific {@link AnnotationHelper} method
	 */
	private void testSpecificHelper(Class<? extends Annotation> annotationClass, Caller methodCall) {
		ClassType specificAnnotationType = TypeFactory.createClassType(annotationClass);

		// Invalid methods are present
		when(mockBase.getAnnotation(specificAnnotationType)).thenReturn(mockAnnotation);
		when(mockAnnotation.getAttributes()).thenReturn(Arrays.asList(mockMethod1, mockMethod2, mockMethod3, mockMethod4));
		doReturn(mockValueClass).when(mockAnnotation).getValue(mockMethod4);
		when(mockValueClass.getValue()).thenReturn(mockClassType1);
		when(mockClassType1.getGenerics()).thenReturn(Collections.singletonList(mockGenericType));
		when(mockGenericType.asClassType()).thenReturn(mockClassType2);

		Assertions.assertEquals(mockClassType2, methodCall.getClassType(mockBase));
		verify(mockBase).getAnnotation(specificAnnotationType);
		verify(mockAnnotation).getAttributes();
		verify(mockMethod1).getName();
		verify(mockMethod2).getName();
		verify(mockMethod3).getName();
		verify(mockMethod4).getName();
		verify(mockAnnotation).getValue(mockMethod4);
		verify(mockValueClass).getValue();
		verify(mockValueClass).getValue();
		verify(mockClassType1).getGenerics();
		verify(mockGenericType).asClassType();
	}
}
