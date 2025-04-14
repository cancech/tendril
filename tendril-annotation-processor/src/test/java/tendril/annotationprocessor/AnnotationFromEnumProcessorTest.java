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
import static org.mockito.Mockito.when;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.annotationprocessor.exception.TendrilException;
import tendril.codegen.classes.method.JMethod;
import tendril.codegen.field.type.ClassType;
import tendril.test.AbstractUnitTest;

/**
 * Test case for the {@link AnnotationFromEnumProcessor}
 */
public class AnnotationFromEnumProcessorTest extends AbstractUnitTest {

    /** Test override to apply the mock processing environment */
    private class TestProcessor extends AnnotationFromEnumProcessor {

        private TestProcessor() {
            super(Override.class);
            this.processingEnv = mockProcessingEnv;
            this.currentClassType = mockAnnotatedClass;
            this.currentMethod = mockAnnotatedMethod;
        }

        /**
         * @see tendril.annotationprocessor.AbstractTendrilProccessor#processType()
         */
        @Override
        protected ClassDefinition processType() throws TendrilException {
            return null;
        }
    }
    
    // Mocks to use for testing
    @Mock
    private ProcessingEnvironment mockProcessingEnv;
    @Mock
    private ClassType mockAnnotatedClass;
    @Mock
    private JMethod<?> mockAnnotatedMethod;
    @Mock
    private TypeElement mockType;
    @Mock
    private Name mockName;

    // Instance to test
    private AnnotationFromEnumProcessor processor;

    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        processor = new TestProcessor();
    }

    /**
     * Verify that the type validation is performed properly
     * @throws TendrilException 
     */
    @Test
    public void testValidateType() throws TendrilException {
        int timesGetKind = 0;
        int timesGetQualifiedName = 0;
        when(mockType.getQualifiedName()).thenReturn(mockName);

        // Non-Enum types throw an exception
        for (ElementKind kind : ElementKind.values()) {
            if (kind == ElementKind.ENUM)
                continue;
            when(mockType.getKind()).thenReturn(kind);
            Assertions.assertThrows(TendrilException.class, () -> processor.validateType(mockType));
            verify(mockType, times(++timesGetKind)).getKind();
            verify(mockType, times(++timesGetQualifiedName)).getQualifiedName();
        }

        when(mockType.getKind()).thenReturn(ElementKind.ENUM);
        processor.validateType(mockType);
        verify(mockType, times(++timesGetKind)).getKind();
    }

    /**
     * Verify that attempting to process a method generates an exception
     */
    @Test
    public void testProcessMethod() {
        when(mockAnnotatedMethod.getName()).thenReturn("mockMethod");
        Assertions.assertThrows(TendrilException.class, () -> processor.processMethod());
        verify(mockAnnotatedClass).getFullyQualifiedName();
        verify(mockAnnotatedMethod).getName();
    }
}
