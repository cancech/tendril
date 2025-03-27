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
package tendril.annotationprocessor;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import tendril.annotationprocessor.element.ElementLoader;
import tendril.annotationprocessor.exception.MissingAnnotationException;
import tendril.codegen.classes.JClass;
import tendril.codegen.classes.method.JMethod;
import tendril.codegen.field.type.ClassType;

/**
 * Test case for {@link AbstractDelayedAnnotationTendrilProcessor}
 */
public class AbstractDelayedAnnotationTendrilProcessorTest extends CommonProcessorTest {
    
    /**
     * Concrete implementation to use for testing
     */
    private class TestDelayedProcessor extends AbstractDelayedAnnotationTendrilProcessor {
        
        /**
         * CTOR
         */
        private TestDelayedProcessor() {
            this.processingEnv = mockProcessingEnv;
        }

        /**
         * @see tendril.annotationprocessor.AbstractTendrilProccessor#processType()
         */
        @Override
        protected ClassDefinition processType() {
            return mockGeneratedDef;
        }

        /**
         * @see tendril.annotationprocessor.AbstractTendrilProccessor#processMethod()
         */
        @Override
        protected ClassDefinition processMethod() {
            return mockGeneratedDef;
        }
    }
    
    // Mocks to use for testing
    @Mock
    private MissingAnnotationException mockException;
    @Mock
    private TypeElement mockAnnotation;
    @Mock
    private TypeElement mockClassElement;
    @Mock
    private ExecutableElement mockMethodElement;
    @Mock
    private JClass mockClass;
    @Mock
    private JMethod<?> mockMethod;
    @Mock
    private ClassType mockType;
    @Mock
    private ClassType mockWaitingAnnotationType;
    
    // Instance to test
    private AbstractDelayedAnnotationTendrilProcessor processor;

    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        processor = new TestDelayedProcessor();
    }

    /**
     * Verify that a class can be processed if it has a missing annotation
     * 
     * @throws MissingAnnotationException
     * @throws IOException
     */
    @Test
    public void testProcessingClassDelayed() throws MissingAnnotationException, IOException {
        // First the attempt fails due to missing exception
        try (MockedStatic<ElementLoader> loader = Mockito.mockStatic(ElementLoader.class)) {
            when(mockException.getMissingAnnotationName()).thenReturn("abc");
            loader.when(() -> ElementLoader.retrieveClass(mockClassElement)).thenThrow(mockException);
            
            processor.processElement(mockAnnotation, mockClassElement);
            loader.verify(() -> ElementLoader.retrieveClass(mockClassElement));
            verify(mockException).getMissingAnnotationName();
        }
        
        // Then once it is generated, processing can pass
        try (MockedStatic<ElementLoader> loader = Mockito.mockStatic(ElementLoader.class)) {
            when(mockClass.getType()).thenReturn(mockType);
            loader.when(() -> ElementLoader.retrieveClass(mockClassElement)).thenReturn(mockClass);

            setupMocksForWriting();
            when(mockWaitingAnnotationType.getSimpleName()).thenReturn("abc");
            processor.annotationGenerated(mockWaitingAnnotationType);
            loader.verify(() -> ElementLoader.retrieveClass(mockClassElement));
            verify(mockWaitingAnnotationType).getSimpleName();
            verifyFileWritten();
        }
    }

    /**
     * Verify that a method can be processed if it has a missing annotation
     * 
     * @throws MissingAnnotationException
     * @throws IOException
     */
    @Test
    public void testProcessingMethodDelayed() throws MissingAnnotationException, IOException {
        // First the attempt fails due to missing exception
        try (MockedStatic<ElementLoader> loader = Mockito.mockStatic(ElementLoader.class)) {
            when(mockException.getMissingAnnotationName()).thenReturn("abc");
            loader.when(() -> ElementLoader.retrieveMethod(mockMethodElement)).thenThrow(mockException);
            
            processor.processElement(mockAnnotation, mockMethodElement);
            loader.verify(() -> ElementLoader.retrieveMethod(mockMethodElement));
            verify(mockException).getMissingAnnotationName();
        }
        
        // Then once it is generated, processing can pass
        try (MockedStatic<ElementLoader> loader = Mockito.mockStatic(ElementLoader.class)) {
            when(mockClass.getType()).thenReturn(mockType);
            loader.when(() -> ElementLoader.retrieveMethod(mockMethodElement)).thenReturn(Pair.of(mockClass, mockMethod));

            setupMocksForWriting();
            when(mockWaitingAnnotationType.getSimpleName()).thenReturn("abc");
            processor.annotationGenerated(mockWaitingAnnotationType);
            loader.verify(() -> ElementLoader.retrieveMethod(mockMethodElement));
            verify(mockWaitingAnnotationType).getSimpleName();
            verifyFileWritten();
        }
    }
    
    /**
     * Verify that no issues are encountered if an annotation is generated when it's not waited on
     */
    @Test
    public void testGeneratedButNotWaiting() {
        when(mockWaitingAnnotationType.getSimpleName()).thenReturn("SOMETHING");
        processor.annotationGenerated(mockWaitingAnnotationType);
        verify(mockWaitingAnnotationType).getSimpleName();
    }
}
