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

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.bean.recipe.Descriptor;
import tendril.test.AbstractUnitTest;

/**
 * Test case for {@link BeanCreationException}
 */
public class BeanCreationExceptionTest extends AbstractUnitTest {
    
    // Mocks to use for testing
    @Mock
    private Descriptor<?> mockDescriptor;
    @Mock
    private Exception mockCause;
    

    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        when(mockDescriptor.toString()).thenReturn("description");
    }

    /**
     * Verify that the exception properly presents the message
     */
    @Test
    public void testWithMessage() {
        BeanCreationException ex = new BeanCreationException(mockDescriptor, "Message");
        Assertions.assertEquals("Message [description]", ex.getMessage());
        Assertions.assertNull(ex.getCause());
    }

    /**
     * Verify that the exception properly presents the cause
     */
    @Test
    public void testWithCause() {
        BeanCreationException ex = new BeanCreationException(mockDescriptor, mockCause);
        Assertions.assertEquals("Failed to create bean [description]", ex.getMessage());
        Assertions.assertEquals(mockCause, ex.getCause());
    }
    
}
