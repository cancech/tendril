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
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.codegen.CodeBuilder;
import tendril.codegen.DefinitionException;
import tendril.codegen.JBase;
import tendril.codegen.annotation.JAnnotationFactory;
import tendril.codegen.classes.method.JConstructor;
import tendril.codegen.classes.method.JMethod;
import tendril.codegen.field.JField;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.PrimitiveType;
import tendril.codegen.field.type.Type;
import tendril.codegen.field.type.VoidType;
import tendril.codegen.field.value.JValueFactory;
import tendril.codegen.generics.GenericType;
import tendril.test.AbstractUnitTest;
import tendril.test.assertions.matchers.MultiLineStringMatcher;
import tendril.test.helper.annotation.TestMarkerAnnotation;
import tendril.test.helper.annotation.TestPrimitiveAnnotation;

/**
 * Test case for {@link JClass}
 */
public class JClassTest extends AbstractUnitTest {

    /**
     * Concrete implementation of {@link JClass} to be used for testing
     */
    private class TestJClass extends JClass {

        /**
         * CTOR
         */
        protected TestJClass() {
            super(mockClassType);
        }

        /**
         * @see tendril.codegen.classes.JClass#getClassKeyword()
         */
        @Override
        protected String getClassKeyword() {
            return "ClassType ";
        }

    }

    // Mocks to use for testing
    @Mock
    private ClassType mockClassType;
    @Mock
    private MethodBuilder<Type> mockMethodBuilder;
    @Mock
    private JMethod<VoidType> mockVoidMethod;
    @Mock
    private JMethod<PrimitiveType> mockPrimitiveMethod;
    @Mock
    private JMethod<ClassType> mockClassMethod;
    @Mock
    private ClassType mockNullPackageClassType;
    @Mock
    private ClassType mockEmptyPackageClassType;
    @Mock
    private ClassType mockJavaLangPackageClassType;
    @Mock
    private ClassType mockSamePackageClassType;
    @Mock
    private JField<Type> mockField1;
    @Mock
    private JField<Type> mockField2;
    @Mock
    private JField<Type> mockField3;
    @Mock
    private ClassType mockField1Type;
    @Mock
    private ClassType mockField2Type;
    @Mock
    private JClass mockParentClass;
    @Mock
    private ClassType mockParentClassType;
    @Mock
    private JClass mockInterface1;
    @Mock
    private ClassType mockInterface1ClassType;
    @Mock
    private JClass mockInterface2;
    @Mock
    private ClassType mockInterface2ClassType;
    @Mock
    private JClass mockInterface3;
    @Mock
    private ClassType mockInterface3ClassType;
    @Mock
    private JConstructor mockCtor1;
    @Mock
    private JConstructor mockCtor2;
    @Mock
    private JConstructor mockCtor3;
    @Mock
    private GenericType mockGeneric1;
    @Mock
    private GenericType mockGeneric2;
    @Mock
    private GenericType mockGeneric3;

    // Helper to match the generate code
    private MultiLineStringMatcher strMatcher;
    // Instance to test
    private TestJClass jclass;

    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        lenient().when(mockNullPackageClassType.getPackageName()).thenReturn(null);
        lenient().when(mockNullPackageClassType.getFullyQualifiedName()).thenReturn("mockNullPackageClassType");
        lenient().when(mockEmptyPackageClassType.getPackageName()).thenReturn("   ");
        lenient().when(mockEmptyPackageClassType.getFullyQualifiedName()).thenReturn("mockEmptyPackageClassType");
        lenient().when(mockJavaLangPackageClassType.getPackageName()).thenReturn("java.lang");
        lenient().when(mockJavaLangPackageClassType.getFullyQualifiedName()).thenReturn("mockJavaLangPackageClassType");
        lenient().when(mockSamePackageClassType.getPackageName()).thenReturn("packageName");
        lenient().when(mockSamePackageClassType.getFullyQualifiedName()).thenReturn("mockSamePackageClassType");
        lenient().when(mockField1Type.getPackageName()).thenReturn("mockField1TypePackage");
        lenient().when(mockField1Type.getFullyQualifiedName()).thenReturn("mockField1Type");
        lenient().when(mockField2Type.getPackageName()).thenReturn("mockField2TypePackage");
        lenient().when(mockField2Type.getFullyQualifiedName()).thenReturn("mockField2Type");
        lenient().when(mockParentClass.getType()).thenReturn(mockParentClassType);
        lenient().when(mockParentClass.getName()).thenReturn("mockParentClass");
        lenient().when(mockParentClass.getAppliedCode(true)).thenReturn("mockParentClass ");
        lenient().when(mockParentClass.getAppliedCode(false)).thenReturn("mockParentClass");
        lenient().when(mockParentClass.getGenericsDefinitionKeyword(true)).thenReturn(" ");
        lenient().when(mockParentClass.getGenericsDefinitionKeyword(false)).thenReturn("");
        lenient().when(mockParentClassType.getPackageName()).thenReturn("mock.class");
        lenient().when(mockParentClassType.getFullyQualifiedName()).thenReturn("mock.class.mockParentClass");
        lenient().when(mockInterface1.getType()).thenReturn(mockInterface1ClassType);
        lenient().when(mockInterface1.getName()).thenReturn("mockInterface1");
        lenient().when(mockInterface1.getAppliedCode(true)).thenReturn("mockInterface1 ");
        lenient().when(mockInterface1.getAppliedCode(false)).thenReturn("mockInterface1");
        lenient().when(mockInterface1.getGenericsDefinitionKeyword(true)).thenReturn(" ");
        lenient().when(mockInterface1.getGenericsDefinitionKeyword(false)).thenReturn("");
        lenient().when(mockInterface1ClassType.getPackageName()).thenReturn("mock.class");
        lenient().when(mockInterface1ClassType.getFullyQualifiedName()).thenReturn("mock.class.mockInterface1");
        lenient().when(mockInterface2.getType()).thenReturn(mockInterface2ClassType);
        lenient().when(mockInterface2.getName()).thenReturn("mockInterface2");
        lenient().when(mockInterface2.getAppliedCode(true)).thenReturn("mockInterface2 ");
        lenient().when(mockInterface2.getAppliedCode(false)).thenReturn("mockInterface2");
        lenient().when(mockInterface2.getGenericsDefinitionKeyword(true)).thenReturn(" ");
        lenient().when(mockInterface2.getGenericsDefinitionKeyword(false)).thenReturn("");
        lenient().when(mockInterface2ClassType.getPackageName()).thenReturn("mock.class");
        lenient().when(mockInterface2ClassType.getFullyQualifiedName()).thenReturn("mock.class.mockInterface2");
        lenient().when(mockInterface3.getType()).thenReturn(mockInterface3ClassType);
        lenient().when(mockInterface3.getName()).thenReturn("mockInterface3");
        lenient().when(mockInterface3.getAppliedCode(true)).thenReturn("mockInterface3 ");
        lenient().when(mockInterface3.getAppliedCode(false)).thenReturn("mockInterface3");
        lenient().when(mockInterface3.getGenericsDefinitionKeyword(true)).thenReturn(" ");
        lenient().when(mockInterface3.getGenericsDefinitionKeyword(false)).thenReturn("");
        lenient().when(mockInterface3ClassType.getPackageName()).thenReturn("mock.class");
        lenient().when(mockInterface3ClassType.getFullyQualifiedName()).thenReturn("mock.class.mockInterface3");
        lenient().when(mockGeneric1.generateDefinition()).thenReturn("GEN_1_DEF");
        lenient().when(mockGeneric1.generateApplication()).thenReturn("GEN_1_APP");
        lenient().when(mockGeneric2.generateDefinition()).thenReturn("GEN_2_DEF");
        lenient().when(mockGeneric2.generateApplication()).thenReturn("GEN_2_APP");
        lenient().when(mockGeneric3.generateDefinition()).thenReturn("GEN_3_DEF");
        lenient().when(mockGeneric3.generateApplication()).thenReturn("GEN_3_APP");

        mockElementGeneration(mockField1, "mockField1", mockField1Type);
        mockElementGeneration(mockField2, "mockField2", mockField2Type);

        mockElementGeneration(mockCtor1, "mockCtor1");
        mockElementGeneration(mockCtor2, "mockCtor2");
        mockElementGeneration(mockCtor3, "mockCtor3");

        mockElementGeneration(mockVoidMethod, "mockVoidMethod", mockNullPackageClassType, mockSamePackageClassType);
        mockElementGeneration(mockPrimitiveMethod, "mockPrimitiveMethod", mockEmptyPackageClassType);
        mockElementGeneration(mockClassMethod, "mockClassMethod", mockJavaLangPackageClassType);

        when(mockClassType.getPackageName()).thenReturn("packageName");
        when(mockClassType.getClassName()).thenReturn("ClassName");
        lenient().when(mockClassType.getFullyQualifiedName()).thenReturn("packagename.className");
        jclass = new TestJClass();
        verify(mockClassType).getPackageName();
        verify(mockClassType).getClassName();

        Assertions.assertEquals("ClassName", jclass.getName());
    }
    
    /**
     * Verify that the applied code is properly generated
     */
    @Test
    public void testAppliedCode() {
        // With no generics applied
        Assertions.assertEquals("ClassName ", jclass.getAppliedCode(true));
        Assertions.assertEquals("ClassName", jclass.getAppliedCode(false));
        
        // With one generic applied
        jclass.addGeneric(mockGeneric1);
        Assertions.assertEquals("ClassName<GEN_1_APP> ", jclass.getAppliedCode(true));
        verify(mockGeneric1).generateApplication();
        Assertions.assertEquals("ClassName<GEN_1_APP>", jclass.getAppliedCode(false));
        verify(mockGeneric1, times(2)).generateApplication();
        
        // With two generics applied
        jclass.addGeneric(mockGeneric2);
        Assertions.assertEquals("ClassName<GEN_1_APP, GEN_2_APP> ", jclass.getAppliedCode(true));
        verify(mockGeneric1, times(3)).generateApplication();
        verify(mockGeneric2).generateApplication();
        Assertions.assertEquals("ClassName<GEN_1_APP, GEN_2_APP>", jclass.getAppliedCode(false));
        verify(mockGeneric1, times(4)).generateApplication();
        verify(mockGeneric2, times(2)).generateApplication();
        
        // With three generics applied
        jclass.addGeneric(mockGeneric3);
        Assertions.assertEquals("ClassName<GEN_1_APP, GEN_2_APP, GEN_3_APP> ", jclass.getAppliedCode(true));
        verify(mockGeneric1, times(5)).generateApplication();
        verify(mockGeneric2, times(3)).generateApplication();
        verify(mockGeneric3).generateApplication();
        Assertions.assertEquals("ClassName<GEN_1_APP, GEN_2_APP, GEN_3_APP>", jclass.getAppliedCode(false));
        verify(mockGeneric1, times(6)).generateApplication();
        verify(mockGeneric2, times(4)).generateApplication();
        verify(mockGeneric3, times(2)).generateApplication();
    }
    
    /**
     * Verify that static is properly handled.
     */
    @Test
    public void testSetStatic() {
        Assertions.assertFalse(jclass.isStatic());
        
        Assertions.assertThrows(DefinitionException.class, () -> jclass.setStatic(true));
        verify(mockClassType).getFullyQualifiedName();
        Assertions.assertFalse(jclass.isStatic());
        
        jclass.setStatic(false);
        Assertions.assertFalse(jclass.isStatic());
    }

    /**
     * Helper to allow for something to be generated for a nested element when it is produced
     * 
     * @param mockElement {@link JBase} mock element which is to be stubbed
     * @param toProduce {@link String} code that it is to produce for inclusion in the class
     * @param toImport   {@link ClassType}... that should be imported
     */
    @SuppressWarnings("unchecked")
    private void mockElementGeneration(JBase mockElement, String toProduce, ClassType... toImport) {
        lenient().doAnswer(inv -> {
            ((CodeBuilder) inv.getArgument(0)).append(toProduce);
            for (ClassType ct : toImport)
                ((Set<ClassType>) inv.getArgument(1)).add(ct);
            return null;
        }).when(mockElement).generate(any(CodeBuilder.class), anySet());
    }

    /**
     * Verify that the base class code is generated
     */
    @Test
    public void testGenerateEmptyClassCode() {
        // Define what the code is expected to look like
        startDefinition(false, "");
        endDefinition();

        // Verify that it matches
        assertGeneratedCode();
    }

    /**
     * Verify that the base class code is generated
     */
    @Test
    public void testGenerateEmptyFinalClassCode() {
        // Define what the code is expected to look like
        startDefinition(true, "");
        endDefinition();

        // Add the additional features
        jclass.setFinal(true);

        // Verify that it matches
        assertGeneratedCode();
    }

    /**
     * Verify that the class can have a parent
     */
    @Test
    public void testGenerateCodeWithParent() {
        // Define what the code is expected to look like
        startDefinition(Collections.emptyList(), false, "", Collections.emptyList(), "extends mockParentClass");
        endDefinition();

        // Add the additional features
        jclass.setParentClass(mockParentClass);

        // Verify that it matches
        assertGeneratedCode();
        verify(mockParentClass).registerImport(anySet());
        verify(mockParentClass).getAppliedCode(true);
    }

    /**
     * Verify that the class can have a parent
     */
    @Test
    public void testGenerateCodeWithInteface() {
        // Define what the code is expected to look like
        startDefinition(Collections.emptyList(), true, "", Collections.emptyList(), "implements mockInterface1");
        endDefinition();

        // Add the additional features
        jclass.setFinal(true);
        jclass.setParentInterfaces(Collections.singletonList(mockInterface1));

        // Verify that it matches
        assertGeneratedCode();
        verify(mockInterface1).registerImport(anySet());
        verify(mockInterface1).getAppliedCode(false);
    }

    /**
     * Verify that the class can have custom annotations
     */
    @Test
    public void testGenerateCodeWithAnnotations() {
        // Define what the code is expected to look like
        startDefinition(Arrays.asList("@TestMarkerAnnotation", "@TestPrimitiveAnnotation(PrimitiveType.BOOLEAN)"), false, "", TestMarkerAnnotation.class, TestPrimitiveAnnotation.class, PrimitiveType.class);
        endDefinition();

        // Add the additional features
        jclass.add(JAnnotationFactory.create(TestMarkerAnnotation.class));
        jclass.add(JAnnotationFactory.create(TestPrimitiveAnnotation.class, JValueFactory.create(PrimitiveType.BOOLEAN)));

        // Verify that it matches
        assertGeneratedCode();
        verify(mockClassType, atLeastOnce()).getFullyQualifiedName();
    }

    /**
     * Verify that the class code is generated with fields
     */
    @Test
    public void testGenerateCodeWithField() {
        // Define what the code is expected to look like
        startDefinition(Collections.emptyList(), true, "", Arrays.asList("mockField1Type", "mockField2Type"));
        strMatcher.eq("    mockField1");
        strMatcher.eq("");
        strMatcher.eq("    mockField2");
        strMatcher.eq("");
        endDefinition();

        // Add the additional features
        jclass.setFinal(true);
        Assertions.assertIterableEquals(Collections.emptyList(), jclass.getFields());
        jclass.addField(mockField1);
        Assertions.assertIterableEquals(Collections.singletonList(mockField1), jclass.getFields());
        jclass.addField(mockField2);
        Assertions.assertIterableEquals(Arrays.asList(mockField1, mockField2), jclass.getFields());

        // Verify that it matches  [mockClassType, mockField1Type, mockField2Type]
        assertGeneratedCode();
        verify(mockField1).generate(any(CodeBuilder.class), anySet());
        verify(mockField2).generate(any(CodeBuilder.class), anySet());
        verify(mockClassType, atLeastOnce()).getFullyQualifiedName();
        verify(mockField1Type, atLeastOnce()).getFullyQualifiedName();
        verify(mockField2Type, atLeastOnce()).getFullyQualifiedName();
        verify(mockField1Type).getPackageName();
        verify(mockField2Type).getPackageName();
    }
    
    /**
     * Verify that the class code is generated with a constructor
     */
    @Test
    public void testGenerateCodeWithCtor() {
        // Define what the code is expected to look like
        startDefinition(false, "");
        strMatcher.eq(("    mockCtor1"));
        strMatcher.eq((""));
        strMatcher.eq(("    mockCtor2"));
        strMatcher.eq((""));
        strMatcher.eq(("    mockCtor3"));
        strMatcher.eq((""));
        endDefinition();

        // Add the additional features
        Assertions.assertIterableEquals(Collections.emptyList(), jclass.getConstructors());
        jclass.addConstructor(mockCtor1);
        Assertions.assertIterableEquals(Collections.singletonList(mockCtor1), jclass.getConstructors());
        jclass.addConstructor(mockCtor2);
        Assertions.assertIterableEquals(Arrays.asList(mockCtor1, mockCtor2), jclass.getConstructors());
        jclass.addConstructor(mockCtor3);
        Assertions.assertIterableEquals(Arrays.asList(mockCtor1, mockCtor2, mockCtor3), jclass.getConstructors());
        
        // Verify that it matches
        assertGeneratedCode();
        verify(mockCtor1).generate(any(CodeBuilder.class), anySet());
        verify(mockCtor2).generate(any(CodeBuilder.class), anySet());
        verify(mockCtor3).generate(any(CodeBuilder.class), anySet());
    }

    /**
     * Verify that the class can have methods
     */
    @Test
    public void testGenerateCodeWithMethod() {
        // Define what the code is expected to look like
        startDefinition(true, "");
        strMatcher.eq(("    mockVoidMethod"));
        strMatcher.eq((""));
        strMatcher.eq(("    mockPrimitiveMethod"));
        strMatcher.eq((""));
        strMatcher.eq(("    mockClassMethod"));
        strMatcher.eq((""));
        endDefinition();

        // Add the additional features
        jclass.setFinal(true);
        Assertions.assertIterableEquals(Collections.emptyList(), jclass.getMethods());
        jclass.addMethod(mockVoidMethod);
        Assertions.assertIterableEquals(Collections.singletonList(mockVoidMethod), jclass.getMethods());
        jclass.addMethod(mockPrimitiveMethod);
        Assertions.assertIterableEquals(Arrays.asList(mockVoidMethod, mockPrimitiveMethod), jclass.getMethods());
        jclass.addMethod(mockClassMethod);
        Assertions.assertIterableEquals(Arrays.asList(mockVoidMethod, mockPrimitiveMethod, mockClassMethod), jclass.getMethods());

        // Verify that it matches
        assertGeneratedCode();
        verify(mockVoidMethod).generate(any(CodeBuilder.class), anySet());
        verify(mockPrimitiveMethod).generate(any(CodeBuilder.class), anySet());
        verify(mockClassMethod).generate(any(CodeBuilder.class), anySet());
        verify(mockClassType, atLeastOnce()).getFullyQualifiedName();
        verify(mockNullPackageClassType, atLeastOnce()).getFullyQualifiedName();
        verify(mockEmptyPackageClassType, atLeastOnce()).getFullyQualifiedName();
        verify(mockJavaLangPackageClassType, atLeastOnce()).getFullyQualifiedName();
        verify(mockSamePackageClassType, atLeastOnce()).getFullyQualifiedName();
        verify(mockNullPackageClassType).getPackageName();
        verify(mockEmptyPackageClassType).getPackageName();
        verify(mockJavaLangPackageClassType).getPackageName();
        verify(mockSamePackageClassType).getPackageName();
    }

    /**
     * Verify that the class can have generics
     */
    @Test
    public void testGenerateCodeWithSingleGeneric() {
        // Define what the code is expected to look like
        startDefinition(false, "<GEN_1_DEF>");
        endDefinition();

        // Add the additional features
        jclass.addGeneric(mockGeneric1);

        // Verify that it matches
        assertGeneratedCode();
        verify(mockGeneric1).generateDefinition();
        verify(mockGeneric1).registerImport(anySet());
    }

    /**
     * Verify that the class can have generics
     */
    @Test
    public void testGenerateCodeWithMultipleGeneric() {
        // Define what the code is expected to look like
        startDefinition(false, "<GEN_1_DEF, GEN_2_DEF, GEN_3_DEF>");
        endDefinition();

        // Add the additional features
        jclass.addGeneric(mockGeneric1);
        jclass.addGeneric(mockGeneric2);
        jclass.addGeneric(mockGeneric3);

        // Verify that it matches
        assertGeneratedCode();
        verify(mockGeneric1).generateDefinition();
        verify(mockGeneric2).generateDefinition();
        verify(mockGeneric3).generateDefinition();
        verify(mockGeneric1).registerImport(anySet());
        verify(mockGeneric2).registerImport(anySet());
        verify(mockGeneric3).registerImport(anySet());
    }

    /**
     * Verify that the class can have generics
     */
    @Test
    public void testGenerateCodeWithParentGeneric() {
        when(mockParentClass.getAppliedCode(true)).thenReturn("mockParentClass<PARENT_GEN> ");
        
        // Define what the code is expected to look like
        startDefinition(Collections.emptyList(), Collections.emptyList(), false, "", "extends mockParentClass<PARENT_GEN>");
        endDefinition();

        // Add the additional features
        jclass.setParentClass(mockParentClass);

        //System.out.println(jclass.generateCode());
        
        // Verify that it matches
        assertGeneratedCode();
        verify(mockParentClass).registerImport(anySet());
        verify(mockParentClass).getAppliedCode(true);
    }

    /**
     * Verify that the class can have generics
     */
    @Test
    public void testGenerateCodeWithIntertfaceGeneric() {
        when(mockInterface1.getAppliedCode(false)).thenReturn("mockInterface1<IFACE_1_GEN>");
        when(mockInterface3.getAppliedCode(false)).thenReturn("mockInterface3<IFACE_3_GEN>");
        
        // Define what the code is expected to look like
        startDefinition(Collections.emptyList(), Collections.emptyList(), 
                false, "", "implements mockInterface1<IFACE_1_GEN>, mockInterface2, mockInterface3<IFACE_3_GEN>");
        endDefinition();

        // Add the additional features
        jclass.setParentInterfaces(Arrays.asList(mockInterface1, mockInterface2, mockInterface3));

        // Verify that it matches
        assertGeneratedCode();
        verify(mockInterface1).registerImport(anySet());
        verify(mockInterface1).getAppliedCode(false);
        verify(mockInterface2).registerImport(anySet());
        verify(mockInterface2).getAppliedCode(false);
        verify(mockInterface3).registerImport(anySet());
        verify(mockInterface3).getAppliedCode(false);
    }

    /**
     * Verify that the class can have custom annotations, methods, and fields
     */
    @Test
    public void testGenerateComplexCode() {
        when(mockParentClass.getAppliedCode(true)).thenReturn("mockParentClass<PARENT_GEN> ");
        when(mockInterface2.getAppliedCode(false)).thenReturn("mockInterface2<IFACE_2_GEN>");
        
        // Define what the code is expected to look like
        startDefinition(Arrays.asList("@TestMarkerAnnotation", "@TestPrimitiveAnnotation(PrimitiveType.BOOLEAN)"), false, "<GEN_1_DEF, GEN_2_DEF, GEN_3_DEF>",
                Arrays.asList("mockField1Type", "mockField2Type"),
                "extends mockParentClass<PARENT_GEN> implements mockInterface1, mockInterface2<IFACE_2_GEN>, mockInterface3", TestMarkerAnnotation.class, TestPrimitiveAnnotation.class, PrimitiveType.class);
        strMatcher.eq(("    mockField1"));
        strMatcher.eq((""));
        strMatcher.eq(("    mockField2"));
        strMatcher.eq((""));
        strMatcher.eq(("    mockCtor1"));
        strMatcher.eq((""));
        strMatcher.eq(("    mockCtor2"));
        strMatcher.eq((""));
        strMatcher.eq(("    mockCtor3"));
        strMatcher.eq((""));
        strMatcher.eq("    mockVoidMethod");
        strMatcher.eq("");
        strMatcher.eq("    mockPrimitiveMethod");
        strMatcher.eq("");
        strMatcher.eq("    mockClassMethod");
        strMatcher.eq("");
        endDefinition();

        // Add the additional features
        jclass.setParentClass(mockParentClass);
        jclass.setParentInterfaces(Arrays.asList(mockInterface1, mockInterface2, mockInterface3));
        jclass.addMethod(mockVoidMethod);
        jclass.addMethod(mockPrimitiveMethod);
        jclass.addMethod(mockClassMethod);
        jclass.add(JAnnotationFactory.create(TestMarkerAnnotation.class));
        jclass.add(JAnnotationFactory.create(TestPrimitiveAnnotation.class, JValueFactory.create(PrimitiveType.BOOLEAN)));
        jclass.addField(mockField1);
        jclass.addField(mockField2);
        jclass.addConstructor(mockCtor1);
        jclass.addConstructor(mockCtor2);
        jclass.addConstructor(mockCtor3);
        jclass.addGeneric(mockGeneric1);
        jclass.addGeneric(mockGeneric2);
        jclass.addGeneric(mockGeneric3);

        // Verify that it matches
        assertGeneratedCode();
        verify(mockVoidMethod).generate(any(CodeBuilder.class), anySet());
        verify(mockPrimitiveMethod).generate(any(CodeBuilder.class), anySet());
        verify(mockClassMethod).generate(any(CodeBuilder.class), anySet());
        verify(mockNullPackageClassType, atLeastOnce()).getFullyQualifiedName();
        verify(mockEmptyPackageClassType, atLeastOnce()).getFullyQualifiedName();
        verify(mockJavaLangPackageClassType, atLeastOnce()).getFullyQualifiedName();
        verify(mockSamePackageClassType, atLeastOnce()).getFullyQualifiedName();
        verify(mockNullPackageClassType).getPackageName();
        verify(mockEmptyPackageClassType).getPackageName();
        verify(mockJavaLangPackageClassType).getPackageName();
        verify(mockSamePackageClassType).getPackageName();
        verify(mockField1).generate(any(CodeBuilder.class), anySet());
        verify(mockField2).generate(any(CodeBuilder.class), anySet());
        verify(mockField1Type, atLeastOnce()).getFullyQualifiedName();
        verify(mockField2Type, atLeastOnce()).getFullyQualifiedName();
        verify(mockField1Type).getPackageName();
        verify(mockField2Type).getPackageName();
        verify(mockParentClass).registerImport(anySet());
        verify(mockParentClass).getAppliedCode(true);
        verify(mockInterface1).registerImport(anySet());
        verify(mockInterface1).getAppliedCode(false);
        verify(mockInterface2).registerImport(anySet());
        verify(mockInterface2).getAppliedCode(false);
        verify(mockInterface3).registerImport(anySet());
        verify(mockInterface3).getAppliedCode(false);
        verify(mockCtor1).generate(any(CodeBuilder.class), anySet());
        verify(mockCtor2).generate(any(CodeBuilder.class), anySet());
        verify(mockCtor3).generate(any(CodeBuilder.class), anySet());
        verify(mockGeneric1).generateDefinition();
        verify(mockGeneric2).generateDefinition();
        verify(mockGeneric3).generateDefinition();
        verify(mockGeneric1).registerImport(anySet());
        verify(mockGeneric2).registerImport(anySet());
        verify(mockGeneric3).registerImport(anySet());
        verify(mockClassType, atLeastOnce()).getFullyQualifiedName();
    }
    
    /**
     * Verify that fields can be retrieved by annotation
     */
    @Test
    public void testGetFieldByAnnotation() {
        jclass.addField(mockField1);
        jclass.addField(mockField2);
        jclass.addField(mockField3);
        
        // Annotation is not present on any
        when(mockField1.hasAnnotation(Override.class)).thenReturn(false);
        when(mockField2.hasAnnotation(Override.class)).thenReturn(false);
        when(mockField3.hasAnnotation(Override.class)).thenReturn(false);
        Assertions.assertIterableEquals(Collections.emptyList(), jclass.getFields(Override.class));
        verify(mockField1).hasAnnotation(Override.class);
        verify(mockField2).hasAnnotation(Override.class);
        verify(mockField3).hasAnnotation(Override.class);

        // Annotation is present on one
        when(mockField1.hasAnnotation(Override.class)).thenReturn(false);
        when(mockField2.hasAnnotation(Override.class)).thenReturn(true);
        when(mockField3.hasAnnotation(Override.class)).thenReturn(false);
        Assertions.assertIterableEquals(Collections.singletonList(mockField2), jclass.getFields(Override.class));
        verify(mockField1, times(2)).hasAnnotation(Override.class);
        verify(mockField2, times(2)).hasAnnotation(Override.class);
        verify(mockField3, times(2)).hasAnnotation(Override.class);

        // Annotation is present on two
        when(mockField1.hasAnnotation(Override.class)).thenReturn(true);
        when(mockField2.hasAnnotation(Override.class)).thenReturn(false);
        when(mockField3.hasAnnotation(Override.class)).thenReturn(true);
        Assertions.assertIterableEquals(Arrays.asList(mockField1, mockField3), jclass.getFields(Override.class));
        verify(mockField1, times(3)).hasAnnotation(Override.class);
        verify(mockField2, times(3)).hasAnnotation(Override.class);
        verify(mockField3, times(3)).hasAnnotation(Override.class);

        // Annotation is present on all
        when(mockField1.hasAnnotation(Override.class)).thenReturn(true);
        when(mockField2.hasAnnotation(Override.class)).thenReturn(true);
        when(mockField3.hasAnnotation(Override.class)).thenReturn(true);
        Assertions.assertIterableEquals(Arrays.asList(mockField1, mockField2, mockField3), jclass.getFields(Override.class));
        verify(mockField1, times(4)).hasAnnotation(Override.class);
        verify(mockField2, times(4)).hasAnnotation(Override.class);
        verify(mockField3, times(4)).hasAnnotation(Override.class);
    }
    
    /**
     * Verify that methods can be retrieved by annotation
     */
    @Test
    public void testGetMethodByAnnotation() {
        jclass.addMethod(mockVoidMethod);
        jclass.addMethod(mockPrimitiveMethod);
        jclass.addMethod(mockClassMethod);
        
        // Annotation is not present on any
        when(mockVoidMethod.hasAnnotation(Override.class)).thenReturn(false);
        when(mockPrimitiveMethod.hasAnnotation(Override.class)).thenReturn(false);
        when(mockClassMethod.hasAnnotation(Override.class)).thenReturn(false);
        Assertions.assertIterableEquals(Collections.emptyList(), jclass.getMethods(Override.class));
        verify(mockVoidMethod).hasAnnotation(Override.class);
        verify(mockPrimitiveMethod).hasAnnotation(Override.class);
        verify(mockClassMethod).hasAnnotation(Override.class);
        
        // Annotation is present on one
        when(mockVoidMethod.hasAnnotation(Override.class)).thenReturn(false);
        when(mockPrimitiveMethod.hasAnnotation(Override.class)).thenReturn(false);
        when(mockClassMethod.hasAnnotation(Override.class)).thenReturn(true);
        Assertions.assertIterableEquals(Collections.singletonList(mockClassMethod), jclass.getMethods(Override.class));
        verify(mockVoidMethod, times(2)).hasAnnotation(Override.class);
        verify(mockPrimitiveMethod, times(2)).hasAnnotation(Override.class);
        verify(mockClassMethod, times(2)).hasAnnotation(Override.class);
        
        // Annotation is present on two
        when(mockVoidMethod.hasAnnotation(Override.class)).thenReturn(true);
        when(mockPrimitiveMethod.hasAnnotation(Override.class)).thenReturn(true);
        when(mockClassMethod.hasAnnotation(Override.class)).thenReturn(false);
        Assertions.assertIterableEquals(Arrays.asList(mockVoidMethod, mockPrimitiveMethod), jclass.getMethods(Override.class));
        verify(mockVoidMethod, times(3)).hasAnnotation(Override.class);
        verify(mockPrimitiveMethod, times(3)).hasAnnotation(Override.class);
        verify(mockClassMethod, times(3)).hasAnnotation(Override.class);
        
        // Annotation is present on all
        when(mockVoidMethod.hasAnnotation(Override.class)).thenReturn(true);
        when(mockPrimitiveMethod.hasAnnotation(Override.class)).thenReturn(true);
        when(mockClassMethod.hasAnnotation(Override.class)).thenReturn(true);
        Assertions.assertIterableEquals(Arrays.asList(mockVoidMethod, mockPrimitiveMethod, mockClassMethod), jclass.getMethods(Override.class));
        verify(mockVoidMethod, times(4)).hasAnnotation(Override.class);
        verify(mockPrimitiveMethod, times(4)).hasAnnotation(Override.class);
        verify(mockClassMethod, times(4)).hasAnnotation(Override.class);
    }
    
    /**
     * Verify that constructors can be retrieved by annotation
     */
    @Test
    public void testGetCtorByAnnotation() {
        jclass.addConstructor(mockCtor1);
        jclass.addConstructor(mockCtor2);
        jclass.addConstructor(mockCtor3);
        
        // Annotation is not present on any
        when(mockCtor1.hasAnnotation(Override.class)).thenReturn(false);
        when(mockCtor2.hasAnnotation(Override.class)).thenReturn(false);
        when(mockCtor3.hasAnnotation(Override.class)).thenReturn(false);
        Assertions.assertIterableEquals(Collections.emptyList(), jclass.getConstructors(Override.class));
        verify(mockCtor1).hasAnnotation(Override.class);
        verify(mockCtor2).hasAnnotation(Override.class);
        verify(mockCtor3).hasAnnotation(Override.class);
        
        // Annotation is present on one
        when(mockCtor1.hasAnnotation(Override.class)).thenReturn(true);
        when(mockCtor2.hasAnnotation(Override.class)).thenReturn(false);
        when(mockCtor3.hasAnnotation(Override.class)).thenReturn(false);
        Assertions.assertIterableEquals(Collections.singletonList(mockCtor1), jclass.getConstructors(Override.class));
        verify(mockCtor1, times(2)).hasAnnotation(Override.class);
        verify(mockCtor2, times(2)).hasAnnotation(Override.class);
        verify(mockCtor3, times(2)).hasAnnotation(Override.class);
        
        // Annotation is present on two
        when(mockCtor1.hasAnnotation(Override.class)).thenReturn(true);
        when(mockCtor2.hasAnnotation(Override.class)).thenReturn(false);
        when(mockCtor3.hasAnnotation(Override.class)).thenReturn(true);
        Assertions.assertIterableEquals(Arrays.asList(mockCtor1, mockCtor3), jclass.getConstructors(Override.class));
        verify(mockCtor1, times(3)).hasAnnotation(Override.class);
        verify(mockCtor2, times(3)).hasAnnotation(Override.class);
        verify(mockCtor3, times(3)).hasAnnotation(Override.class);
        
        // Annotation is present on all
        when(mockCtor1.hasAnnotation(Override.class)).thenReturn(true);
        when(mockCtor2.hasAnnotation(Override.class)).thenReturn(true);
        when(mockCtor3.hasAnnotation(Override.class)).thenReturn(true);
        Assertions.assertIterableEquals(Arrays.asList(mockCtor1, mockCtor2, mockCtor3), jclass.getConstructors(Override.class));
        verify(mockCtor1, times(4)).hasAnnotation(Override.class);
        verify(mockCtor2, times(4)).hasAnnotation(Override.class);
        verify(mockCtor3, times(4)).hasAnnotation(Override.class);
    }

    /**
     * Verify that the generated code matches the expected lines
     */
    private void assertGeneratedCode() {
        strMatcher.match(jclass.generateCode());
    }

    /**
     * Prepare the expected matchers for the start of the class.
     * 
     * @param isFinal         boolean true if the class is to be defined as final
     * @param expectedGenerics      {@link String} indicating the generics to be applied to the class
     * @param expectedImports {@link Class} representing what is expected to be imported
     */
    private void startDefinition(boolean isFinal, String expectedGenerics, Class<?>... expectedImports) {
        startDefinition(Collections.emptyList(), isFinal, expectedGenerics, expectedImports);
    }

    /**
     * Prepare the expected matchers for the start of the class.
     * 
     * @param annotations     {@link List} of {@link String}s representing additional annotations that should be present
     * @param isFinal         boolean true if the class is to be defined as final
     * @param expectedGenerics      {@link String} indicating the generics to be applied to the class
     * @param expectedImports {@link Class} representing what is expected to be imported
     */
    private void startDefinition(List<String> annotations, boolean isFinal, String expectedGenerics, Class<?>... expectedImports) {
        startDefinition(annotations, isFinal, expectedGenerics, Collections.emptyList(), expectedImports);
    }

    /**
     * Prepare the expected matchers for the start of the class.
     * 
     * @param annotations           {@link List} of {@link String}s representing additional annotations that should be present
     * @param isFinal               boolean true if the class is to be defined as final
     * @param expectedGenerics      {@link String} indicating the generics to be applied to the class
     * @param expectedCustomImports {@link List} of {@link String}s indicating what custom (not class derived) imports to use
     * @param expectedImports       {@link Class} representing what is expected to be imported
     */
    private void startDefinition(List<String> annotations, boolean isFinal, String expectedGenerics, List<String> expectedCustomImports, Class<?>... expectedImports) {
        startDefinition(annotations, isFinal, expectedGenerics, expectedCustomImports, "", expectedImports);
    }

    /**
     * Prepare the expected matchers for the start of the class.
     * 
     * @param annotations           {@link List} of {@link String}s representing additional annotations that should be present
     * @param isFinal               boolean true if the class is to be defined as final
     * @param expectedGenerics      {@link String} indicating the generics to be applied to the class
     * @param expectedCustomImports {@link List} of {@link String}s indicating what custom (not class derived) imports to use
     * @param expectedHierarchy     {@link String} indicating the defined class hierarchy (i.e.: extends/implements)
     * @param expectedImports       {@link Class} representing what is expected to be imported
     */
    private void startDefinition(List<String> annotations, boolean isFinal, String expectedGenerics, List<String> expectedCustomImports, String expectedHierarchy, Class<?>... expectedImports) {
        List<String> imports = new ArrayList<>(expectedCustomImports);
        for (Class<?> c : expectedImports) {
            imports.add(c.getName());
        }

        startDefinition(annotations, imports, isFinal, expectedGenerics, expectedHierarchy);
    }

    /**
     * Prepare the expected matchers for the start of the class.
     * 
     * @param annotations       {@link List} of {@link String}s representing additional annotations that should be present
     * @param expectedImports   {@link List} of {@link String}s representing what is expected to be imported
     * @param isFinal           boolean true if the class is to be defined as final
     * @param expectedGenerics  {@link String} indicating the generics to be applied to the class
     * @param expectedHierarchy {@link String} indicating the defined class hierarchy (i.e.: extends/implements)
     */
    private void startDefinition(List<String> annotations, List<String> expectedImports, boolean isFinal, String expectedGenerics, String expectedHierarchy) {
        strMatcher = new MultiLineStringMatcher();

        // Prepare the package information
        strMatcher.eq("package packageName;");
        strMatcher.eq((""));

        // Include the imports
        List<String> imports = new ArrayList<>(expectedImports);
        imports.sort((l, r) -> l.compareTo(r));
        for (String s : imports)
            strMatcher.eq(("import " + s + ";"));

        // Add the annotations
        strMatcher.eq((""));
        for (String s : annotations)
            strMatcher.eq((s));

        // Create the class signature
        expectedHierarchy = expectedHierarchy.trim();
        if (!expectedHierarchy.isEmpty())
            expectedHierarchy += " ";
        
        String finalKeyword = isFinal ? "final " : "";
        strMatcher.eq((finalKeyword + "ClassType ClassName" + expectedGenerics + " " + expectedHierarchy + "{"));
        strMatcher.eq((""));
    }

    /**
     * Add the termination of the class definition, after the meat of the class has been included.
     */
    private void endDefinition() {
        strMatcher.eq(("}"));
    }
}
