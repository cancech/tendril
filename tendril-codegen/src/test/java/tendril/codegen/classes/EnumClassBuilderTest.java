/*
 * Copyright 2025 Jaroslav Bosak
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

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.codegen.DefinitionException;
import tendril.codegen.VisibilityType;
import tendril.codegen.classes.method.JConstructor;
import tendril.codegen.field.type.ClassType;
import tendril.test.AbstractUnitTest;
import tendril.test.assertions.ClassAssert;

/**
 * Test case for the {@link EnumClassBuilder}
 */
public class EnumClassBuilderTest extends AbstractUnitTest {
    
    // Mocks to use for testing
    @Mock
    private ClassType mockType;
    @Mock
    private JClass mockClass;
    @Mock
    private JConstructor mockCtor1;
    @Mock
    private JConstructor mockCtor2;
    @Mock
    private JConstructor mockCtor3;
    @Mock
    private EnumerationEntry mockEnum1;
    @Mock
    private EnumerationEntry mockEnum2;
    @Mock
    private EnumerationEntry mockEnum3;
    
    
    // Instance to test
    private EnumClassBuilder builder;
    
    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        when(mockType.getSimpleName()).thenReturn("SimpleName");
        when(mockType.getGenerics()).thenReturn(Collections.emptyList());
        
        builder = new EnumClassBuilder(mockType);
        verify(mockType).getSimpleName();
        verify(mockType).getGenerics();
    }

    /**
     * Ensure that the validation properly catches unwanted states
     */
    @Test
    public void testValidate() {
        when(mockType.getFullyQualifiedName()).thenReturn("FullyQualifiedName");

        // Cannot be static
        builder.setStatic(true);
        Assertions.assertThrows(DefinitionException.class, () -> builder.validate());
        verify(mockType).getFullyQualifiedName();
        builder.setStatic(false);
        builder.validate();
        
        // Cannot be final
        builder.setFinal(false);
        Assertions.assertThrows(DefinitionException.class, () -> builder.validate());
        verify(mockType, times(2)).getFullyQualifiedName();
        builder.setFinal(true);
        builder.validate();
        
        // Cannot have a parent
        builder.extendsClass(mockClass);
        Assertions.assertThrows(DefinitionException.class, () -> builder.validate());
        verify(mockType, times(3)).getFullyQualifiedName();
        builder.extendsClass(null);
        builder.validate();
        
        // All ctors must be private or package private
        builder.add(mockCtor1);
        builder.add(mockCtor2);
        builder.add(mockCtor3);

        when(mockCtor1.getVisibility()).thenReturn(VisibilityType.PUBLIC);
        Assertions.assertThrows(DefinitionException.class, () -> builder.validate());
        verify(mockCtor1, times(1)).getVisibility();
        verify(mockType, times(4)).getFullyQualifiedName();
        
        when(mockCtor1.getVisibility()).thenReturn(VisibilityType.PRIVATE);
        when(mockCtor2.getVisibility()).thenReturn(VisibilityType.PROTECTED);
        Assertions.assertThrows(DefinitionException.class, () -> builder.validate());
        verify(mockCtor1, times(2)).getVisibility();
        verify(mockCtor2, times(1)).getVisibility();
        verify(mockType, times(5)).getFullyQualifiedName();
        
        when(mockCtor1.getVisibility()).thenReturn(VisibilityType.PRIVATE);
        when(mockCtor2.getVisibility()).thenReturn(VisibilityType.PACKAGE_PRIVATE);
        when(mockCtor3.getVisibility()).thenReturn(VisibilityType.PUBLIC);
        Assertions.assertThrows(DefinitionException.class, () -> builder.validate());
        verify(mockCtor1, times(3)).getVisibility();
        verify(mockCtor2, times(2)).getVisibility();
        verify(mockCtor3, times(1)).getVisibility();
        verify(mockType, times(6)).getFullyQualifiedName();
        
        when(mockCtor1.getVisibility()).thenReturn(VisibilityType.PRIVATE);
        when(mockCtor2.getVisibility()).thenReturn(VisibilityType.PACKAGE_PRIVATE);
        when(mockCtor3.getVisibility()).thenReturn(VisibilityType.PRIVATE);
        builder.validate();
        verify(mockCtor1, times(4)).getVisibility();
        verify(mockCtor2, times(3)).getVisibility();
        verify(mockCtor3, times(2)).getVisibility();
        
        when(mockCtor1.getVisibility()).thenReturn(VisibilityType.PACKAGE_PRIVATE);
        when(mockCtor2.getVisibility()).thenReturn(VisibilityType.PACKAGE_PRIVATE);
        when(mockCtor3.getVisibility()).thenReturn(VisibilityType.PACKAGE_PRIVATE);
        builder.validate();
        verify(mockCtor1, times(5)).getVisibility();
        verify(mockCtor2, times(4)).getVisibility();
        verify(mockCtor3, times(3)).getVisibility();
        
        when(mockCtor1.getVisibility()).thenReturn(VisibilityType.PRIVATE);
        when(mockCtor2.getVisibility()).thenReturn(VisibilityType.PRIVATE);
        when(mockCtor3.getVisibility()).thenReturn(VisibilityType.PRIVATE);
        builder.validate();
        verify(mockCtor1, times(6)).getVisibility();
        verify(mockCtor2, times(5)).getVisibility();
        verify(mockCtor3, times(4)).getVisibility();
    }
    
    /**
     * Verify that the correct type is created and it has the enumerations applied
     */
    @Test
    public void testCreate() {
        when(mockType.getPackageName()).thenReturn("PackageName");
        when(mockType.getClassName()).thenReturn("ClassName");
        
        // By default no entries
        JClass cls = builder.create();
        verify(mockType).getPackageName();
        verify(mockType).getClassName();
        ClassAssert.assertInstance(JClassEnum.class, cls);
        Assertions.assertIterableEquals(Collections.emptyList(), ((JClassEnum) cls).getEnumerations());
        
        // Add one
        builder.add(mockEnum1);
        cls = builder.create();
        verify(mockType, times(2)).getPackageName();
        verify(mockType, times(2)).getClassName();
        ClassAssert.assertInstance(JClassEnum.class, cls);
        Assertions.assertIterableEquals(Collections.singletonList(mockEnum1), ((JClassEnum) cls).getEnumerations());
        
        // Add a second
        builder.add(mockEnum2);
        cls = builder.create();
        verify(mockType, times(3)).getPackageName();
        verify(mockType, times(3)).getClassName();
        ClassAssert.assertInstance(JClassEnum.class, cls);
        Assertions.assertIterableEquals(Arrays.asList(mockEnum1, mockEnum2), ((JClassEnum) cls).getEnumerations());
        
        // Add a third
        builder.add(mockEnum3);
        cls = builder.create();
        verify(mockType, times(4)).getPackageName();
        verify(mockType, times(4)).getClassName();
        ClassAssert.assertInstance(JClassEnum.class, cls);
        Assertions.assertIterableEquals(Arrays.asList(mockEnum1, mockEnum2, mockEnum3), ((JClassEnum) cls).getEnumerations());
    }
    
    /**
     * Verify that the enumeration builder is properly created.
     */
    @Test
    public void testBuildEnumeration() {
        Assertions.assertNotNull(builder.buildEnumeration("abc123"));
    }
}
