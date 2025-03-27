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
package tendril.annotationprocessor.exception;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.type.DeclaredType;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.test.AbstractUnitTest;

/**
 * Test case for the {@link MissingAnnotationException}
 */
public class MissingAnnotationExceptionTest extends AbstractUnitTest {
    
    // Mocks to use for testing
    @Mock
    private DeclaredType mockMissingType;
    @Mock
    private Element mockMissingElement;
    @Mock
    private Name mockName;
    @Mock
    private Element mockElement;
    
    // Instance to test
    private MissingAnnotationException exception;

    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        when(mockMissingType.asElement()).thenReturn(mockMissingElement);
        when(mockMissingElement.getSimpleName()).thenReturn(mockName);
        exception = new MissingAnnotationException(mockMissingType, mockElement);
        verify(mockMissingType).asElement();
        verify(mockMissingElement).getSimpleName();
    }

    /**
     * Verify that the exception properly represents the information
     */
    @Test
    public void testException() {
        Assertions.assertEquals("Unable to find a definition for the annotation mockMissingType applied to mockElement", exception.getMessage());
        Assertions.assertEquals("mockName", exception.getMissingAnnotationName());
    }
}
