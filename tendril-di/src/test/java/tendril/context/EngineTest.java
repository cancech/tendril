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
package tendril.context;

import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import tendril.bean.recipe.Descriptor;
import tendril.processor.registration.RegistryFile;
import tendril.test.AbstractUnitTest;
import tendril.test.recipe.DoubleTestRecipe;
import tendril.test.recipe.IntTestRecipe;
import tendril.test.recipe.StringTestRecipe;

/**
 * Test case for the {@link Engine}
 */
public class EngineTest extends AbstractUnitTest {
    
    // Mocks to use for testing
    @Mock
    private Descriptor<Double> mockDoubleDescriptor;
    @Mock
    private Descriptor<Integer> mockIntDescriptor;
    @Mock
    private Descriptor<String> mockStringDescriptor;

    // Instance to test
    private Engine engine;

    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
    @Override
    protected void prepareTest() {
        engine = new Engine();
    }

    /**
     * Verify that the engine can be properly initialized
     */
    @Test
    public void testInit() {
        try (MockedStatic<RegistryFile> registry = Mockito.mockStatic(RegistryFile.class)) {
            registry.when(RegistryFile::read).thenReturn(new HashSet<>(Arrays.asList(DoubleTestRecipe.class.getName(), IntTestRecipe.class.getName(), 
                    StringTestRecipe.class.getName())));
            engine.init();
        }
        
        Assertions.assertEquals(3, engine.getBeanCount());
    }
    
    /**
     * Verify that beans can be retrieved
     */
    @Test
    public void testGetBean() {
        when(mockDoubleDescriptor.getBeanClass()).thenReturn(Double.class);
        when(mockIntDescriptor.getBeanClass()).thenReturn(Integer.class);
        when(mockStringDescriptor.getBeanClass()).thenReturn(String.class);
        
        testInit();

        Assertions.assertEquals(DoubleTestRecipe.VALUE, engine.getBean(mockDoubleDescriptor));
        Assertions.assertEquals(IntTestRecipe.VALUE, engine.getBean(mockIntDescriptor));
        Assertions.assertEquals(StringTestRecipe.VALUE, engine.getBean(mockStringDescriptor));
    }

}
