package tendril.codegen.classes;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.codegen.VisibilityType;
import tendril.codegen.classes.method.ConcreteMethodBuilder;
import tendril.codegen.field.type.TypeData;
import tendril.dom.type.Type;
import tendril.dom.type.core.ClassType;
import test.AbstractUnitTest;
import test.assertions.ClassAssert;

/**
 * Test case for {@link JClassDefault}
 */
public class JClassDefaultTest extends AbstractUnitTest {

	// Mocks to use for testing
	@Mock
	private VisibilityType mockVisibility;
	@Mock
	private ClassType mockClassType;
	@Mock
	private TypeData<Type> mockReturnType;

	// Instance to test
	private JClassDefault cls;

	/**
	 * @see test.AbstractUnitTest#prepareTest()
	 */
	@Override
	protected void prepareTest() {
		when(mockClassType.getPackageName()).thenReturn("packageName");
		when(mockClassType.getClassName()).thenReturn("ClassName");
		cls = new JClassDefault(mockVisibility, mockClassType);
		verify(mockClassType).getPackageName();
		verify(mockClassType).getClassName();
	}

	/**
	 * Verify that the class type is properly prepared
	 */
	@Test
	public void testClassType() {
		Assertions.assertEquals("class", cls.classType());
	}

	/**
	 * Verify that the method builder is properly created
	 */
	@Test
	public void testMethodBuilder() {
		ClassAssert.assertInstance(ConcreteMethodBuilder.class, cls.createMethodBuilder(mockReturnType, "someName"));
	}

}
