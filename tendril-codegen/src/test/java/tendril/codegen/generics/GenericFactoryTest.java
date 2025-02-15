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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.codegen.DefinitionException;
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
    private ClassType mockClassType1;
    @Mock
    private ClassType mockClassType2;
    @Mock
    private ClassType mockClassType3;
    @Mock
    private JClass mockClass1;
    @Mock
    private JClass mockClass2;
    @Mock
    private JClass mockClass3;
    
    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        lenient().when(mockClass1.getType()).thenReturn(mockClassType1);
        lenient().when(mockClass2.getType()).thenReturn(mockClassType1);
        lenient().when(mockClass3.getType()).thenReturn(mockClassType1);
        lenient().when(mockClassType1.getSimpleName()).thenReturn("MockClass1");
        lenient().when(mockClassType2.getSimpleName()).thenReturn("MockClass2");
        lenient().when(mockClassType3.getSimpleName()).thenReturn("MockClass3");
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
        Assertions.assertThrows(DefinitionException.class, () -> GenericFactory.create("123"));
        Assertions.assertThrows(DefinitionException.class, () -> GenericFactory.create("?"));
        Assertions.assertThrows(DefinitionException.class, () -> GenericFactory.create(""));
    }
    
    /**
     * Verify the creation with a {@link ClassType}
     */
    @Test
    public void testCreateWithClassType () {
        ClassAssert.assertInstance(SimpleExplicitGeneric.class, GenericFactory.create(mockClassType1));
        verify(mockClassType1).getSimpleName();
    }
    
    /**
     * Verify the creation with a {@link JClass}
     */
    @Test
    public void testCreateWithJClass () {
        ClassAssert.assertInstance(SimpleExplicitGeneric.class, GenericFactory.create(mockClass1));
        verify(mockClass1).getType();
        verify(mockClassType1).getSimpleName();
    }
    
    /**
     * Verify the creation of a wildcard
     */
    @Test
    public void testCreateWildcard() {
        ClassAssert.assertInstance(SimpleWildcardGeneric.class, GenericFactory.createWildcard());
    }
    
    /**
     * Verify the creation of a generic type with extends
     */
    @Test
    public void testCreateExtendsClassType() {
        // Must have at least one extension
        Assertions.assertThrows(DefinitionException.class, () -> GenericFactory.createExtends("MyGeneric", new JClass[0]));
        // Must have a valid name
        Assertions.assertThrows(DefinitionException.class, () -> GenericFactory.createExtends("123", mockClassType1, mockClassType2, mockClassType3));

        ClassAssert.assertInstance(CompoundExtendsGeneric.class, GenericFactory.createExtends("MyGeneric", mockClassType1));
        ClassAssert.assertInstance(CompoundExtendsGeneric.class, GenericFactory.createExtends("MyGeneric", mockClassType1, mockClassType2));
        ClassAssert.assertInstance(CompoundExtendsGeneric.class, GenericFactory.createExtends("MyGeneric", mockClassType1, mockClassType2, mockClassType3));
    }
    
    /**
     * Verify the creation of a generic type with extends
     */
    @Test
    public void testCreateExtendsJClass() {
        // Must have at least one extension
        Assertions.assertThrows(DefinitionException.class, () -> GenericFactory.createExtends("MyGeneric", new ClassType[0]));
        // Must have a valid name
        Assertions.assertThrows(DefinitionException.class, () -> GenericFactory.createExtends("123", mockClass1, mockClass2, mockClass3));
        verify(mockClass1).getType();
        verify(mockClass2).getType();
        verify(mockClass3).getType();

        ClassAssert.assertInstance(CompoundExtendsGeneric.class, GenericFactory.createExtends("MyGeneric", mockClass1));
        verify(mockClass1, times(2)).getType();
        ClassAssert.assertInstance(CompoundExtendsGeneric.class, GenericFactory.createExtends("MyGeneric", mockClass1, mockClass2));
        verify(mockClass1, times(3)).getType();
        verify(mockClass2, times(2)).getType();
        ClassAssert.assertInstance(CompoundExtendsGeneric.class, GenericFactory.createExtends("MyGeneric", mockClass1, mockClass2, mockClass3));
        verify(mockClass1, times(4)).getType();
        verify(mockClass2, times(3)).getType();
        verify(mockClass3, times(2)).getType();
    }
    
    /**
     * Verify the creation of a generic type with super
     */
    @Test
    public void testCreateSuperClassType() {
        ClassAssert.assertInstance(CompoundSuperGeneric.class, GenericFactory.createSuper(mockClassType1));
    }
    
    /**
     * Verify the creation of a generic type with super
     */
    @Test
    public void testCreateSuperJClass() {
        ClassAssert.assertInstance(CompoundSuperGeneric.class, GenericFactory.createSuper(mockClass1));
        verify(mockClass1).getType();
    }
    
    /**
     * Verify the creation of a generic wildcard type with extends
     */
    @Test
    public void testCreateWildcardExtendsClassType() {
        // Must have at least one extension
        Assertions.assertThrows(DefinitionException.class, () -> GenericFactory.createWildcardExtends(new ClassType[0]));
        
        ClassAssert.assertInstance(CompoundExtendsGeneric.class, GenericFactory.createWildcardExtends(mockClassType1));
        ClassAssert.assertInstance(CompoundExtendsGeneric.class, GenericFactory.createWildcardExtends(mockClassType1, mockClassType2));
        ClassAssert.assertInstance(CompoundExtendsGeneric.class, GenericFactory.createWildcardExtends(mockClassType1, mockClassType2, mockClassType3));
    }
    
    /**
     * Verify the creation of a generic wildcard type with extends
     */
    @Test
    public void testCreateWildcardExtendsJClass() {
        // Must have at least one extension
        Assertions.assertThrows(DefinitionException.class, () -> GenericFactory.createWildcardExtends(new JClass[0]));
        
        ClassAssert.assertInstance(CompoundExtendsGeneric.class, GenericFactory.createWildcardExtends(mockClass1));
        verify(mockClass1).getType();
        ClassAssert.assertInstance(CompoundExtendsGeneric.class, GenericFactory.createWildcardExtends(mockClass1, mockClass2));
        verify(mockClass1, times(2)).getType();
        verify(mockClass2).getType();
        ClassAssert.assertInstance(CompoundExtendsGeneric.class, GenericFactory.createWildcardExtends(mockClass1, mockClass2, mockClass3));
        verify(mockClass1, times(3)).getType();
        verify(mockClass2, times(2)).getType();
        verify(mockClass3).getType();
    }
}
