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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.codegen.DefinitionException;
import tendril.codegen.VisibilityType;
import tendril.codegen.classes.method.InterfaceMethodBuilder;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.generics.GenericType;
import tendril.test.AbstractUnitTest;
import tendril.test.assertions.ClassAssert;

/**
 * Test case for {@link InterfaceBuilder}
 */
public class InterfaceBuilderTest extends AbstractUnitTest {
    
    // Mocks to use for testing
    @Mock
    private ClassType mockClassType1;
    @Mock
    private JClass mockJClass1;
    @Mock
    private JClass mockJClass2;
    @Mock
    private JClass mockJClass3;
    @Mock
    private JClass mockClass;
    @Mock
    private GenericType mockGeneric1;
    @Mock
    private GenericType mockGeneric2;
    @Mock
    private GenericType mockGeneric3;
    
    // Instance to test
    private InterfaceBuilder builder;

    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        when(mockClassType1.getSimpleName()).thenReturn("MockClass");
        when(mockClassType1.getGenerics()).thenReturn(Arrays.asList(mockGeneric1, mockGeneric2, mockGeneric3));
        builder = new InterfaceBuilder(mockClassType1);
        verify(mockClassType1).getSimpleName();
        verify(mockClassType1).getGenerics();
    }
    
    /**
     * Verify that the correct method builder is created
     */
    @Test
    public void testCreatMethodBuilder() {
        ClassAssert.assertInstance(InterfaceMethodBuilder.class, builder.createMethodBuilder("method"));
    }
    
    /**
     * Verify that validation properly verifies the details
     */
    @Test
    public void testValidate() {
        int times = 0;
        for (VisibilityType type: VisibilityType.values()) {
            builder.setVisibility(type);
            if (type == VisibilityType.PRIVATE || type == VisibilityType.PROTECTED) {
                Assertions.assertThrows(DefinitionException.class, () -> builder.validate());
                verify(mockClassType1, times(++times)).getFullyQualifiedName();
            } else
                Assertions.assertDoesNotThrow(() -> builder.validate());
        }
    }
    
    /**
     * Verify that create produces the correct class type
     */
    @Test
    public void testCreate() {
        when(mockClassType1.getPackageName()).thenReturn("package");
        when(mockClassType1.getClassName()).thenReturn("ClassName");
        ClassAssert.assertInstance(JClassInterface.class, builder.create());
        verify(mockClassType1).getClassName();
        verify(mockClassType1).getPackageName();
    }

    /**
     * Verify that the interface cannot implement
     */
    @Test
    public void testInterfaceCannotImplement() {
        Assertions.assertThrows(DefinitionException.class, () -> builder.implementsInterface(mockJClass1));
        verify(mockClassType1).getFullyQualifiedName();
    }
    
    /**
     * Verify that extending allows for any number of interfaces to be extended
     */
    @Test
    public void testInterfaceExtend() {
        builder.extendsClass(mockJClass1);
        builder.extendsClass(mockJClass2);
        builder.extendsClass(mockJClass3);
        builder.applyDetails(mockClass);
        
        verify(mockClass).setParentClass(null);
        verify(mockClass).setParentInterfaces(Arrays.asList(mockJClass1, mockJClass2, mockJClass3));
        verify(mockClass).setVisibility(any());
        verify(mockClass).setStatic(anyBoolean());
        verify(mockClass).setFinal(anyBoolean());
        verify(mockClass, times(1)).addAnnotation(any());
        verify(mockClass).addGeneric(mockGeneric1);
        verify(mockClass).addGeneric(mockGeneric2);
        verify(mockClass).addGeneric(mockGeneric3);
    }
    
    /**
     * Verify that the interface cannot create a constructor
     */
    @Test
    public void testCannotCreateConstructor() {
        Assertions.assertThrows(DefinitionException.class, () -> builder.buildConstructor());
        verify(mockClassType1).getFullyQualifiedName();
    }
}
