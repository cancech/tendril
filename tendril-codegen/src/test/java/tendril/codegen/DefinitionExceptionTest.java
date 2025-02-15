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
package tendril.codegen;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.Type;
import tendril.test.AbstractUnitTest;

/**
 * Test case for {@link DefinitionException}
 */
public class DefinitionExceptionTest extends AbstractUnitTest {

    // Mocks to use for testing
    @Mock
    private Exception mockException;
    @Mock
    private ClassType mockClassType;
    @Mock
    private Type mockType;
    
    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        // Not required
    }

    /**
     * Simple message only
     */
    @Test
    public void testMessageOnly() {
        DefinitionException ex = new DefinitionException("message");
        Assertions.assertEquals("message", ex.getMessage());
        Assertions.assertNull(ex.getCause());

        ex = new DefinitionException("message2", mockException);
        Assertions.assertEquals("message2", ex.getMessage());
        Assertions.assertEquals(mockException, ex.getCause());
    }

    /**
     * Message derived from the class type
     */
    @Test
    public void testClassType() {
        when(mockClassType.getFullyQualifiedName()).thenReturn("FullyQualified");
        
        DefinitionException ex = new DefinitionException(mockClassType, "message");
        verify(mockClassType).getFullyQualifiedName();
        Assertions.assertEquals("FullyQualified - message", ex.getMessage());
        Assertions.assertNull(ex.getCause());

        ex = new DefinitionException(mockClassType, "message2");
        verify(mockClassType, times(2)).getFullyQualifiedName();
        Assertions.assertEquals("FullyQualified - message2", ex.getMessage());
        Assertions.assertNull(ex.getCause());
    }

    /**
     * Message derived from the type
     */
    @Test
    public void testType() {
        when(mockType.getSimpleName()).thenReturn("Simple");
        
        DefinitionException ex = new DefinitionException(mockType, "message");
        verify(mockType).getSimpleName();
        Assertions.assertEquals("Simple - message", ex.getMessage());
        Assertions.assertNull(ex.getCause());

        ex = new DefinitionException(mockType, "message2");
        verify(mockType, times(2)).getSimpleName();
        Assertions.assertEquals("Simple - message2", ex.getMessage());
        Assertions.assertNull(ex.getCause());
    }

    /**
     * Message derived from the class
     */
    @Test
    public void testClass() {
        DefinitionException ex = new DefinitionException(String.class, "message");
        Assertions.assertEquals("java.lang.String - message", ex.getMessage());
        Assertions.assertNull(ex.getCause());

        ex = new DefinitionException(String.class, "message2");
        Assertions.assertEquals("java.lang.String - message2", ex.getMessage());
        Assertions.assertNull(ex.getCause());
    }
}
