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
         * @see tendril.codegen.classes.JClass#classType()
         */
        @Override
        protected String classType() {
            return "ClassType";
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
    private ClassType mockField1Type;
    @Mock
    private ClassType mockField2Type;
    @Mock
    private ClassType mockParentClass;
    @Mock
    private ClassType mockInterface1;
    @Mock
    private ClassType mockInterface2;
    @Mock
    private ClassType mockInterface3;
    @Mock
    private JConstructor mockCtor1;
    @Mock
    private JConstructor mockCtor2;
    @Mock
    private JConstructor mockCtor3;

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
        lenient().when(mockParentClass.getSimpleName()).thenReturn("mockParentClass");
        lenient().when(mockParentClass.getPackageName()).thenReturn("mock.class");
        lenient().when(mockParentClass.getFullyQualifiedName()).thenReturn("mock.class.mockParentClass");
        lenient().when(mockInterface1.getSimpleName()).thenReturn("mockInterface1");
        lenient().when(mockInterface1.getPackageName()).thenReturn("mock.class");
        lenient().when(mockInterface1.getFullyQualifiedName()).thenReturn("mock.class.mockInterface1");
        lenient().when(mockInterface2.getSimpleName()).thenReturn("mockInterface2");
        lenient().when(mockInterface2.getPackageName()).thenReturn("mock.class");
        lenient().when(mockInterface2.getFullyQualifiedName()).thenReturn("mock.class.mockInterface2");
        lenient().when(mockInterface3.getSimpleName()).thenReturn("mockInterface3");
        lenient().when(mockInterface3.getPackageName()).thenReturn("mock.class");
        lenient().when(mockInterface3.getFullyQualifiedName()).thenReturn("mock.class.mockInterface3");

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
        jclass = new TestJClass();
        verify(mockClassType).getPackageName();
        verify(mockClassType).getClassName();

        Assertions.assertEquals("ClassName", jclass.getName());
    }
    
    /**
     * Verify that static is properly handled.
     */
    @Test
    public void testSetStatic() {
        Assertions.assertFalse(jclass.isStatic());
        
        Assertions.assertThrows(IllegalArgumentException.class, () -> jclass.setStatic(true));
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
        startDefinition(false);
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
        startDefinition(true);
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
        startDefinition(Collections.emptyList(), false, Arrays.asList("mock.class.mockParentClass"), "extends mockParentClass");
        endDefinition();

        // Add the additional features
        jclass.setParentClass(mockParentClass);

        // Verify that it matches
        assertGeneratedCode();
        verify(mockParentClass).getSimpleName();
        verify(mockParentClass).getPackageName();
        verify(mockParentClass).getFullyQualifiedName();
    }

    /**
     * Verify that the class can have a parent
     */
    @Test
    public void testGenerateCodeWithInteface() {
        // Define what the code is expected to look like
        startDefinition(Collections.emptyList(), true, Arrays.asList("mock.class.mockInterface1"), "implements mockInterface1");
        endDefinition();

        // Add the additional features
        jclass.setFinal(true);
        jclass.setParentInterfaces(Collections.singletonList(mockInterface1));

        // Verify that it matches
        assertGeneratedCode();
        verify(mockInterface1).getSimpleName();
        verify(mockInterface1).getPackageName();
        verify(mockInterface1).getFullyQualifiedName();
    }

    /**
     * Verify that the class can have custom annotations
     */
    @Test
    public void testGenerateCodeWithAnnotations() {
        // Define what the code is expected to look like
        startDefinition(Arrays.asList("@TestMarkerAnnotation", "@TestPrimitiveAnnotation(PrimitiveType.BOOLEAN)"), false, TestMarkerAnnotation.class, TestPrimitiveAnnotation.class, PrimitiveType.class);
        endDefinition();

        // Add the additional features
        jclass.addAnnotation(JAnnotationFactory.create(TestMarkerAnnotation.class));
        jclass.addAnnotation(JAnnotationFactory.create(TestPrimitiveAnnotation.class, JValueFactory.create(PrimitiveType.BOOLEAN)));

        // Verify that it matches
        assertGeneratedCode();
    }

    /**
     * Verify that the class code is generated with fields
     */
    @Test
    public void testGenerateCodeWithField() {
        // Define what the code is expected to look like
        startDefinition(Collections.emptyList(), true, Arrays.asList("mockField1Type", "mockField2Type"));
        strMatcher.eq("    mockField1");
        strMatcher.eq("");
        strMatcher.eq("    mockField2");
        strMatcher.eq("");
        endDefinition();

        // Add the additional features
        jclass.setFinal(true);
        jclass.addField(mockField1);
        jclass.addField(mockField2);

        // Verify that it matches
        assertGeneratedCode();
        verify(mockField1).generate(any(CodeBuilder.class), anySet());
        verify(mockField2).generate(any(CodeBuilder.class), anySet());
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
        startDefinition(false);
        strMatcher.eq(("    mockCtor1"));
        strMatcher.eq((""));
        strMatcher.eq(("    mockCtor2"));
        strMatcher.eq((""));
        strMatcher.eq(("    mockCtor3"));
        strMatcher.eq((""));
        endDefinition();

        // Add the additional features
        jclass.addConstructor(mockCtor1);
        jclass.addConstructor(mockCtor2);
        jclass.addConstructor(mockCtor3);
        
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
        startDefinition(true);
        strMatcher.eq(("    mockVoidMethod"));
        strMatcher.eq((""));
        strMatcher.eq(("    mockPrimitiveMethod"));
        strMatcher.eq((""));
        strMatcher.eq(("    mockClassMethod"));
        strMatcher.eq((""));
        endDefinition();

        // Add the additional features
        jclass.setFinal(true);
        jclass.addMethod(mockVoidMethod);
        jclass.addMethod(mockPrimitiveMethod);
        jclass.addMethod(mockClassMethod);

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
    }

    /**
     * Verify that the class can have custom annotations, methods, and fields
     */
    @Test
    public void testGenerateComplexCode() {
        // Define what the code is expected to look like
        startDefinition(Arrays.asList("@TestMarkerAnnotation", "@TestPrimitiveAnnotation(PrimitiveType.BOOLEAN)"), false,
                Arrays.asList("mockField1Type", "mockField2Type", "mock.class.mockParentClass", "mock.class.mockInterface1", "mock.class.mockInterface2", "mock.class.mockInterface3"),
                "extends mockParentClass implements mockInterface1, mockInterface2, mockInterface3", TestMarkerAnnotation.class, TestPrimitiveAnnotation.class, PrimitiveType.class);
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
        jclass.addAnnotation(JAnnotationFactory.create(TestMarkerAnnotation.class));
        jclass.addAnnotation(JAnnotationFactory.create(TestPrimitiveAnnotation.class, JValueFactory.create(PrimitiveType.BOOLEAN)));
        jclass.addField(mockField1);
        jclass.addField(mockField2);
        jclass.addConstructor(mockCtor1);
        jclass.addConstructor(mockCtor2);
        jclass.addConstructor(mockCtor3);

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
        verify(mockParentClass).getSimpleName();
        verify(mockParentClass).getPackageName();
        verify(mockParentClass, atLeastOnce()).getFullyQualifiedName();
        verify(mockInterface1).getSimpleName();
        verify(mockInterface1).getPackageName();
        verify(mockInterface1, atLeastOnce()).getFullyQualifiedName();
        verify(mockInterface2).getSimpleName();
        verify(mockInterface2).getPackageName();
        verify(mockInterface2, atLeastOnce()).getFullyQualifiedName();
        verify(mockInterface3).getSimpleName();
        verify(mockInterface3).getPackageName();
        verify(mockInterface3, atLeastOnce()).getFullyQualifiedName();
        verify(mockCtor1).generate(any(CodeBuilder.class), anySet());
        verify(mockCtor2).generate(any(CodeBuilder.class), anySet());
        verify(mockCtor3).generate(any(CodeBuilder.class), anySet());
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
     * @param expectedImports {@link Class} representing what is expected to be imported
     */
    private void startDefinition(boolean isFinal, Class<?>... expectedImports) {
        startDefinition(Collections.emptyList(), isFinal, expectedImports);
    }

    /**
     * Prepare the expected matchers for the start of the class.
     * 
     * @param annotations     {@link List} of {@link String}s representing additional annotations that should be present
     * @param isFinal         boolean true if the class is to be defined as final
     * @param expectedImports {@link Class} representing what is expected to be imported
     */
    private void startDefinition(List<String> annotations, boolean isFinal, Class<?>... expectedImports) {
        startDefinition(annotations, isFinal, Collections.emptyList(), expectedImports);
    }

    /**
     * Prepare the expected matchers for the start of the class.
     * 
     * @param annotations           {@link List} of {@link String}s representing additional annotations that should be present
     * @param isFinal               boolean true if the class is to be defined as final
     * @param expectedCustomImports {@link List} of {@link String}s indicating what custom (not class derived) imports to use
     * @param expectedImports       {@link Class} representing what is expected to be imported
     */
    private void startDefinition(List<String> annotations, boolean isFinal, List<String> expectedCustomImports, Class<?>... expectedImports) {
        startDefinition(annotations, isFinal, expectedCustomImports, "", expectedImports);
    }

    /**
     * Prepare the expected matchers for the start of the class.
     * 
     * @param annotations           {@link List} of {@link String}s representing additional annotations that should be present
     * @param isFinal           boolean true if the class is to be defined as final
     * @param expectedCustomImports {@link List} of {@link String}s indicating what custom (not class derived) imports to use
     * @param expectedHierarchy     {@link String} indicating the defined class hierarchy (i.e.: extends/implements)
     * @param expectedImports       {@link Class} representing what is expected to be imported
     */
    private void startDefinition(List<String> annotations, boolean isFinal, List<String> expectedCustomImports, String expectedHierarchy, Class<?>... expectedImports) {
        List<String> imports = new ArrayList<>(expectedCustomImports);
        for (Class<?> c : expectedImports) {
            imports.add(c.getName());
        }

        startDefinition(annotations, imports, isFinal, expectedHierarchy);
    }

    /**
     * Prepare the expected matchers for the start of the class.
     * 
     * @param annotations       {@link List} of {@link String}s representing additional annotations that should be present
     * @param expectedImports   {@link List} of {@link String}s representing what is expected to be imported
     * @param isFinal           boolean true if the class is to be defined as final
     * @param expectedHierarchy {@link String} indicating the defined class hierarchy (i.e.: extends/implements)
     */
    private void startDefinition(List<String> annotations, List<String> expectedImports, boolean isFinal, String expectedHierarchy) {
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
        strMatcher.eq((finalKeyword + "ClassType ClassName " + expectedHierarchy + "{"));
        strMatcher.eq((""));
    }

    /**
     * Add the termination of the class definition, after the meat of the class has been included.
     */
    private void endDefinition() {
        strMatcher.eq(("}"));
    }
}
