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

import java.util.Collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.codegen.field.type.ClassType;
import tendril.test.AbstractUnitTest;

/**
 * Test case for {@link JClassAnnotation}
 */
public class JClassAnnotationTest extends AbstractUnitTest {

    // Mocks to use for testing
    @Mock
    private ClassType mockClassType;

    // Instance to test
    private JClassAnnotation cls;

    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        when(mockClassType.getPackageName()).thenReturn("packageName");
        when(mockClassType.getClassName()).thenReturn("ClassName");
        cls = new JClassAnnotation(mockClassType);
        verify(mockClassType).getPackageName();
        verify(mockClassType).getClassName();
    }

    /**
     * Verify that the class type is properly prepared
     */
    @Test
    public void testClassType() {
        Assertions.assertEquals("@interface", cls.classType());
    }

    /**
     * Verify that attempting to give any parent to an interface triggers an exception
     */
    @Test
    public void testCannotHaveAnyParent() {
        // "Proper" values are not allowed
        Assertions.assertThrows(IllegalArgumentException.class, () -> cls.setParentClass(mockClassType));
        Assertions.assertThrows(IllegalArgumentException.class, () -> cls.setParentInterfaces(Collections.singletonList(mockClassType)));
        
        // Empty values are OK
        cls.setParentClass(null);
        cls.setParentInterfaces(Collections.emptyList());
    }
}
