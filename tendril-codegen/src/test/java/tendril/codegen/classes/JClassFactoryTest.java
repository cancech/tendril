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
package tendril.codegen.classes;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.codegen.VisibilityType;
import tendril.codegen.field.type.ClassType;
import tendril.test.AbstractUnitTest;
import tendril.test.assertions.ClassAssert;

/**
 * Test case for {@link JClassFactory}
 */
public class JClassFactoryTest extends AbstractUnitTest {
	
	// Mocks to use for testing
	@Mock
	private VisibilityType mockVisibility;
	@Mock
	private ClassType mockClassType;

	/**
	 * @see tendril.test.AbstractUnitTest#prepareTest()
	 */
	@Override
	protected void prepareTest() {
		when(mockClassType.getPackageName()).thenReturn("packageName");
		when(mockClassType.getClassName()).thenReturn("ClassName");
	}

	/**
	 * Verify that the appropriate instance is created
	 */
	@Test
	public void testDefaultCreation() {
		ClassAssert.assertInstance(JClassDefault.class, JClassFactory.createClass(mockVisibility, mockClassType));
		verify(mockClassType).getPackageName();
		verify(mockClassType).getClassName();
	}

	/**
	 * Verify that the appropriate instance is created
	 */
	@Test
	public void testAbstractCreation() {
		ClassAssert.assertInstance(JClassAbstract.class, JClassFactory.createAbstractClass(mockVisibility, mockClassType));
		verify(mockClassType).getPackageName();
		verify(mockClassType).getClassName();
	}

	/**
	 * Verify that the appropriate instance is created
	 */
	@Test
	public void testInterfaceCreation() {
	    // Visibilities that are not allowed
        Assertions.assertThrows(IllegalArgumentException.class, () -> JClassFactory.createInterface(VisibilityType.PROTECTED, mockClassType));
        Assertions.assertThrows(IllegalArgumentException.class, () -> JClassFactory.createInterface(VisibilityType.PRIVATE, mockClassType));
        
        // Visibilities that are allowed
        ClassAssert.assertInstance(JClassInterface.class, JClassFactory.createInterface(VisibilityType.PUBLIC, mockClassType));
        verify(mockClassType).getPackageName();
        verify(mockClassType).getClassName();
        ClassAssert.assertInstance(JClassInterface.class, JClassFactory.createInterface(VisibilityType.PACKAGE_PRIVATE, mockClassType));
        verify(mockClassType, times(2)).getPackageName();
        verify(mockClassType, times(2)).getClassName();

	}

	/**
	 * Verify that the appropriate instance is created
	 */
	@Test
	public void testAnnotationCreation() {
        // Visibilities that are not allowed
        Assertions.assertThrows(IllegalArgumentException.class, () -> JClassFactory.createAnnotation(VisibilityType.PROTECTED, mockClassType));
        Assertions.assertThrows(IllegalArgumentException.class, () -> JClassFactory.createAnnotation(VisibilityType.PRIVATE, mockClassType));
        
        ClassAssert.assertInstance(JClassAnnotation.class, JClassFactory.createAnnotation(VisibilityType.PUBLIC, mockClassType));
        verify(mockClassType).getPackageName();
        verify(mockClassType).getClassName();
        ClassAssert.assertInstance(JClassAnnotation.class, JClassFactory.createAnnotation(VisibilityType.PACKAGE_PRIVATE, mockClassType));
        verify(mockClassType, times(2)).getPackageName();
        verify(mockClassType, times(2)).getClassName();
	}
}
