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
package tendril.processor;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.codegen.classes.ParameterBuilder;
import tendril.codegen.classes.method.AnonymousMethod;
import tendril.codegen.classes.method.JMethod;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.PrimitiveType;
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
         * @see tendril.processor.AbstractTendrilProccessor#processType(tendril.codegen.field.type.ClassType)
         */
        protected void processType(ClassType data) {
            timesTypeCalled++;
            lastClassTypeData = data;
        }
        
        /**
         * Verify that the call to {@code processType} was as expected
         * 
         * @param expectedTimesCalled int the number of times it was expected to be called
         * @param expectedData {@link ClassType} which should have been provided with the last call
         */
        private void verifyClassType(int expectedTimesCalled, ClassType expectedData) {
            Assertions.assertEquals(expectedTimesCalled, timesTypeCalled);
            Assertions.assertEquals(expectedData, lastClassTypeData);
        }

        /**
         * @see tendril.processor.AbstractTendrilProccessor#processMethod(tendril.codegen.field.type.ClassType, tendril.codegen.classes.method.JMethod)
         */
        protected void processMethod(ClassType classData, JMethod<?> methodData) {
            timesMethodCalled++;
            lastMethodTypeClassData = classData;
            lastMethodTypeMethodData = methodData;
        }
        
        /**
         * Verify that the call to {@code processMethod} was as expected
         * 
         * @param expectedTimesCalled int the number of times it was expected to be called
         * @param expectedClassData {@link ClassType} which should have been provided with the last call
         * @param expectedMethodData {@link JMethod} which should have been provided with the last call
         */
        private void verifyMethodType(int expectedTimesCalled, ClassType expectedClassData, JMethod<?> expectedMethodData) {
            Assertions.assertEquals(expectedTimesCalled, timesMethodCalled);
            Assertions.assertEquals(expectedClassData, lastMethodTypeClassData);
            Assertions.assertEquals(expectedMethodData, lastMethodTypeMethodData);
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
    
    // Instance to test
    private TestTendrilProcessor processor;

    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        processor = new TestTendrilProcessor();

        when(mockEnvironment.errorRaised()).thenReturn(false);
        when(mockEnvironment.processingOver()).thenReturn(false);
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
     * Verify that a class element is properly processed
     */
    @Test
    public void testProcessClass() {
        when(mockTypeElement.getSimpleName()).thenReturn(mockSimpleName);
        when(mockSimpleName.toString()).thenReturn("Qwerty");
        when(mockTypeElement.getQualifiedName()).thenReturn(mockFullyQualifiedName);
        when(mockFullyQualifiedName.toString()).thenReturn("a.b.c.d.Qwerty");
        
        // This mock format is required due to compilation error with "normal" method
        doReturn(Set.of(mockTypeElement)).when(mockEnvironment).getElementsAnnotatedWith(mockAnnotation);
        Assertions.assertFalse(processor.process(Set.of(mockAnnotation), mockEnvironment));
        verify(mockEnvironment).errorRaised();
        verify(mockEnvironment).processingOver();
        verify(mockEnvironment).getElementsAnnotatedWith(mockAnnotation);
        verify(mockTypeElement).getSimpleName();
        verify(mockTypeElement).getQualifiedName();
        
        processor.verifyClassType(1, new ClassType("a.b.c.d.Qwerty"));
        processor.verifyMethodType(0, null, null);
    }
    
    /**
     * Verify that a method is properly processed
     */
    @Test
    public void testProcessMethod() {
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
        when(mockParam2Var.getAnnotationMirrors()).thenReturn(Collections.emptyList());
        when(mockParam2Name.toString()).thenReturn("param2");
        
        when(mockMethodElement.getSimpleName()).thenReturn(mockMethodName);
        when(mockMethodName.toString()).thenReturn("mockMethod");
        
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
        
        AnonymousMethod<PrimitiveType> expectedMethod = new AnonymousMethod<>(PrimitiveType.BOOLEAN, "mockMethod");
        expectedMethod.addParameter(new ParameterBuilder<>(PrimitiveType.CHAR, "param1").build());
        expectedMethod.addParameter(new ParameterBuilder<>(PrimitiveType.DOUBLE, "param2").build());
        
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
        
        Assertions.assertThrows(IllegalStateException.class, () -> processor.process(Set.of(mockAnnotation), mockEnvironment));
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
        
        Assertions.assertThrows(IllegalStateException.class, () -> processor.process(Set.of(mockAnnotation), mockEnvironment));
    }
}
