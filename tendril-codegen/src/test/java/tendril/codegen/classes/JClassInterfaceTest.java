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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.codegen.DefinitionException;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.Type;
import tendril.test.AbstractUnitTest;

/**
 * Test case for {@link JClassInterface}
 */
public class JClassInterfaceTest extends AbstractUnitTest {

	// Mocks to use for testing
	@Mock
	private ClassType mockClassType;
	@Mock
	private JClass mockJClass;
	@Mock
	private Type mockReturnType;

	// Instance to test
	private JClassInterface cls;

	/**
	 * @see tendril.test.AbstractUnitTest#prepareTest()
	 */
	@Override
	protected void prepareTest() {
		when(mockClassType.getPackageName()).thenReturn("packageName");
		when(mockClassType.getClassName()).thenReturn("ClassName");
		cls = new JClassInterface(mockClassType);
		verify(mockClassType).getPackageName();
		verify(mockClassType).getClassName();
	}

	/**
	 * Verify that the class type is properly prepared
	 */
	@Test
	public void testClassType() {
		Assertions.assertEquals("interface ", cls.getClassKeyword());
	}
	
	/**
	 * Verify that the class hierarchy is properly processed with the interface
	 */
	@Test
	public void testClassHierarchy() {
	    Assertions.assertThrows(DefinitionException.class, () -> cls.setParentClass(mockJClass));
	    verify(mockClassType).getFullyQualifiedName();
	    cls.setParentClass(null);
	    Assertions.assertEquals("extends ", cls.interfaceExtensionKeyword());
	}
}
