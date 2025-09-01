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
package tendril;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.test.AbstractUnitTest;

/**
 * Test case for {@link TendrilStartupException}
 */
public class TendrilStartupExceptionTest extends AbstractUnitTest{

    @Mock
    private Exception mockCause; 
    
    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        // Not required
    }

    /**
     * Verify that the message is properly stored
     */
    @Test
    public void testMessage() {
        testMessageException("abc");
        testMessageException("123");
        testMessageException("abc123");
    }
    
    /**
     * Helper for testing exceptions that only include a message
     * @param msg
     */
    private void testMessageException(String msg) {
        TendrilStartupException ex = new TendrilStartupException(msg);
        Assertions.assertEquals(msg, ex.getMessage());
        Assertions.assertNull(ex.getCause());
    }
    
    @Test
    public void testCause() {
        TendrilStartupException ex = new TendrilStartupException(mockCause);
        Assertions.assertEquals("mockCause", ex.getMessage());
        Assertions.assertEquals(mockCause, ex.getCause());
    }
}
