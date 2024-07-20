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
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import tendril.codegen.CodeBuilder;
import tendril.codegen.Utilities;
import tendril.codegen.VisibilityType;
import tendril.codegen.annotation.JAnnotationFactory;
import tendril.codegen.classes.method.JMethod;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.PrimitiveType;
import tendril.codegen.field.type.Type;
import tendril.codegen.field.type.VoidType;
import tendril.codegen.field.value.JValueFactory;
import tendril.helper.annotation.TestMarkerAnnotation;
import tendril.helper.annotation.TestPrimitiveAnnotation;
import test.AbstractUnitTest;
import test.assertions.matchers.MultiLineStringMatcher;

/**
 * Test case for {@link JClass}
 */
public class JClassTest extends AbstractUnitTest {

    /**
     * Concrete implementation of {@link JClass} to be used for testing
     */
    private class TestJClass extends JClass {
        /** Holds the last method return type so that it can be verified */
        private Type methodReturnType;
        /** Holds the last method name so that it can be verified */
        private String methodName;

        /**
         * CTOR
         */
        protected TestJClass() {
            super(mockVisibility, mockClassType);
        }

        /**
         * Track the details of the last call, and return the mock builder
         */
        @SuppressWarnings("unchecked")
        @Override
        protected <RETURN_TYPE extends Type> MethodBuilder<RETURN_TYPE> createMethodBuilder(RETURN_TYPE returnType, String name) {
            methodReturnType = returnType;
            methodName = name;
            return (MethodBuilder<RETURN_TYPE>) mockMethodBuilder;
        }

        /**
         * Verify the details from the last build method call.
         * 
         * @param expectedType {@link Type} the expected return type
         * @param expectedName {@link String} the expected method name
         */
        public void verifyMethodDetails(Type expectedType, String expectedName) {
            Assertions.assertEquals(expectedType.getSimpleName(), methodReturnType.getSimpleName());
            Assertions.assertEquals(expectedName, methodName);
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
    private VisibilityType mockVisibility;
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

    // Helper to match the generate code
    private MultiLineStringMatcher strMatcher;
    // Instance to test
    private TestJClass jclass;

    /**
     * @see test.AbstractUnitTest#prepareTest()
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
        
        mockMethodGeneration(mockVoidMethod, "mockVoidMethod", mockNullPackageClassType, mockSamePackageClassType);
        mockMethodGeneration(mockPrimitiveMethod, "mockPrimitiveMethod", mockEmptyPackageClassType);
        mockMethodGeneration(mockClassMethod, "mockClassMethod", mockJavaLangPackageClassType);

        try (MockedStatic<Utilities> mockUtil = Mockito.mockStatic(Utilities.class)) {
            mockUtil.when(() -> Utilities.iso8061TimeStamp()).thenReturn("TIMESTAMP");

            when(mockClassType.getPackageName()).thenReturn("packageName");
            when(mockClassType.getClassName()).thenReturn("ClassName");
            jclass = new TestJClass();
            verify(mockClassType).getPackageName();
            verify(mockClassType).getClassName();
            mockUtil.verify(() -> Utilities.iso8061TimeStamp());

            Assertions.assertEquals("ClassName", jclass.getName());
        }
    }

    /**
     * Helper to allow for something to be generated for a method when it is produced
     * 
     * @param mockMethod {@link JMethod} mock method which is to be stubbed
     * @param toProduce  {@link String} code that it is to produce for inclusion in the class
     * @param toImport   {@link ClassType}... that should be imported by the method
     */
    @SuppressWarnings("unchecked")
    private void mockMethodGeneration(JMethod<?> mockMethod, String toProduce, ClassType... toImport) {
        lenient().doAnswer(inv -> {
            ((CodeBuilder) inv.getArgument(0)).append(toProduce);
            for (ClassType ct: toImport)
                ((Set<ClassType>) inv.getArgument(1)).add(ct);
            return null;
        }).when(mockMethod).generate(any(CodeBuilder.class), anySet());
    }

    /**
     * Verify that the method builder is properly created
     */
    @Test
    public void testBuildMethodBuilder() {
        Assertions.assertEquals(mockMethodBuilder, jclass.buildMethod("voidMethodName"));
        jclass.verifyMethodDetails(VoidType.INSTANCE, "voidMethodName");

        Assertions.assertEquals(mockMethodBuilder, jclass.buildMethod(PrimitiveType.INT, "intMethodName"));
        jclass.verifyMethodDetails(PrimitiveType.INT, "intMethodName");

        Assertions.assertEquals(mockMethodBuilder, jclass.buildMethod(TestJClass.class, "classMethodName"));
        jclass.verifyMethodDetails(new ClassType(TestJClass.class), "classMethodName");

        ClassType clsType = new ClassType("package", "class");
        Assertions.assertEquals(mockMethodBuilder, jclass.buildMethod(clsType, "classMethodName"));
        jclass.verifyMethodDetails(clsType, "classMethodName");
    }

    /**
     * Verify that the class code is generated when no methods are present
     */
    @Test
    public void testGenerateCodeNoAnnotationNoMethod() {
        // Define what the code is expected to look like
        startDefinition();
        endDefinition();

        // Verify that it matches
        assertGeneratedCode();
    }

    /**
     * Verify that the class can have custom annotations
     */
    @Test
    public void testGenerateCodeWithAnnotationsNoMethod() {
        // Define what the code is expected to look like
        startDefinition(Arrays.asList("@TestMarkerAnnotation", "@TestPrimitiveAnnotation(PrimitiveType.BOOLEAN)"), TestMarkerAnnotation.class, TestPrimitiveAnnotation.class, PrimitiveType.class);
        endDefinition();

        // Add the additional features
        jclass.annotate(JAnnotationFactory.create(TestMarkerAnnotation.class));
        jclass.annotate(JAnnotationFactory.create(TestPrimitiveAnnotation.class, JValueFactory.create(PrimitiveType.BOOLEAN)));

        // Verify that it matches
        assertGeneratedCode();
    }

    /**
     * Verify that the class can have methods
     */
    @Test
    public void testGenerateCodeNoAnnotationsWithMethod() {
        // Define what the code is expected to look like
        startDefinition(Collections.emptyList());
        strMatcher.eq(("    mockVoidMethod"));
        strMatcher.eq((""));
        strMatcher.eq(("    mockPrimitiveMethod"));
        strMatcher.eq((""));
        strMatcher.eq(("    mockClassMethod"));
        strMatcher.eq((""));
        endDefinition();

        // Add the additional features
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
     * Verify that the class can have custom annotations and methods
     */
    @Test
    public void testGenerateCodeWithAnnotationsWithMethods() {
        // Define what the code is expected to look like
        startDefinition(Arrays.asList("@TestMarkerAnnotation", "@TestPrimitiveAnnotation(PrimitiveType.BOOLEAN)"), TestMarkerAnnotation.class, TestPrimitiveAnnotation.class, PrimitiveType.class);
        strMatcher.eq("    mockVoidMethod");
        strMatcher.eq("");
        strMatcher.eq("    mockPrimitiveMethod");
        strMatcher.eq("");
        strMatcher.eq("    mockClassMethod");
        strMatcher.eq("");
        endDefinition();

        // Add the additional features
        jclass.addMethod(mockVoidMethod);
        jclass.addMethod(mockPrimitiveMethod);
        jclass.addMethod(mockClassMethod);
        jclass.annotate(JAnnotationFactory.create(TestMarkerAnnotation.class));
        jclass.annotate(JAnnotationFactory.create(TestPrimitiveAnnotation.class, JValueFactory.create(PrimitiveType.BOOLEAN)));

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
     * Verify that the generated code matches the expected lines
     */
    private void assertGeneratedCode() {
        strMatcher.match(jclass.generateCode());
    }

    /**
     * Prepare the expected matchers for the start of the class.
     * 
     * @param expectedImports {@link Class} representing what is expected to be imported
     */
    private void startDefinition(Class<?>... expectedImports) {
        startDefinition(Collections.emptyList(), expectedImports);
    }

    /**
     * Prepare the expected matchers for the start of the class.
     * 
     * @param annotations     {@link List} of {@link String}s representing additional annotations that should be present
     * @param expectedImports {@link Class} representing what is expected to be imported
     */
    private void startDefinition(List<String> annotations, Class<?>... expectedImports) {
        strMatcher = new MultiLineStringMatcher();

        // Prepare the package information
        strMatcher.eq("package packageName;");
        strMatcher.eq((""));

        // Include the imports
        for (String s : prepareImports(expectedImports))
            strMatcher.eq(("import " + s + ";"));

        // Add the annotations
        strMatcher.eq((""));
        strMatcher.eq(("@Generated(date = \"TIMESTAMP\", value = \"tendril\")"));
        for (String s : annotations)
            strMatcher.eq((s));

        // Create the class signature
        strMatcher.eq(("mockVisibility ClassType ClassName {"));
        strMatcher.eq((""));
    }

    /**
     * Process the imports to determine what should be presented to the user
     * 
     * @param expectedImports {@link Class}... of which classes should be imported in any order
     * @return {@link List} of {@link String} containing the fully qualified names of all classes that are to be imported, in the order they should be listed
     */
    private List<String> prepareImports(Class<?>... expectedImports) {
        List<String> imports = new ArrayList<>();
        imports.add("javax.annotation.processing.Generated");
        for (Class<?> c : expectedImports) {
            imports.add(c.getName());
        }

        imports.sort((l, r) -> l.compareTo(r));
        return imports;
    }

    /**
     * Add the termination of the class definition, after the meat of the class has been included.
     */
    private void endDefinition() {
        strMatcher.eq(("}"));
    }
}
