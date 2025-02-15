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
package tendril.annotationprocessor;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.ModuleElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.Parameterizable;
import javax.lang.model.element.QualifiedNameable;
import javax.lang.model.element.RecordComponentElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.JavaFileObject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.codegen.annotation.JAnnotationFactory;
import tendril.codegen.classes.JParameter;
import tendril.codegen.classes.ParameterBuilder;
import tendril.codegen.classes.method.AnonymousMethod;
import tendril.codegen.classes.method.JMethod;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.PrimitiveType;
import tendril.codegen.field.value.JValueFactory;
import tendril.test.AbstractUnitTest;

/**
 * Test case for {@link AbstractTendrilProccessor}
 */
public class AbstractTendrilProccessorTest extends AbstractUnitTest {

    /**
     * Concrete implementation to use for testing
     */
    private class TestTendrilProcessor extends AbstractTendrilProccessor {
        // Flags for tracking the calls to the processing methods
        private int timesTypeCalled = 0;
        private ClassType lastClassTypeData;
        private int timesMethodCalled = 0;
        private ClassType lastMethodTypeClassData;
        private JMethod<?> lastMethodTypeMethodData;

        /**
         * CTOR - prepare for test
         */
        private TestTendrilProcessor() {
            this.processingEnv = mockProcessingEnv;
        }

        /**
         * @see tendril.annotationprocessor.AbstractTendrilProccessor#processType(tendril.codegen.field.type.ClassType)
         */
        protected ClassDefinition processType(ClassType data) {
            timesTypeCalled++;
            lastClassTypeData = data;
            return mockGeneratedDefForClass;
        }

        /**
         * Verify that the call to {@code processType} was as expected
         * 
         * @param expectedTimesCalled int the number of times it was expected to be called
         * @param expectedData        {@link ClassType} which should have been provided with the last call
         */
        private void verifyClassType(int expectedTimesCalled, ClassType expectedData) {
            Assertions.assertEquals(expectedTimesCalled, timesTypeCalled);
            Assertions.assertEquals(expectedData, lastClassTypeData);
        }

        /**
         * @see tendril.annotationprocessor.AbstractTendrilProccessor#processMethod(tendril.codegen.field.type.ClassType, tendril.codegen.classes.method.JMethod)
         */
        protected ClassDefinition processMethod(ClassType classData, JMethod<?> methodData) {
            timesMethodCalled++;
            lastMethodTypeClassData = classData;
            lastMethodTypeMethodData = methodData;
            return mockGeneratedDefForMethod;
        }

        /**
         * Verify that the call to {@code processMethod} was as expected
         * 
         * @param expectedTimesCalled int the number of times it was expected to be called
         * @param expectedClassData   {@link ClassType} which should have been provided with the last call
         * @param expectedMethodData  {@link JMethod} which should have been provided with the last call
         */
        private void verifyMethodType(int expectedTimesCalled, ClassType expectedClassData, JMethod<?> expectedMethodData) {
            Assertions.assertEquals(expectedTimesCalled, timesMethodCalled);
            Assertions.assertEquals(expectedClassData, lastMethodTypeClassData);
            Assertions.assertEquals(expectedMethodData, lastMethodTypeMethodData);

            if (expectedMethodData == null)
                return;

            // Verify parameter annotations
            List<JParameter<?>> expectedParams = expectedMethodData.getParameters();
            List<JParameter<?>> actualParams = lastMethodTypeMethodData.getParameters();
            Assertions.assertEquals(expectedParams.size(), actualParams.size());
            for (int i = 0; i < expectedParams.size(); i++) {
                JParameter<?> expectedParam = expectedParams.get(i);
                JParameter<?> actualParam = actualParams.get(i);
                Assertions.assertIterableEquals(expectedParam.getAnnotations(), actualParam.getAnnotations());
            }
        }
    }

    // Mocks to use for testing
    @Mock
    private TypeElement mockAnnotation;
    @Mock
    private RoundEnvironment mockEnvironment;
    @Mock
    private TypeElement mockTypeElement;
    @Mock
    private Name mockSimpleName;
    @Mock
    private Name mockFullyQualifiedName;
    @Mock
    private ExecutableElement mockMethodElement;
    @Mock
    private Name mockMethodName;
    @Mock
    private TypeMirror mockTypeMirror;
    @Mock
    private ExecutableType mockMethodType;
    @Mock
    private TypeMirror mockParam1TypeMirror;
    @Mock
    private VariableElement mockParam1Var;
    @Mock
    private Name mockParam1Name;
    @Mock
    private TypeMirror mockParam2TypeMirror;
    @Mock
    private VariableElement mockParam2Var;
    @Mock
    private Name mockParam2Name;
    @Mock
    private AnnotationMirror mockParam2Annotation1;
    @Mock
    private DeclaredType mockParam2Annotation1Type;
    @Mock
    private TypeElement mockParam2Annotation1Element;
    @Mock
    private Name mockParam2Annotation1SimpleName;
    @Mock
    private Name mockParam2Annotation1QualifiedName;
    @Mock
    private ExecutableElement mockParam2Annotation1Method;
    @Mock
    private ExecutableType mockParam2Annotation1MethodType;
    @Mock
    private TypeMirror mockParam2Annotation1ReturnType;
    @Mock
    private Name mockParam2Annotation1MethodName;
    @Mock
    private AnnotationValue mockParam2Annotation1Value;
    @Mock
    private AnnotationMirror mockParam2Annotation2;
    @Mock
    private DeclaredType mockParam2Annotation2Type;
    @Mock
    private TypeElement mockParam2Annotation2Element;
    @Mock
    private Name mockParam2Annotation2SimpleName;
    @Mock
    private Name mockParam2Annotation2QualifiedName;
    @Mock
    private ExecutableElement mockParam2Annotation2Method;
    @Mock
    private Name mockParam2Annotation2MethodName;
    @Mock
    private ModuleElement mockModuleElement;
    @Mock
    private PackageElement mockPackageElement;
    @Mock
    private Parameterizable mockParameterizableElement;
    @Mock
    private QualifiedNameable mockQualifiedNameableElement;
    @Mock
    private RecordComponentElement mockRecordComponentElement;
    @Mock
    private TypeParameterElement mockParameterElement;
    @Mock
    private VariableElement mockVariableElement;
    @Mock
    private Elements mockElementUtils;
    @Mock
    private Types mockTypeUtils;
    @Mock
    private ProcessingEnvironment mockProcessingEnv;
    @Mock
    private ClassDefinition mockGeneratedDefForClass;
    @Mock
    private ClassDefinition mockGeneratedDefForMethod;
    @Mock
    private ClassType mockGeneratedType;
    @Mock
    private Filer mockFiler;
    @Mock
    private JavaFileObject mockFileObject;
    @Mock
    private Writer mockFileWriter;

    // Instance to test
    private TestTendrilProcessor processor;

    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        processor = new TestTendrilProcessor();

        lenient().when(mockEnvironment.errorRaised()).thenReturn(false);
        lenient().when(mockEnvironment.processingOver()).thenReturn(false);
    }

    /**
     * Verify that processing doesn't happen is there is an error or processing is complete
     */
    @Test
    public void testEnvShouldNotBeProcessed() {
        // If error raised, do nothing
        when(mockEnvironment.errorRaised()).thenReturn(true);
        Assertions.assertFalse(processor.process(Set.of(mockAnnotation), mockEnvironment));
        verify(mockEnvironment).errorRaised();

        // If no error raised, but processing is complete, still do nothing
        when(mockEnvironment.errorRaised()).thenReturn(false);
        when(mockEnvironment.processingOver()).thenReturn(true);
        Assertions.assertFalse(processor.process(Set.of(mockAnnotation), mockEnvironment));
        verify(mockEnvironment, times(2)).errorRaised();
        verify(mockEnvironment).processingOver();
    }

    /**
     * Verify that everything works properly if no element is detected
     */
    @Test
    public void testNothingToProcess() {
        when(mockEnvironment.getElementsAnnotatedWith(mockAnnotation)).thenReturn(Collections.emptySet());
        Assertions.assertFalse(processor.process(Set.of(mockAnnotation), mockEnvironment));
        verify(mockEnvironment).errorRaised();
        verify(mockEnvironment).processingOver();
        verify(mockEnvironment).getElementsAnnotatedWith(mockAnnotation);

        processor.verifyClassType(0, null);
        processor.verifyMethodType(0, null, null);
    }

    /**
     * Verify that processing doesn't happen if the element is not one of the desired types
     */
    @Test
    public void testDoNotProcessUndesiredTypes() {
        doReturn(Set.of(mockModuleElement, mockPackageElement, mockParameterizableElement, mockQualifiedNameableElement, mockRecordComponentElement, mockParameterElement, mockVariableElement))
                .when(mockEnvironment).getElementsAnnotatedWith(mockAnnotation);
        processor.process(Set.of(mockAnnotation), mockEnvironment);
        verify(mockEnvironment).errorRaised();
        verify(mockEnvironment).processingOver();
        verify(mockEnvironment).getElementsAnnotatedWith(mockAnnotation);

        processor.verifyClassType(0, null);
        processor.verifyMethodType(0, null, null);
    }

    /**
     * Verify that a class element is properly processed
     * @throws IOException 
     */
    @Test
    public void testProcessClass() throws IOException {
        when(mockTypeElement.getSimpleName()).thenReturn(mockSimpleName);
        when(mockSimpleName.toString()).thenReturn("Qwerty");
        when(mockTypeElement.getQualifiedName()).thenReturn(mockFullyQualifiedName);
        when(mockFullyQualifiedName.toString()).thenReturn("a.b.c.d.Qwerty");
        when(mockGeneratedDefForClass.getType()).thenReturn(mockGeneratedType);
        when(mockGeneratedType.getFullyQualifiedName()).thenReturn("z.x.c.V");
        when(mockGeneratedDefForClass.getCode()).thenReturn("classCode");
        when(mockProcessingEnv.getFiler()).thenReturn(mockFiler);
        when(mockFiler.createSourceFile("z.x.c.V")).thenReturn(mockFileObject);
        when(mockFileObject.openWriter()).thenReturn(mockFileWriter);

        // This mock format is required due to compilation error with "normal" method
        doReturn(Set.of(mockTypeElement)).when(mockEnvironment).getElementsAnnotatedWith(mockAnnotation);
        Assertions.assertFalse(processor.process(Set.of(mockAnnotation), mockEnvironment));
        verify(mockEnvironment).errorRaised();
        verify(mockEnvironment).processingOver();
        verify(mockEnvironment).getElementsAnnotatedWith(mockAnnotation);
        verify(mockTypeElement).getSimpleName();
        verify(mockTypeElement).getQualifiedName();
        verify(mockFileWriter).write("classCode", 0, "classCode".length());
        verify(mockFileWriter).close();

        processor.verifyClassType(1, new ClassType("a.b.c.d.Qwerty"));
        processor.verifyMethodType(0, null, null);
    }

    /**
     * Verify that a method is properly processed
     * @throws IOException 
     */
    @Test
    public void testProcessMethod() throws IOException {
        when(mockTypeElement.getSimpleName()).thenReturn(mockSimpleName);
        when(mockSimpleName.toString()).thenReturn("Qwerty");
        when(mockTypeElement.getQualifiedName()).thenReturn(mockFullyQualifiedName);
        when(mockFullyQualifiedName.toString()).thenReturn("a.b.c.d.Qwerty");

        // This mock format is required due to compilation error with "normal" method
        doReturn(Set.of(mockMethodElement)).when(mockEnvironment).getElementsAnnotatedWith(mockAnnotation);
        when(mockMethodElement.getEnclosingElement()).thenReturn(mockTypeElement);
        when(mockMethodElement.getReturnType()).thenReturn(mockTypeMirror);
        when(mockTypeMirror.getKind()).thenReturn(TypeKind.BOOLEAN);
        when(mockMethodElement.asType()).thenReturn(mockMethodType);
        doReturn(Arrays.asList(mockParam1TypeMirror, mockParam2TypeMirror)).when(mockMethodType).getParameterTypes();
        when(mockParam1TypeMirror.getKind()).thenReturn(TypeKind.CHAR);
        when(mockParam2TypeMirror.getKind()).thenReturn(TypeKind.DOUBLE);
        doReturn(Arrays.asList(mockParam1Var, mockParam2Var)).when(mockMethodElement).getParameters();
        when(mockParam1Var.getSimpleName()).thenReturn(mockParam1Name);
        when(mockParam1Var.getAnnotationMirrors()).thenReturn(Collections.emptyList());
        when(mockParam1Name.toString()).thenReturn("param1");
        when(mockParam2Var.getSimpleName()).thenReturn(mockParam2Name);
        when(mockParam2Name.toString()).thenReturn("param2");

        doReturn(Arrays.asList(mockParam2Annotation1, mockParam2Annotation2)).when(mockParam2Var).getAnnotationMirrors();
        when(mockParam2Annotation1.getAnnotationType()).thenReturn(mockParam2Annotation1Type);
        when(mockParam2Annotation1Type.asElement()).thenReturn(mockParam2Annotation1Element);
        when(mockParam2Annotation1Element.getSimpleName()).thenReturn(mockParam2Annotation1SimpleName);
        when(mockParam2Annotation1Element.getQualifiedName()).thenReturn(mockParam2Annotation1QualifiedName);
        when(mockParam2Annotation1SimpleName.toString()).thenReturn("D");
        when(mockParam2Annotation1QualifiedName.toString()).thenReturn("a.b.c.D");
        doReturn(Map.of(mockParam2Annotation1Method, mockParam2Annotation1Value)).when(mockParam2Annotation1).getElementValues();
        when(mockParam2Annotation1Method.getEnclosingElement()).thenReturn(mockParam2Annotation1Element);
        when(mockParam2Annotation1Method.asType()).thenReturn(mockParam2Annotation1MethodType);
        when(mockParam2Annotation1MethodType.getParameterTypes()).thenReturn(Collections.emptyList());
        when(mockParam2Annotation1Method.getParameters()).thenReturn(Collections.emptyList());
        when(mockParam2Annotation1Method.getSimpleName()).thenReturn(mockParam2Annotation1MethodName);
        when(mockParam2Annotation1Method.getReturnType()).thenReturn(mockParam2Annotation1ReturnType);
        when(mockParam2Annotation1ReturnType.getKind()).thenReturn(TypeKind.DECLARED);
        when(mockParam2Annotation1ReturnType.toString()).thenReturn(String.class.getName());
        when(mockParam2Annotation1MethodName.toString()).thenReturn("value");
        when(mockParam2Annotation1Value.getValue()).thenReturn("abc123");

        when(mockParam2Annotation2.getAnnotationType()).thenReturn(mockParam2Annotation2Type);
        when(mockParam2Annotation2Type.asElement()).thenReturn(mockParam2Annotation2Element);
        when(mockParam2Annotation2Element.getSimpleName()).thenReturn(mockParam2Annotation2SimpleName);
        when(mockParam2Annotation2Element.getQualifiedName()).thenReturn(mockParam2Annotation2QualifiedName);
        when(mockParam2Annotation2SimpleName.toString()).thenReturn("Y");
        when(mockParam2Annotation2QualifiedName.toString()).thenReturn("q.w.e.r.t.Y");
        doReturn(Map.of()).when(mockParam2Annotation2).getElementValues();

        when(mockMethodElement.getSimpleName()).thenReturn(mockMethodName);
        when(mockMethodName.toString()).thenReturn("mockMethod");

        when(mockGeneratedDefForMethod.getType()).thenReturn(mockGeneratedType);
        when(mockGeneratedType.getFullyQualifiedName()).thenReturn("y.u.i.O");
        when(mockGeneratedDefForMethod.getCode()).thenReturn("methodCode");
        when(mockProcessingEnv.getFiler()).thenReturn(mockFiler);
        when(mockFiler.createSourceFile("y.u.i.O")).thenReturn(mockFileObject);
        when(mockFileObject.openWriter()).thenReturn(mockFileWriter);

        Assertions.assertFalse(processor.process(Set.of(mockAnnotation), mockEnvironment));
        verify(mockEnvironment).errorRaised();
        verify(mockEnvironment).processingOver();
        verify(mockEnvironment).getElementsAnnotatedWith(mockAnnotation);
        verify(mockTypeElement).getSimpleName();
        verify(mockTypeElement).getQualifiedName();
        verify(mockMethodElement).getEnclosingElement();
        verify(mockMethodElement).getReturnType();
        verify(mockTypeMirror).getKind();
        verify(mockMethodElement).asType();
        verify(mockMethodType).getParameterTypes();
        verify(mockMethodElement).getParameters();
        verify(mockParam1TypeMirror).getKind();
        verify(mockParam1Var).getSimpleName();
        verify(mockParam1Var).getAnnotationMirrors();
        verify(mockParam2TypeMirror).getKind();
        verify(mockParam2Var).getSimpleName();
        verify(mockParam2Var).getAnnotationMirrors();
        verify(mockFileWriter).write("methodCode", 0, "methodCode".length());
        verify(mockFileWriter).close();

        AnonymousMethod<PrimitiveType> expectedMethod = new AnonymousMethod<>(PrimitiveType.BOOLEAN, "mockMethod");
        expectedMethod.addParameter(new ParameterBuilder<>(PrimitiveType.CHAR, "param1").build());
        expectedMethod
                .addParameter(new ParameterBuilder<>(PrimitiveType.DOUBLE, "param2").addAnnotation(JAnnotationFactory.create("a.b.c.D", Map.of("value", JValueFactory.create("abc123"))))
                        .addAnnotation(JAnnotationFactory.create("q.w.e.r.t.Y")).build());

        processor.verifyClassType(0, null);
        processor.verifyMethodType(1, new ClassType("a.b.c.d.Qwerty"), expectedMethod);
    }

    /**
     * Verify that parameter mismatch fails processing
     */
    @Test
    public void testMethodMoreParamsThanTypes() {
        when(mockTypeElement.getSimpleName()).thenReturn(mockSimpleName);
        when(mockSimpleName.toString()).thenReturn("Qwerty");
        when(mockTypeElement.getQualifiedName()).thenReturn(mockFullyQualifiedName);
        when(mockFullyQualifiedName.toString()).thenReturn("a.b.c.d.Qwerty");

        doReturn(Set.of(mockMethodElement)).when(mockEnvironment).getElementsAnnotatedWith(mockAnnotation);
        when(mockMethodElement.getEnclosingElement()).thenReturn(mockTypeElement);
        when(mockMethodElement.asType()).thenReturn(mockMethodType);
        doReturn(Arrays.asList(mockParam1TypeMirror)).when(mockMethodType).getParameterTypes();
        doReturn(Arrays.asList(mockParam1Var, mockParam2Var)).when(mockMethodElement).getParameters();

        Assertions.assertThrows(ProcessingException.class, () -> processor.process(Set.of(mockAnnotation), mockEnvironment));
        verify(mockEnvironment).errorRaised();
        verify(mockEnvironment).processingOver();
    }

    /**
     * Verify that parameter mismatch fails processing
     */
    @Test
    public void testMethodMoreTypesThanParams() {
        when(mockTypeElement.getSimpleName()).thenReturn(mockSimpleName);
        when(mockSimpleName.toString()).thenReturn("Qwerty");
        when(mockTypeElement.getQualifiedName()).thenReturn(mockFullyQualifiedName);
        when(mockFullyQualifiedName.toString()).thenReturn("a.b.c.d.Qwerty");

        doReturn(Set.of(mockMethodElement)).when(mockEnvironment).getElementsAnnotatedWith(mockAnnotation);
        when(mockMethodElement.getEnclosingElement()).thenReturn(mockTypeElement);
        when(mockMethodElement.asType()).thenReturn(mockMethodType);
        doReturn(Arrays.asList(mockParam1TypeMirror, mockParam2TypeMirror)).when(mockMethodType).getParameterTypes();
        doReturn(Arrays.asList(mockParam1Var)).when(mockMethodElement).getParameters();

        Assertions.assertThrows(ProcessingException.class, () -> processor.process(Set.of(mockAnnotation), mockEnvironment));
        verify(mockEnvironment).errorRaised();
        verify(mockEnvironment).processingOver();
    }

    /**
     * Verify that the assignability is properly determined
     */
    @Test
    public void testAssignable() {
        when(mockProcessingEnv.getElementUtils()).thenReturn(mockElementUtils);
        when(mockElementUtils.getTypeElement(anyString())).thenReturn(mockAnnotation);
        when(mockAnnotation.asType()).thenReturn(mockParam1TypeMirror);
        when(mockProcessingEnv.getTypeUtils()).thenReturn(mockTypeUtils);
        when(mockTypeElement.asType()).thenReturn(mockTypeMirror);

        when(mockTypeUtils.isAssignable(mockTypeMirror, mockParam1TypeMirror)).thenReturn(false);
        Assertions.assertFalse(processor.isTypeOf(mockTypeElement, getClass()));
        verify(mockProcessingEnv).getElementUtils();
        verify(mockElementUtils).getTypeElement(anyString());
        verify(mockProcessingEnv).getTypeUtils();
        verify(mockTypeUtils).isAssignable(mockTypeMirror, mockParam1TypeMirror);

        when(mockTypeUtils.isAssignable(mockTypeMirror, mockParam1TypeMirror)).thenReturn(true);
        Assertions.assertTrue(processor.isTypeOf(mockTypeElement, getClass()));
        verify(mockProcessingEnv, times(2)).getElementUtils();
        verify(mockElementUtils, times(2)).getTypeElement(anyString());
        verify(mockProcessingEnv, times(2)).getTypeUtils();
        verify(mockTypeUtils, times(2)).isAssignable(mockTypeMirror, mockParam1TypeMirror);
    }
    
    /**
     * Verify that a generated null class definition is not written out.
     */
    @Test
    public void testNullClassDefinition() {
        processor.writeCode(null);
        verifyNoInteractions(mockFileWriter);
    }
}
