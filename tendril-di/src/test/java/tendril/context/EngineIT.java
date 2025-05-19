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

import tendril.BeanRetrievalException;
import tendril.bean.qualifier.Descriptor;
import tendril.processor.registration.RegistryFile;
import tendril.test.AbstractUnitTest;
import tendril.test.assertions.CollectionAssert;
import tendril.test.recipe.Double1DuplicateTestRecipe;
import tendril.test.recipe.Double1TestRecipe;
import tendril.test.recipe.Double2TestRecipe;
import tendril.test.recipe.IntTestRecipe;
import tendril.test.recipe.RequiresABRecipe;
import tendril.test.recipe.RequiresAConfigRecipe;
import tendril.test.recipe.RequiresARecipe;
import tendril.test.recipe.RequiresBNestedRecipe;
import tendril.test.recipe.RequiresBRecipe;
import tendril.test.recipe.StringTestRecipe;
import tendril.test.recipe.TestConfigRecipe;

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
        Assertions.assertThrows(BeanRetrievalException.class, () -> engine.getBean(new Descriptor<>(Double.class)));
        Assertions.assertThrows(BeanRetrievalException.class, () -> engine.getBean(new Descriptor<>(Double.class).setName(Double1TestRecipe.NAME)));
    }

    /**
     * Verify that the engine can be properly initialized
     */
    @Test
    public void testInitAllUnique() {
        try (MockedStatic<RegistryFile> registry = Mockito.mockStatic(RegistryFile.class)) {
            registry.when(RegistryFile::read)
                    .thenReturn(new HashSet<>(Arrays.asList(Double1TestRecipe.class.getName(), Double2TestRecipe.class.getName(), IntTestRecipe.class.getName(), StringTestRecipe.class.getName())));
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

        // Not available
        Assertions.assertThrows(BeanRetrievalException.class, () -> engine.getBean(new Descriptor<>(Long.class)));
        CollectionAssert.assertEmpty(engine.getAllBeans(new Descriptor<>(Long.class)));
        
        // Exact match
        Assertions.assertThrows(BeanRetrievalException.class, () -> engine.getBean(new Descriptor<>(Double.class)));
        Assertions.assertEquals(Double1TestRecipe.VALUE, engine.getBean(new Descriptor<>(Double.class).setName(Double1TestRecipe.NAME)));
        Assertions.assertEquals(Double2TestRecipe.VALUE, engine.getBean(new Descriptor<>(Double.class).setName(Double2TestRecipe.NAME)));
        Assertions.assertEquals(IntTestRecipe.VALUE, engine.getBean(new Descriptor<>(Integer.class)));
        Assertions.assertEquals(StringTestRecipe.VALUE, engine.getBean(new Descriptor<>(String.class)));

        CollectionAssert.assertEquivalent(engine.getAllBeans(new Descriptor<>(Double.class)), Double1TestRecipe.VALUE, Double2TestRecipe.VALUE);
        CollectionAssert.assertEquivalent(engine.getAllBeans(new Descriptor<>(Double.class).setName(Double1TestRecipe.NAME)), Double1TestRecipe.VALUE);
        CollectionAssert.assertEquivalent(engine.getAllBeans(new Descriptor<>(Double.class).setName(Double2TestRecipe.NAME)), Double2TestRecipe.VALUE);
        CollectionAssert.assertEquivalent(engine.getAllBeans(new Descriptor<>(Integer.class)), IntTestRecipe.VALUE);
        CollectionAssert.assertEquivalent(engine.getAllBeans(new Descriptor<>(String.class)), StringTestRecipe.VALUE);
        CollectionAssert.assertEquivalent(engine.getAllBeans(new Descriptor<>(Object.class)), Double1TestRecipe.VALUE, Double2TestRecipe.VALUE, IntTestRecipe.VALUE, StringTestRecipe.VALUE);
    }

    /**
     * Verify that beans can be retrieved from a configuration
     */
    @Test
    public void testConfiguration() {
        try (MockedStatic<RegistryFile> registry = Mockito.mockStatic(RegistryFile.class)) {
            registry.when(RegistryFile::read).thenReturn(new HashSet<>(Arrays.asList(TestConfigRecipe.class.getName())));
            engine.init();
        }

        Assertions.assertEquals(2, engine.getBeanCount());
        Assertions.assertEquals(Double1TestRecipe.VALUE, engine.getBean(new Descriptor<>(Double.class).setName(Double1TestRecipe.NAME)));
        Assertions.assertEquals(Double2TestRecipe.VALUE, engine.getBean(new Descriptor<>(Double.class).setName(Double2TestRecipe.NAME)));
        
        CollectionAssert.assertEmpty(engine.getAllBeans(new Descriptor<>(Long.class)));
        CollectionAssert.assertEquivalent(engine.getAllBeans(new Descriptor<>(Double.class)), Double1TestRecipe.VALUE, Double2TestRecipe.VALUE);
        CollectionAssert.assertEquivalent(engine.getAllBeans(new Descriptor<>(Object.class)), Double1TestRecipe.VALUE, Double2TestRecipe.VALUE);
        CollectionAssert.assertEquivalent(engine.getAllBeans(new Descriptor<>(Double.class).setName(Double1TestRecipe.NAME)), Double1TestRecipe.VALUE);
        CollectionAssert.assertEquivalent(engine.getAllBeans(new Descriptor<>(Double.class).setName(Double2TestRecipe.NAME)), Double2TestRecipe.VALUE);
    }

    /**
     * Verify that beans can be retrieved from a configuration as well as from beans directly
     */
    @Test
    public void testMixedConfiguration() {
        try (MockedStatic<RegistryFile> registry = Mockito.mockStatic(RegistryFile.class)) {
            registry.when(RegistryFile::read).thenReturn(new HashSet<>(Arrays.asList(TestConfigRecipe.class.getName(), IntTestRecipe.class.getName(), StringTestRecipe.class.getName())));
            engine.init();
        }

        Assertions.assertEquals(4, engine.getBeanCount());
        Assertions.assertEquals(Double1TestRecipe.VALUE, engine.getBean(new Descriptor<>(Double.class).setName(Double1TestRecipe.NAME)));
        Assertions.assertEquals(Double2TestRecipe.VALUE, engine.getBean(new Descriptor<>(Double.class).setName(Double2TestRecipe.NAME)));
        Assertions.assertEquals(IntTestRecipe.VALUE, engine.getBean(new Descriptor<>(Integer.class)));
        Assertions.assertEquals(StringTestRecipe.VALUE, engine.getBean(new Descriptor<>(String.class)));
        

        CollectionAssert.assertEmpty(engine.getAllBeans(new Descriptor<>(Long.class)));
        CollectionAssert.assertEquivalent(engine.getAllBeans(new Descriptor<>(Double.class)), Double1TestRecipe.VALUE, Double2TestRecipe.VALUE);
        CollectionAssert.assertEquivalent(engine.getAllBeans(new Descriptor<>(Double.class).setName(Double1TestRecipe.NAME)), Double1TestRecipe.VALUE);
        CollectionAssert.assertEquivalent(engine.getAllBeans(new Descriptor<>(Double.class).setName(Double2TestRecipe.NAME)), Double2TestRecipe.VALUE);
        CollectionAssert.assertEquivalent(engine.getAllBeans(new Descriptor<>(Integer.class)), IntTestRecipe.VALUE);
        CollectionAssert.assertEquivalent(engine.getAllBeans(new Descriptor<>(String.class)), StringTestRecipe.VALUE);
        CollectionAssert.assertEquivalent(engine.getAllBeans(new Descriptor<>(Object.class)), Double1TestRecipe.VALUE, Double2TestRecipe.VALUE, IntTestRecipe.VALUE, StringTestRecipe.VALUE);
    }

    /**
     * Verify that only beans meeting requirements are available
     */
    @Test
    public void testNoEnvironmentSpecified() {
        try (MockedStatic<RegistryFile> registry = Mockito.mockStatic(RegistryFile.class)) {
            registry.when(RegistryFile::read).thenReturn(new HashSet<>(Arrays.asList(Double1TestRecipe.class.getName(), RequiresARecipe.class.getName(), RequiresBRecipe.class.getName(), RequiresABRecipe.class.getName(),
                    RequiresAConfigRecipe.class.getName())));
            engine.init();
        }

        Assertions.assertEquals(1, engine.getBeanCount());
        Assertions.assertEquals(Double1TestRecipe.VALUE, engine.getBean(new Descriptor<>(Double.class).setName(Double1TestRecipe.NAME)));
        

        CollectionAssert.assertEmpty(engine.getAllBeans(new Descriptor<>(Long.class)));
        CollectionAssert.assertEquivalent(engine.getAllBeans(new Descriptor<>(Double.class)), Double1TestRecipe.VALUE);
    }

    /**
     * Verify that only beans meeting requirements are available
     */
    @Test
    public void testAEnvironmentSpecified() {
        engine.setEnvironments("A");
        try (MockedStatic<RegistryFile> registry = Mockito.mockStatic(RegistryFile.class)) {
            registry.when(RegistryFile::read).thenReturn(new HashSet<>(Arrays.asList(Double1TestRecipe.class.getName(), RequiresARecipe.class.getName(), RequiresBRecipe.class.getName(), RequiresABRecipe.class.getName(),
                    RequiresAConfigRecipe.class.getName())));
            engine.init();
        }

        Assertions.assertEquals(3, engine.getBeanCount());
        Assertions.assertEquals(Double1TestRecipe.VALUE, engine.getBean(new Descriptor<>(Double.class).setName(Double1TestRecipe.NAME)));
        Assertions.assertEquals(RequiresARecipe.VALUE, engine.getBean(new Descriptor<>(String.class).setName(RequiresARecipe.NAME)));
        Assertions.assertEquals(IntTestRecipe.VALUE, engine.getBean(new Descriptor<>(Integer.class).setName(IntTestRecipe.NAME)));
        

        CollectionAssert.assertEmpty(engine.getAllBeans(new Descriptor<>(Long.class)));
        CollectionAssert.assertEquivalent(engine.getAllBeans(new Descriptor<>(Double.class)), Double1TestRecipe.VALUE);
        CollectionAssert.assertEquivalent(engine.getAllBeans(new Descriptor<>(String.class)), RequiresARecipe.VALUE);
        CollectionAssert.assertEquivalent(engine.getAllBeans(new Descriptor<>(Integer.class)), IntTestRecipe.VALUE);
    }

    /**
     * Verify that only beans meeting requirements are available
     */
    @Test
    public void testBEnvironmentSpecified() {
        engine.setEnvironments("B");
        try (MockedStatic<RegistryFile> registry = Mockito.mockStatic(RegistryFile.class)) {
            registry.when(RegistryFile::read).thenReturn(new HashSet<>(Arrays.asList(Double1TestRecipe.class.getName(), RequiresARecipe.class.getName(), RequiresBRecipe.class.getName(), RequiresABRecipe.class.getName(),
                    RequiresAConfigRecipe.class.getName())));
            engine.init();
        }

        Assertions.assertEquals(2, engine.getBeanCount());
        Assertions.assertEquals(Double1TestRecipe.VALUE, engine.getBean(new Descriptor<>(Double.class).setName(Double1TestRecipe.NAME)));
        Assertions.assertEquals(RequiresBRecipe.VALUE, engine.getBean(new Descriptor<>(Double.class).setName(RequiresBRecipe.NAME)));
        

        CollectionAssert.assertEmpty(engine.getAllBeans(new Descriptor<>(Long.class)));
        CollectionAssert.assertEquivalent(engine.getAllBeans(new Descriptor<>(Double.class)), Double1TestRecipe.VALUE, RequiresBRecipe.VALUE);
        CollectionAssert.assertEmpty(engine.getAllBeans(new Descriptor<>(String.class)));
    }

    /**
     * Verify that only beans meeting requirements are available
     */
    @Test
    public void testABEnvironmentSpecified() {
        engine.setEnvironments("A", "B");
        try (MockedStatic<RegistryFile> registry = Mockito.mockStatic(RegistryFile.class)) {
            registry.when(RegistryFile::read).thenReturn(new HashSet<>(Arrays.asList(Double1TestRecipe.class.getName(), RequiresARecipe.class.getName(), RequiresBRecipe.class.getName(), RequiresABRecipe.class.getName(),
                    RequiresAConfigRecipe.class.getName())));
            engine.init();
        }

        Assertions.assertEquals(6, engine.getBeanCount());
        Assertions.assertEquals(Double1TestRecipe.VALUE, engine.getBean(new Descriptor<>(Double.class).setName(Double1TestRecipe.NAME)));
        Assertions.assertEquals(RequiresARecipe.VALUE, engine.getBean(new Descriptor<>(String.class).setName(RequiresARecipe.NAME)));
        Assertions.assertEquals(RequiresBRecipe.VALUE, engine.getBean(new Descriptor<>(Double.class).setName(RequiresBRecipe.NAME)));
        Assertions.assertEquals(RequiresABRecipe.VALUE, engine.getBean(new Descriptor<>(Integer.class).setName(RequiresABRecipe.NAME)));
        Assertions.assertEquals(IntTestRecipe.VALUE, engine.getBean(new Descriptor<>(Integer.class).setName(IntTestRecipe.NAME)));
        Assertions.assertEquals(RequiresBNestedRecipe.VALUE, engine.getBean(new Descriptor<>(Double.class).setName(RequiresBNestedRecipe.NAME)));
        

        CollectionAssert.assertEmpty(engine.getAllBeans(new Descriptor<>(Long.class)));
        CollectionAssert.assertEquivalent(engine.getAllBeans(new Descriptor<>(Double.class)), Double1TestRecipe.VALUE, RequiresBRecipe.VALUE, RequiresBNestedRecipe.VALUE);
        CollectionAssert.assertEquivalent(engine.getAllBeans(new Descriptor<>(String.class)), RequiresARecipe.VALUE);
        CollectionAssert.assertEquivalent(engine.getAllBeans(new Descriptor<>(Integer.class)), RequiresABRecipe.VALUE, IntTestRecipe.VALUE);
    }
    
    /**
     * Verify that initializing the engine prevents changing the specified environment
     */
    @Test
    public void testCannotChangeEnvironmentsAfterEngineInit() {
        testABEnvironmentSpecified();
        Assertions.assertThrows(RuntimeException.class, () -> engine.setEnvironments("qwerty"));
    }
}
