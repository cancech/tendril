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
package tendril.codegen.generics;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.codegen.classes.JClass;
import tendril.codegen.field.type.ClassType;
import tendril.test.AbstractUnitTest;
import tendril.test.assertions.ClassAssert;

/**
 * Test case for the {@link GenericFactory}
 */
public class GenericFactoryTest extends AbstractUnitTest {

    // Mocks to use for testing
    @Mock
    private ClassType mockClassType;
    @Mock
    private JClass mockClass;
    
    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        lenient().when(mockClass.getType()).thenReturn(mockClassType);
        lenient().when(mockClassType.getSimpleName()).thenReturn("MockClass");
    }
    
    /**
     * Verify the creation with name accounts for valid generic names
     */
    @Test
    public void testCreateByName() {
        // Valid names produce a generic
        ClassAssert.assertInstance(SimpleGeneric.class, GenericFactory.create("ABC123"));
        ClassAssert.assertInstance(SimpleGeneric.class, GenericFactory.create("T"));
        ClassAssert.assertInstance(SimpleGeneric.class, GenericFactory.create("_123T"));
        
        // Invalid names generate exception
        Assertions.assertThrows(IllegalArgumentException.class, () -> GenericFactory.create("123"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> GenericFactory.create("?"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> GenericFactory.create(""));
    }
    
    /**
     * Verify the creation with a {@link ClassType}
     */
    @Test
    public void testCreateWithClassType () {
        ClassAssert.assertInstance(SimpleClassTypeGeneric.class, GenericFactory.create(mockClassType));
        verify(mockClassType).getSimpleName();
    }
    
    /**
     * Verify the creation with a {@link JClass}
     */
    @Test
    public void testCreateWithJClass () {
        ClassAssert.assertInstance(SimpleClassTypeGeneric.class, GenericFactory.create(mockClass));
        verify(mockClass).getType();
        verify(mockClassType).getSimpleName();
    }
    
    /**
     * Verify the creation of a wildcard
     */
    @Test
    public void testCreateWildcard() {
        ClassAssert.assertInstance(SimpleWildcardGeneric.class, GenericFactory.createWildcard());
    }

}
