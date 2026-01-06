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

import java.util.Arrays;
import java.util.Collections;

import javax.annotation.processing.Generated;
import javax.lang.model.type.TypeKind;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import tendril.codegen.DefinitionException;
import tendril.codegen.VisibilityType;
import tendril.codegen.annotation.JAnnotation;
import tendril.codegen.classes.method.JConstructor;
import tendril.codegen.classes.method.JMethod;
import tendril.codegen.field.JField;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.PrimitiveType;
import tendril.codegen.field.type.Type;
import tendril.codegen.field.type.TypeFactory;
import tendril.codegen.field.type.VoidType;
import tendril.test.AbstractUnitTest;
import tendril.test.assertions.ClassAssert;
import tendril.test.assertions.CollectionAssert;

/**
 * Test case for {@link ClassBuilder}
 */
public class ClassBuilderTest extends AbstractUnitTest {
    
    /**
     * Test implementation of the {@link ClassBuilder} so that it can be tested
     */
    private class TestClassBuilder extends ClassBuilder {

        /**
         * CTOR
         */
        public TestClassBuilder() {
            super(mockClassType);
        }

        /**
         * @see tendril.codegen.classes.ClassBuilder#createMethodBuilder(java.lang.String)
         */
        @SuppressWarnings("unchecked")
        @Override
        protected <RETURN_TYPE extends Type> MethodBuilder<RETURN_TYPE> createMethodBuilder(String name) {
            return (MethodBuilder<RETURN_TYPE>) mockMethodBuilder;
        }

        /**
         * @see tendril.codegen.BaseBuilder#create()
         */
        @Override
        protected JClass create() {
            return mockClass;
        }
        
    }
    
    // Mocks to use for testing
    @Mock
    private ClassType mockClassType;
    @Mock
    private ClassType mockMethodClassType;
    @Mock
    private MethodBuilder<Type> mockMethodBuilder;
    @Mock
    private JClass mockClass;
    @Mock
    private JMethod<?> mockMethod1;
    @Mock
    private JMethod<?> mockMethod2;
    @Mock
    private JMethod<?> mockMethod3;
    @Mock
    private JField<?> mockField1;
    @Mock
    private JField<?> mockField2;
    @Mock
    private JField<?> mockField3;
    @Mock
    private JClass mockParentClass;
    @Mock
    private JClass mockInterface1;
    @Mock
    private JClass mockInterface2;
    @Mock
    private JClass mockInterface3;
    @Mock
    private JConstructor mockCtor1;
    @Mock
    private JConstructor mockCtor2;
    @Mock
    private JConstructor mockCtor3;
    @Mock
    private EnumerationEntry mockEnumEntry;
    
    // Instance to test
    private TestClassBuilder builder;

    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        when(mockClassType.getSimpleName()).thenReturn("MockClass");
        when(mockClassType.getGenerics()).thenReturn(Collections.emptyList());
        builder = new TestClassBuilder();
        verify(mockClassType).getSimpleName();
        verify(mockClassType).getGenerics();
    }
    
    /**
     * Verify that can create the appropriate builders via the factory methods
     */
    @Test
    public void testCreateViaFactory() {
        ClassAssert.assertInstance(ConcreteClassBuilder.class, ClassBuilder.forConcreteClass(mockClassType));
        ClassAssert.assertInstance(ConcreteClassBuilder.class, ClassBuilder.forConcreteClass(ClassBuilder.class));
        ClassAssert.assertInstance(AbstractClassBuilder.class, ClassBuilder.forAbstractClass(mockClassType));
        ClassAssert.assertInstance(AbstractClassBuilder.class, ClassBuilder.forAbstractClass(ClassBuilder.class));
        ClassAssert.assertInstance(InterfaceBuilder.class, ClassBuilder.forInterface(mockClassType));
        ClassAssert.assertInstance(InterfaceBuilder.class, ClassBuilder.forInterface(ClassBuilder.class));
        ClassAssert.assertInstance(AnnotationBuilder.class, ClassBuilder.forAnnotation(mockClassType));
        ClassAssert.assertInstance(AnnotationBuilder.class, ClassBuilder.forAnnotation(ClassBuilder.class));
        ClassAssert.assertInstance(EnumClassBuilder.class, ClassBuilder.forEnum(mockClassType));
        ClassAssert.assertInstance(EnumClassBuilder.class, ClassBuilder.forEnum(TypeKind.class));
    }
    
    /**
     * Verify that the method builder can be properly created
     */
    @Test
    public void testCreateVoidMethodBuilder() {
        Assertions.assertEquals(mockMethodBuilder, builder.buildMethod("voidMethod"));
        verify(mockMethodBuilder).setType(VoidType.INSTANCE);
    }
    
    /**
     * Verify that the method builder can be properly created
     */
    @Test
    public void testCreatePrimitiveMethodBuilder() {
        Assertions.assertEquals(mockMethodBuilder, builder.buildMethod(PrimitiveType.CHAR, "primitiveMethod"));
        verify(mockMethodBuilder).setType(PrimitiveType.CHAR);
    }
    
    /**
     * Verify that the method builder can be properly created
     */
    @Test
    public void testCreateClassMethodBuilder() {
        Assertions.assertEquals(mockMethodBuilder, builder.buildMethod(PrimitiveType.class, "classMethod"));
        verify(mockMethodBuilder).setType(TypeFactory.createClassType(PrimitiveType.class));
    }
    
    /**
     * Verify that the method builder can be properly created
     */
    @Test
    public void testCreateClassTypeMethodBuilder() {
        Assertions.assertEquals(mockMethodBuilder, builder.buildMethod(mockMethodClassType, "classMethod"));
        verify(mockMethodBuilder).setType(mockMethodClassType);
    }
    
    /**
     * Verify that the constructor builder can be properly created
     */
    @Test
    public void testConstructorBuilder() {
        Assertions.assertNotNull(builder.buildConstructor());
        verify(mockClassType, times(2)).getSimpleName();
    }
    
    /**
     * Verify that class details are applied when original defaults are kept
     */
    @Test
    public void testApplyDetailsDefaultValues() {
        builder.applyDetails(mockClass);
        verifyCommonDetailsApplied(null);
    }
    
    /**
     * Verify that class details are applied when methods are added
     */
    @Test
    public void testApplyDetailsWithMethods() {
        builder.add(mockMethod1);
        builder.add(mockMethod2);
        builder.add(mockMethod3);
        
        builder.applyDetails(mockClass);

        verifyCommonDetailsApplied(null);
        verify(mockClass).addMethod(mockMethod1);
        verify(mockClass).addMethod(mockMethod2);
        verify(mockClass).addMethod(mockMethod3);
    }
    
    /**
     * Verify that class details are applied when fields are added
     */
    @Test
    public void testApplyDetailsWithFields() {
        builder.add(mockField1);
        builder.add(mockField2);
        builder.add(mockField3);
        
        builder.applyDetails(mockClass);

        verifyCommonDetailsApplied(null);
        verify(mockClass).addField(mockField1);
        verify(mockClass).addField(mockField2);
        verify(mockClass).addField(mockField3);
    }
    
    /**
     * Verify that class details are applied when a parent class is set
     */
    @Test
    public void testApplyDetailsParentClass() {
        builder.extendsClass(mockParentClass);
        builder.applyDetails(mockClass);
        verifyCommonDetailsApplied(mockParentClass);
    }
    
    /**
     * Verify that class details are applied when implemented interfaces are added
     */
    @Test
    public void testApplyDetailsInterfaces() {
        builder.implementsInterface(mockInterface1);
        builder.implementsInterface(mockInterface2);
        builder.implementsInterface(mockInterface3);
        builder.applyDetails(mockClass);
        verifyCommonDetailsApplied(null, mockInterface1, mockInterface2, mockInterface3);
    }
    
    /**
     * Verify that everything can be applied at once.
     */
    @Test
    public void testApplyDetailsAllAtOnce() {
        builder.add(mockMethod1);
        builder.add(mockMethod2);
        builder.add(mockMethod3);
        builder.add(mockField1);
        builder.add(mockField2);
        builder.add(mockField3);
        builder.extendsClass(mockParentClass);
        builder.implementsInterface(mockInterface1);
        builder.implementsInterface(mockInterface2);
        builder.implementsInterface(mockInterface3);
        builder.add(mockCtor1);
        builder.add(mockCtor2);
        builder.add(mockCtor3);
        
        builder.applyDetails(mockClass);

        verifyCommonDetailsApplied(mockParentClass, mockInterface1, mockInterface2, mockInterface3);
        verify(mockClass).addMethod(mockMethod1);
        verify(mockClass).addMethod(mockMethod2);
        verify(mockClass).addMethod(mockMethod3);
        verify(mockClass).addField(mockField1);
        verify(mockClass).addField(mockField2);
        verify(mockClass).addField(mockField3);
        verify(mockClass).addConstructor(mockCtor1);
        verify(mockClass).addConstructor(mockCtor2);
        verify(mockClass).addConstructor(mockCtor3);
    }
    
    /**
     * Verify that the enum entries generate exceptions by default
     */
    @Test
    public void testEnumEntries() {
        Assertions.assertThrows(DefinitionException.class, () -> builder.buildEnumeration("EnumName"));
        verify(mockClassType).getFullyQualifiedName();
        Assertions.assertThrows(DefinitionException.class, () -> builder.add(mockEnumEntry));
        verify(mockClassType, times(2)).getFullyQualifiedName();
    }
    
    /**
     * Perform the verification of common details that are always applied
     * 
     * @param expectedParent {@link ClassType} class the class is expected to extend
     * @param expectedInterfaces {@link ClassType}... interfaces the class is expected to implement
     */
    private void verifyCommonDetailsApplied(JClass expectedParent, JClass...expectedInterfaces) {
        verify(mockClass).setParentClass(expectedParent);
        verify(mockClass).setParentInterfaces(Arrays.asList(expectedInterfaces));
        
        verify(mockClass).setVisibility(VisibilityType.PACKAGE_PRIVATE);
        verify(mockClass).setFinal(false);
        verify(mockClass).setStatic(false);
        
        ArgumentCaptor<JAnnotation> captor = ArgumentCaptor.forClass(JAnnotation.class);
        verify(mockClass).add(captor.capture());
        CollectionAssert.assertSize(1, captor.getAllValues());
        Assertions.assertEquals(TypeFactory.createClassType(Generated.class), captor.getValue().getType());
    }

}
