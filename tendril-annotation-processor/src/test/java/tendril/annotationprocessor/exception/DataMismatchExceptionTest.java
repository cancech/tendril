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

import tendril.codegen.field.type.Type;
import tendril.test.AbstractUnitTest;

/**
 * Test case for {@link DataMismatchException}
 */
public class DataMismatchExceptionTest extends AbstractUnitTest {

    // Mocks to use for testing
    @Mock
    private Type mockExpected;
    @Mock
    private Type mockReceived;

    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        // Not required
    }

    /**
     * Verify that the exception presents the appropriate reason/message
     */
    @Test
    public void testException() {
        Assertions.assertEquals("Invalid type, expected mockExpected but received mockReceived", new DataMismatchException(mockExpected, mockReceived).getMessage());
        Assertions.assertEquals("Invalid type, expected mockExpected but received SOMETHING", new DataMismatchException(mockExpected, "SOMETHING").getMessage());
        Assertions.assertEquals("A REASON", new DataMismatchException("A REASON").getMessage());
    }
}
