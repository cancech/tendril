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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.test.AbstractUnitTest;

/**
 * Test case for the {@link ProcessingException}
 */
public class ProcessingExceptionTest extends AbstractUnitTest {
    
    // Mocks to use for testing
    @Mock
    private Exception mockException;

    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        // Not required (yet)
    }

    /**
     * Verify that the message is properly applied
     */
    @Test
    public void testMessage() {
        ProcessingException ex = new ProcessingException("Message");
        Assertions.assertEquals("Message", ex.getMessage());
        Assertions.assertEquals(null, ex.getCause());
    }
    
    /**
     * Verify that the exception has the proper cause
     */
    @Test
    public void testCause() {
        ProcessingException ex = new ProcessingException("Another message", mockException);
        Assertions.assertEquals("Another message", ex.getMessage());
        Assertions.assertEquals(mockException, ex.getCause());
    }
}
