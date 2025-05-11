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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.bean.qualifier.Descriptor;
import tendril.bean.recipe.AbstractRecipe;
import tendril.test.AbstractUnitTest;

/**
 * Test case for {@link BeanRetrievalException}
 */
public class BeanRetrievalExceptionTest extends AbstractUnitTest {
    
    // Mocks to use for testing
    @Mock
    private Descriptor<Object> mockBeanDescriptor;
    @Mock
    private AbstractRecipe<Object> mockRecipe1;
    @Mock
    private Descriptor<Object> mockRecipeDescriptor1;
    @Mock
    private AbstractRecipe<Object> mockRecipe2;
    @Mock
    private Descriptor<Object> mockRecipeDescriptor2;
    @Mock
    private AbstractRecipe<Object> mockRecipe3;
    @Mock
    private Descriptor<Object> mockRecipeDescriptor3;

    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        when(mockBeanDescriptor.toString()).thenReturn("BEAN");
    }

    /**
     * Verify the exception for when a bean was not found
     */
    @Test
    public void testBeanNotFound() {
        BeanRetrievalException ex = new BeanRetrievalException(mockBeanDescriptor);
        Assertions.assertEquals("No matching Bean found for BEAN", ex.getMessage());
    }

    /**
     * Verify the exception for when multiple beans were not found
     */
    @Test
    public void testMultipleBeansFound() {
        when(mockRecipe1.getDescription()).thenReturn(mockRecipeDescriptor1);
        when(mockRecipe2.getDescription()).thenReturn(mockRecipeDescriptor2);
        when(mockRecipe3.getDescription()).thenReturn(mockRecipeDescriptor3);
        when(mockRecipeDescriptor1.toString()).thenReturn("A");
        when(mockRecipeDescriptor2.toString()).thenReturn("B");
        when(mockRecipeDescriptor3.toString()).thenReturn("C");
        
        BeanRetrievalException ex = new BeanRetrievalException(mockBeanDescriptor, Arrays.asList(mockRecipe1, mockRecipe2, mockRecipe3));
        verify(mockRecipe1).getDescription();
        verify(mockRecipe2).getDescription();
        verify(mockRecipe3).getDescription();
        
        Assertions.assertEquals("Multiple matches available for BEAN:\n    - A\n    - B\n    - C", ex.getMessage());
    }
}
