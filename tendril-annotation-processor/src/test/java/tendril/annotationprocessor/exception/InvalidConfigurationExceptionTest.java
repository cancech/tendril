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
 * Test case for {@link InvalidConfigurationException}
 */
public class InvalidConfigurationExceptionTest extends AbstractUnitTest {

	// Mocks to use for testing
	@Mock
	private Exception mockException;
	
    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
	@Override
	protected void prepareTest() {
		// nothing to be done
	}
	
    /**
     * Verify that the message is properly stored
     */
    @Test
    public void testMessage() {
        Assertions.assertEquals("abc123", new InvalidConfigurationException("abc123").getMessage());
        Assertions.assertEquals("123abc", new InvalidConfigurationException("123abc").getMessage());
        Assertions.assertEquals("qwerty", new InvalidConfigurationException("qwerty").getMessage());
    }
    
    /**
     * Verify that the message and cause are properly stored
     */
    @Test
    public void testCause() {
    	InvalidConfigurationException ex = new InvalidConfigurationException("kfjfor123", mockException);
    	Assertions.assertEquals("kfjfor123", ex.getMessage());
    	Assertions.assertEquals(mockException, ex.getCause());
    }
}
