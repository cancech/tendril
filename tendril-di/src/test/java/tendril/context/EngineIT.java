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

import java.util.Arrays;
import java.util.HashSet;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import tendril.bean.recipe.Descriptor;
import tendril.processor.registration.RegistryFile;
import tendril.test.AbstractUnitTest;
import tendril.test.recipe.Double1DuplicateTestRecipe;
import tendril.test.recipe.Double1TestRecipe;
import tendril.test.recipe.Double2TestRecipe;
import tendril.test.recipe.IntTestRecipe;
import tendril.test.recipe.StringTestRecipe;

/**
 * Test case for the {@link Engine}
 */
public class EngineIT extends AbstractUnitTest {
    
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
     * Verify that the engine can be properly initialized where there are duplicate beans
     */
    @Test
    public void testInitDuplicateEntries() {
        try (MockedStatic<RegistryFile> registry = Mockito.mockStatic(RegistryFile.class)) {
            registry.when(RegistryFile::read).thenReturn(new HashSet<>(Arrays.asList(Double1TestRecipe.class.getName(), Double1DuplicateTestRecipe.class.getName())));
            engine.init();
        }
        
        Assertions.assertEquals(2, engine.getBeanCount());
        Assertions.assertThrows(IllegalArgumentException.class, () -> engine.getBean(new Descriptor<>(Double.class)));
        Assertions.assertThrows(IllegalArgumentException.class, () -> engine.getBean(new Descriptor<>(Double.class).setName(Double1TestRecipe.NAME)));
    }

    /**
     * Verify that the engine can be properly initialized
     */
    @Test
    public void testInitAllUnique() {
        try (MockedStatic<RegistryFile> registry = Mockito.mockStatic(RegistryFile.class)) {
            registry.when(RegistryFile::read).thenReturn(new HashSet<>(Arrays.asList(Double1TestRecipe.class.getName(), Double2TestRecipe.class.getName(),
                    IntTestRecipe.class.getName(), StringTestRecipe.class.getName())));
            engine.init();
        }
        
        Assertions.assertEquals(4, engine.getBeanCount());
    }
    
    /**
     * Verify that beans can be retrieved
     */
    @Test
    public void testGetBean() {
        testInitAllUnique();

        Assertions.assertThrows(IllegalArgumentException.class, () -> engine.getBean(new Descriptor<>(Double.class)));
        Assertions.assertEquals(Double1TestRecipe.VALUE, engine.getBean(new Descriptor<>(Double.class).setName(Double1TestRecipe.NAME)));
        Assertions.assertEquals(Double2TestRecipe.VALUE, engine.getBean(new Descriptor<>(Double.class).setName(Double2TestRecipe.NAME)));
        Assertions.assertEquals(IntTestRecipe.VALUE, engine.getBean(new Descriptor<>(Integer.class)));
        Assertions.assertEquals(StringTestRecipe.VALUE, engine.getBean(new Descriptor<>(String.class)));
    }

}
