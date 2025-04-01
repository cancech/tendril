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

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import tendril.annotationprocessor.element.ElementLoader;
import tendril.annotationprocessor.element.GeneratedAnnotationHandler;
import tendril.annotationprocessor.exception.ProcessingException;
import tendril.codegen.field.type.ClassType;
import tendril.test.AbstractUnitTest;

/**
 * Test case for the {@link AnnotationLoaderProcessor}
 */
public class AnnotationLoaderProcessorTest extends AbstractUnitTest {
    
    /**
     * Test override which performs the necessary initial setup to perform the test
     */
    private class TestLoader extends AnnotationLoaderProcessor {
        
        TestLoader() {
            this.currentClassType = mockClassType;
        }
        
    }
    
    // Mocks to use for testing
    @Mock
    private GeneratedAnnotationHandler mockAnnotationHandler;
    @Mock
    private AnnotationGeneratedListener mockListener1;
    @Mock
    private AnnotationGeneratedListener mockListener2;
    @Mock
    private AnnotationGeneratedListener mockListener3;
    @Mock
    private ClassType mockClassType;
    
    // Instance to test
    private AnnotationLoaderProcessor loader;

    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        try (MockedStatic<ElementLoader> mockLoader = Mockito.mockStatic(ElementLoader.class)) {
            mockLoader.when(() -> ElementLoader.getGeneratedAnnotationHandler()).thenReturn(mockAnnotationHandler);
            loader = new TestLoader();
            mockLoader.verify(() -> ElementLoader.getGeneratedAnnotationHandler());
            verify(mockAnnotationHandler).registerLoader(loader);
        }
    }

    /**
     * Verify that processType() triggers registered listeners
     */
    @Test
    public void testListenersTriggersOnProcessType() {
        // No listeners, "nothing" happens
        Assertions.assertNull(loader.processType());
        verifyAllChecked();
        
        // Single listeners, only it is triggered
        loader.addListener(mockListener1);
        Assertions.assertNull(loader.processType());
        verify(mockListener1).annotationGenerated(mockClassType);
        
        // Multiple listeners, all are triggered
        loader.addListener(mockListener2);
        loader.addListener(mockListener3);
        Assertions.assertNull(loader.processType());
        verify(mockListener1, times(2)).annotationGenerated(mockClassType);
        verify(mockListener2).annotationGenerated(mockClassType);
        verify(mockListener3).annotationGenerated(mockClassType);
    }
    
    /**
     * Verify that processMethod() cannot be done
     */
    @Test
    public void testProcessMethod() {
        Assertions.assertThrows(ProcessingException.class, () -> loader.processMethod());
    }
}
