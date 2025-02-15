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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import tendril.test.AbstractUnitTest;

/**
 * Test case for the {@link ProcessingException}
 */
public class ProcessingExceptionTest extends AbstractUnitTest {

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
    }
}
