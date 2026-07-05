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
import java.util.Collections;
import java.util.HashSet;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import tendril.BeanReplacementException;
import tendril.BeanRetrievalException;
import tendril.bean.duplicate.Blueprint;
import tendril.bean.qualifier.Descriptor;
import tendril.processor.registration.RegistryFile;
import tendril.processor.registration.ReplacementRegistryFile;
import tendril.test.AbstractUnitTest;
import tendril.test.assertions.CollectionAssert;
import tendril.test.recipe.BasicStringRecipe1;
import tendril.test.recipe.BasicStringRecipe2;
import tendril.test.recipe.BasicStringRecipe3;
import tendril.test.recipe.Double1DuplicateTestRecipe;
import tendril.test.recipe.Double1TestRecipe;
import tendril.test.recipe.Double2TestRecipe;
import tendril.test.recipe.FallbackStringRecipe1;
import tendril.test.recipe.FallbackStringRecipe2;
import tendril.test.recipe.FallbackStringRecipe3;
import tendril.test.recipe.IntTestRecipe;
import tendril.test.recipe.PrimaryStringRecipe1;
import tendril.test.recipe.PrimaryStringRecipe2;
import tendril.test.recipe.PrimaryStringRecipe3;
import tendril.test.recipe.ReplaceIntRecipe;
import tendril.test.recipe.ReplaceStringRecipe;
import tendril.test.recipe.RequiresEnvABRecipe;
import tendril.test.recipe.RequiresEnvAConfigRecipe;
import tendril.test.recipe.RequiresEnvANotBRecipe;
import tendril.test.recipe.RequiresEnvARecipe;
import tendril.test.recipe.RequiresEnvAorBRecipe;
import tendril.test.recipe.RequiresEnvBNestedRecipe;
import tendril.test.recipe.RequiresEnvBNotARecipe;
import tendril.test.recipe.RequiresEnvBRecipe;
import tendril.test.recipe.RequiresEnvNotARecipe;
import tendril.test.recipe.RequiresEnvNotBRecipe;
import tendril.test.recipe.RequiresPropABRecipe;
import tendril.test.recipe.RequiresPropAConfigRecipe;
import tendril.test.recipe.RequiresPropANotBRecipe;
import tendril.test.recipe.RequiresPropARecipe;
import tendril.test.recipe.RequiresPropAorBRecipe;
import tendril.test.recipe.RequiresPropBNestedRecipe;
import tendril.test.recipe.RequiresPropBNotARecipe;
import tendril.test.recipe.RequiresPropBRecipe;
import tendril.test.recipe.RequiresPropNotARecipe;
import tendril.test.recipe.RequiresPropNotBRecipe;
import tendril.test.recipe.StringTestRecipe;
import tendril.test.recipe.TestConfigRecipe;

/**
 * Test case for the {@link Engine}
 */
public class EngineIT extends AbstractUnitTest {

	// Blueprint Drivers to use for testing
	private interface Level1 extends Blueprint {
	}

	private interface Level2 extends Level1 {
	}

	private interface Level3 extends Level2 {
	}

	private interface SeparateLevel1 extends Blueprint {
	}

	private interface SeparateLevel2 extends SeparateLevel1 {
	}

	// Mocks to use for testing
	@Mock
	private Level1 mockLevel11Driver;
	@Mock
	private Level1 mockLevel12Driver;
	@Mock
	private Level2 mockLevel21Driver;
	@Mock
	private Level2 mockLevel22Driver;
	@Mock
	private Level3 mockLevel31Driver;
	@Mock
	private Level3 mockLevel32Driver;
	@Mock
	private SeparateLevel1 mockSeparateLevel11Driver;
	@Mock
	private SeparateLevel1 mockSeparateLevel12Driver;
	@Mock
	private SeparateLevel2 mockSeparateLevel21Driver;
	@Mock
	private SeparateLevel2 mockSeparateLevel22Driver;

	// Instance to test
	private Engine engine;

	/**
	 * @see tendril.test.AbstractUnitTest#prepareTest()
	 */
	@Override
	protected void prepareTest() {
		engine = new Engine();
	}

	@Override
	protected void cleanupTest() {
		clearSystemProperty("A");
		clearSystemProperty("B");
	}

	private void clearSystemProperty(String prop) {
		try {
			System.clearProperty(prop);
		} catch (NullPointerException ex) {
			// Do nothing - just means that the property wasn't set
		}
	}

	/**
	 * Verify that the engine can be properly initialized when there are no beans
	 */
	@Test
	public void testNoBeans() {
		try (MockedStatic<RegistryFile> registry = Mockito.mockStatic(RegistryFile.class)) {
			registry.when(RegistryFile::read).thenReturn(new HashSet<>());
			engine.init();
		}

		// Check what is present
		Assertions.assertEquals(1, engine.getBeanCount());
		assertBeans(new Descriptor<>(Double.class));
		assertBeans(new Descriptor<>(Integer.class));
		assertBeans(new Descriptor<>(String.class));
		assertBeans(new Descriptor<>(ApplicationContext.class), engine);
	}

	/**
	 * Verify that the engine can be properly initialized when only external/manual beans are employed
	 */
	@Test
	public void testManualBeansOnly() {
		testNoBeans();
		
		// Add one bean
		engine.registerBean(123, new Descriptor<>(Integer.class));
		Assertions.assertEquals(2, engine.getBeanCount());
		assertBeans(new Descriptor<>(Double.class));
		assertBeans(new Descriptor<>(Integer.class), 123);
		assertBeans(new Descriptor<>(String.class));
		assertBeans(new Descriptor<>(ApplicationContext.class), engine);
		
		// Add some more beans
		engine.registerBean(123, new Descriptor<>(Integer.class));
		engine.registerBean(321, new Descriptor<>(Integer.class));
		engine.registerBean(234, new Descriptor<>(Integer.class));
		engine.registerBean("abc123", new Descriptor<>(String.class));
		Assertions.assertEquals(6, engine.getBeanCount());
		assertBeans(new Descriptor<>(Double.class));
		assertBeans(new Descriptor<>(Integer.class), 123, 123, 321, 234);
		assertBeans(new Descriptor<>(String.class), "abc123");
		assertBeans(new Descriptor<>(ApplicationContext.class), engine);
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

		// Check what is present
		Assertions.assertEquals(3, engine.getBeanCount());
		assertBeans(new Descriptor<>(Double.class), Double1TestRecipe.VALUE, Double1DuplicateTestRecipe.VALUE);
		assertBeans(new Descriptor<>(Double.class).setName(Double1TestRecipe.NAME), Double1TestRecipe.VALUE, Double1DuplicateTestRecipe.VALUE);
		assertBeans(new Descriptor<>(Double.class).setName(Double2TestRecipe.NAME));
		assertBeans(new Descriptor<>(Integer.class));
		assertBeans(new Descriptor<>(String.class));
		assertBeans(new Descriptor<>(ApplicationContext.class), engine);
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

		Assertions.assertEquals(5, engine.getBeanCount());
		assertBeans(new Descriptor<>(Double.class), Double1TestRecipe.VALUE, Double2TestRecipe.VALUE);
		assertBeans(new Descriptor<>(Double.class).setName(Double1TestRecipe.NAME), Double1TestRecipe.VALUE);
		assertBeans(new Descriptor<>(Double.class).setName(Double2TestRecipe.NAME), Double2TestRecipe.VALUE);
		assertBeans(new Descriptor<>(Long.class));
		assertBeans(new Descriptor<>(Integer.class), IntTestRecipe.VALUE);
		assertBeans(new Descriptor<>(String.class), StringTestRecipe.VALUE);
		assertBeans(new Descriptor<>(ApplicationContext.class), engine);
		assertBeans(new Descriptor<>(Object.class), Double1TestRecipe.VALUE, Double2TestRecipe.VALUE, IntTestRecipe.VALUE, StringTestRecipe.VALUE, engine);
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

		Assertions.assertEquals(3, engine.getBeanCount());
		assertBeans(new Descriptor<>(Double.class), Double1TestRecipe.VALUE, Double2TestRecipe.VALUE);
		assertBeans(new Descriptor<>(Double.class).setName(Double1TestRecipe.NAME), Double1TestRecipe.VALUE);
		assertBeans(new Descriptor<>(Double.class).setName(Double2TestRecipe.NAME), Double2TestRecipe.VALUE);
		assertBeans(new Descriptor<>(Long.class));
		assertBeans(new Descriptor<>(Integer.class));
		assertBeans(new Descriptor<>(String.class));
		assertBeans(new Descriptor<>(ApplicationContext.class), engine);
		assertBeans(new Descriptor<>(Object.class), Double1TestRecipe.VALUE, Double2TestRecipe.VALUE, engine);
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

		Assertions.assertEquals(5, engine.getBeanCount());
		assertBeans(new Descriptor<>(Double.class), Double1TestRecipe.VALUE, Double2TestRecipe.VALUE);
		assertBeans(new Descriptor<>(Double.class).setName(Double1TestRecipe.NAME), Double1TestRecipe.VALUE);
		assertBeans(new Descriptor<>(Double.class).setName(Double2TestRecipe.NAME), Double2TestRecipe.VALUE);
		assertBeans(new Descriptor<>(Integer.class), IntTestRecipe.VALUE);
		assertBeans(new Descriptor<>(String.class), StringTestRecipe.VALUE);
		assertBeans(new Descriptor<>(Long.class));
		assertBeans(new Descriptor<>(ApplicationContext.class), engine);
		assertBeans(new Descriptor<>(Object.class), Double1TestRecipe.VALUE, Double2TestRecipe.VALUE, IntTestRecipe.VALUE, StringTestRecipe.VALUE, engine);
	}

	/**
	 * Verify that the engine can be properly initialized when only external/manual beans are employed
	 */
	@Test
	public void testMixedConfigurationManualBeans() {
		testMixedConfiguration();
		
		// Add one bean
		engine.registerBean(123, new Descriptor<>(Integer.class));
		Assertions.assertEquals(6, engine.getBeanCount());
		assertBeans(new Descriptor<>(Double.class), Double1TestRecipe.VALUE, Double2TestRecipe.VALUE);
		assertBeans(new Descriptor<>(Double.class).setName(Double1TestRecipe.NAME), Double1TestRecipe.VALUE);
		assertBeans(new Descriptor<>(Double.class).setName(Double2TestRecipe.NAME), Double2TestRecipe.VALUE);
		assertBeans(new Descriptor<>(Integer.class), IntTestRecipe.VALUE, 123);
		assertBeans(new Descriptor<>(String.class), StringTestRecipe.VALUE);
		assertBeans(new Descriptor<>(Long.class));
		assertBeans(new Descriptor<>(ApplicationContext.class), engine);
		assertBeans(new Descriptor<>(Object.class), Double1TestRecipe.VALUE, Double2TestRecipe.VALUE, IntTestRecipe.VALUE, StringTestRecipe.VALUE, 123, engine);
		
		// Add some more beans
		engine.registerBean(123, new Descriptor<>(Integer.class));
		engine.registerBean(321, new Descriptor<>(Integer.class));
		engine.registerBean(234, new Descriptor<>(Integer.class));
		engine.registerBean("abc123", new Descriptor<>(String.class));
		Assertions.assertEquals(10, engine.getBeanCount());
		assertBeans(new Descriptor<>(Double.class), Double1TestRecipe.VALUE, Double2TestRecipe.VALUE);
		assertBeans(new Descriptor<>(Double.class).setName(Double1TestRecipe.NAME), Double1TestRecipe.VALUE);
		assertBeans(new Descriptor<>(Double.class).setName(Double2TestRecipe.NAME), Double2TestRecipe.VALUE);
		assertBeans(new Descriptor<>(Integer.class), IntTestRecipe.VALUE, 123, 123, 321, 234);
		assertBeans(new Descriptor<>(String.class), StringTestRecipe.VALUE, "abc123");
		assertBeans(new Descriptor<>(Long.class));
		assertBeans(new Descriptor<>(ApplicationContext.class), engine);
		assertBeans(new Descriptor<>(Object.class), Double1TestRecipe.VALUE, Double2TestRecipe.VALUE, IntTestRecipe.VALUE, StringTestRecipe.VALUE, 123, 123, 321, 234, "abc123", engine);
	}

	/**
	 * Verify that only beans meeting requirements are available
	 */
	@Test
	public void testNoEnvironmentSpecified() {
		try (MockedStatic<RegistryFile> registry = Mockito.mockStatic(RegistryFile.class)) {
			registry.when(RegistryFile::read).thenReturn(new HashSet<>(Arrays.asList(Double1TestRecipe.class.getName(), RequiresEnvARecipe.class.getName(), RequiresEnvBRecipe.class.getName(),
					RequiresEnvABRecipe.class.getName(), RequiresEnvAConfigRecipe.class.getName(), RequiresEnvAorBRecipe.class.getName())));
			engine.init();
		}

		Assertions.assertEquals(2, engine.getBeanCount());
		assertBeans(new Descriptor<>(Double.class), Double1TestRecipe.VALUE);
		assertBeans(new Descriptor<>(Double.class).setName(Double1TestRecipe.NAME), Double1TestRecipe.VALUE);
		assertBeans(new Descriptor<>(Double.class).setName(Double2TestRecipe.NAME));
		assertBeans(new Descriptor<>(Integer.class));
		assertBeans(new Descriptor<>(String.class));
		assertBeans(new Descriptor<>(Long.class));
		assertBeans(new Descriptor<>(ApplicationContext.class), engine);
		assertBeans(new Descriptor<>(Object.class), Double1TestRecipe.VALUE, engine);
	}

	/**
	 * Verify that only beans meeting requirements are available
	 */
	@Test
	public void testAEnvironmentSpecified() {
		engine.addEnvironments("A");
		try (MockedStatic<RegistryFile> registry = Mockito.mockStatic(RegistryFile.class)) {
			registry.when(RegistryFile::read)
					.thenReturn(new HashSet<>(Arrays.asList(Double1TestRecipe.class.getName(), RequiresEnvARecipe.class.getName(), RequiresEnvBRecipe.class.getName(),
							RequiresEnvABRecipe.class.getName(), RequiresEnvAConfigRecipe.class.getName(), RequiresEnvANotBRecipe.class.getName(), RequiresEnvBNotARecipe.class.getName(),
							RequiresEnvNotARecipe.class.getName(), RequiresEnvNotBRecipe.class.getName(), RequiresEnvAorBRecipe.class.getName())));
			engine.init();
		}

		Assertions.assertEquals(7, engine.getBeanCount());
		assertBeans(new Descriptor<>(Double.class), Double1TestRecipe.VALUE);
		assertBeans(new Descriptor<>(Double.class).setName(Double1TestRecipe.NAME), Double1TestRecipe.VALUE);
		assertBeans(new Descriptor<>(Double.class).setName(Double2TestRecipe.NAME));
		assertBeans(new Descriptor<>(Integer.class), IntTestRecipe.VALUE, RequiresEnvAorBRecipe.VALUE);
		assertBeans(new Descriptor<>(Integer.class).setName(IntTestRecipe.NAME), IntTestRecipe.VALUE);
		assertBeans(new Descriptor<>(Integer.class).setName(RequiresEnvAorBRecipe.NAME), RequiresEnvAorBRecipe.VALUE);
		assertBeans(new Descriptor<>(String.class), RequiresEnvARecipe.VALUE, RequiresEnvANotBRecipe.VALUE, RequiresEnvNotBRecipe.VALUE);
		assertBeans(new Descriptor<>(String.class).setName(RequiresEnvARecipe.NAME), RequiresEnvARecipe.VALUE);
		assertBeans(new Descriptor<>(String.class).setName(RequiresEnvANotBRecipe.NAME), RequiresEnvANotBRecipe.VALUE);
		assertBeans(new Descriptor<>(String.class).setName(RequiresEnvNotBRecipe.NAME), RequiresEnvNotBRecipe.VALUE);
		assertBeans(new Descriptor<>(Long.class));
		assertBeans(new Descriptor<>(ApplicationContext.class), engine);
		assertBeans(new Descriptor<>(Object.class), Double1TestRecipe.VALUE, IntTestRecipe.VALUE, RequiresEnvAorBRecipe.VALUE, RequiresEnvARecipe.VALUE, RequiresEnvANotBRecipe.VALUE,
				RequiresEnvNotBRecipe.VALUE, engine);
	}

	/**
	 * Verify that only beans meeting requirements are available
	 */
	@Test
	public void testBEnvironmentSpecified() {
		engine.addEnvironments("B");
		try (MockedStatic<RegistryFile> registry = Mockito.mockStatic(RegistryFile.class)) {
			registry.when(RegistryFile::read)
					.thenReturn(new HashSet<>(Arrays.asList(Double1TestRecipe.class.getName(), RequiresEnvARecipe.class.getName(), RequiresEnvBRecipe.class.getName(),
							RequiresEnvABRecipe.class.getName(), RequiresEnvAConfigRecipe.class.getName(), RequiresEnvANotBRecipe.class.getName(), RequiresEnvBNotARecipe.class.getName(),
							RequiresEnvNotARecipe.class.getName(), RequiresEnvNotBRecipe.class.getName(), RequiresEnvAorBRecipe.class.getName())));
			engine.init();
		}

		Assertions.assertEquals(6, engine.getBeanCount());
		assertBeans(new Descriptor<>(Double.class), Double1TestRecipe.VALUE, RequiresEnvBRecipe.VALUE);
		assertBeans(new Descriptor<>(Double.class).setName(Double1TestRecipe.NAME), Double1TestRecipe.VALUE);
		assertBeans(new Descriptor<>(Double.class).setName(RequiresEnvBRecipe.NAME), RequiresEnvBRecipe.VALUE);
		assertBeans(new Descriptor<>(Integer.class), RequiresEnvAorBRecipe.VALUE);
		assertBeans(new Descriptor<>(Integer.class).setName(RequiresEnvAorBRecipe.NAME), RequiresEnvAorBRecipe.VALUE);
		assertBeans(new Descriptor<>(String.class), RequiresEnvBNotARecipe.VALUE, RequiresEnvNotARecipe.VALUE);
		assertBeans(new Descriptor<>(String.class).setName(RequiresEnvBNotARecipe.NAME), RequiresEnvBNotARecipe.VALUE);
		assertBeans(new Descriptor<>(String.class).setName(RequiresEnvNotARecipe.NAME), RequiresEnvNotARecipe.VALUE);
		assertBeans(new Descriptor<>(Long.class));
		assertBeans(new Descriptor<>(ApplicationContext.class), engine);
		assertBeans(new Descriptor<>(Object.class), Double1TestRecipe.VALUE, RequiresEnvBRecipe.VALUE, RequiresEnvAorBRecipe.VALUE, RequiresEnvBNotARecipe.VALUE, RequiresEnvNotARecipe.VALUE, engine);
	}

	/**
	 * Verify that only beans meeting requirements are available
	 */
	@Test
	public void testABEnvironmentSpecified() {
		engine.addEnvironments("A", "B");
		try (MockedStatic<RegistryFile> registry = Mockito.mockStatic(RegistryFile.class)) {
			registry.when(RegistryFile::read)
					.thenReturn(new HashSet<>(Arrays.asList(Double1TestRecipe.class.getName(), RequiresEnvARecipe.class.getName(), RequiresEnvBRecipe.class.getName(),
							RequiresEnvABRecipe.class.getName(), RequiresEnvAConfigRecipe.class.getName(), RequiresEnvANotBRecipe.class.getName(), RequiresEnvBNotARecipe.class.getName(),
							RequiresEnvNotARecipe.class.getName(), RequiresEnvNotBRecipe.class.getName(), RequiresEnvAorBRecipe.class.getName())));
			engine.init();
		}

		Assertions.assertEquals(8, engine.getBeanCount());
		assertBeans(new Descriptor<>(Double.class), Double1TestRecipe.VALUE, RequiresEnvBRecipe.VALUE, RequiresEnvBNestedRecipe.VALUE);
		assertBeans(new Descriptor<>(Double.class).setName(Double1TestRecipe.NAME), Double1TestRecipe.VALUE);
		assertBeans(new Descriptor<>(Double.class).setName(RequiresEnvBRecipe.NAME), RequiresEnvBRecipe.VALUE);
		assertBeans(new Descriptor<>(Double.class).setName(RequiresEnvBNestedRecipe.NAME), RequiresEnvBNestedRecipe.VALUE);
		assertBeans(new Descriptor<>(Integer.class), RequiresEnvABRecipe.VALUE, IntTestRecipe.VALUE, RequiresEnvAorBRecipe.VALUE);
		assertBeans(new Descriptor<>(Integer.class).setName(RequiresEnvABRecipe.NAME), RequiresEnvABRecipe.VALUE);
		assertBeans(new Descriptor<>(Integer.class).setName(IntTestRecipe.NAME), IntTestRecipe.VALUE);
		assertBeans(new Descriptor<>(Integer.class).setName(RequiresEnvAorBRecipe.NAME), RequiresEnvAorBRecipe.VALUE);
		assertBeans(new Descriptor<>(String.class), RequiresEnvARecipe.VALUE);
		assertBeans(new Descriptor<>(String.class).setName(RequiresEnvARecipe.NAME), RequiresEnvARecipe.VALUE);
		assertBeans(new Descriptor<>(Long.class));
		assertBeans(new Descriptor<>(ApplicationContext.class), engine);
		assertBeans(new Descriptor<>(Object.class), Double1TestRecipe.VALUE, RequiresEnvBRecipe.VALUE, RequiresEnvBNestedRecipe.VALUE, RequiresEnvABRecipe.VALUE, IntTestRecipe.VALUE,
				RequiresEnvARecipe.VALUE, RequiresEnvAorBRecipe.VALUE, engine);
	}

	/**
	 * Verify that only beans meeting requirements are available
	 */
	@Test
	public void testNoPropertySpecified() {
		try (MockedStatic<RegistryFile> registry = Mockito.mockStatic(RegistryFile.class)) {
			registry.when(RegistryFile::read).thenReturn(new HashSet<>(Arrays.asList(Double1TestRecipe.class.getName(), RequiresPropARecipe.class.getName(), RequiresPropBRecipe.class.getName(),
					RequiresPropABRecipe.class.getName(), RequiresPropAConfigRecipe.class.getName(), RequiresPropAorBRecipe.class.getName())));
			engine.init();
		}

		Assertions.assertEquals(2, engine.getBeanCount());
		assertBeans(new Descriptor<>(Double.class), Double1TestRecipe.VALUE);
		assertBeans(new Descriptor<>(Double.class).setName(Double1TestRecipe.NAME), Double1TestRecipe.VALUE);
		assertBeans(new Descriptor<>(Integer.class));
		assertBeans(new Descriptor<>(String.class));
		assertBeans(new Descriptor<>(Long.class));
		assertBeans(new Descriptor<>(ApplicationContext.class), engine);
		assertBeans(new Descriptor<>(Object.class), Double1TestRecipe.VALUE, engine);
	}

	/**
	 * Verify that only beans meeting requirements are available
	 */
	@Test
	public void testAPropertySpecified() {
		System.setProperty("A", "");
		try (MockedStatic<RegistryFile> registry = Mockito.mockStatic(RegistryFile.class)) {
			registry.when(RegistryFile::read)
					.thenReturn(new HashSet<>(Arrays.asList(Double1TestRecipe.class.getName(), RequiresPropARecipe.class.getName(), RequiresPropBRecipe.class.getName(),
							RequiresPropABRecipe.class.getName(), RequiresPropAConfigRecipe.class.getName(), RequiresPropANotBRecipe.class.getName(), RequiresPropBNotARecipe.class.getName(),
							RequiresPropNotARecipe.class.getName(), RequiresPropNotBRecipe.class.getName(), RequiresPropAorBRecipe.class.getName())));
			engine.init();
		}

		Assertions.assertEquals(7, engine.getBeanCount());
		assertBeans(new Descriptor<>(Double.class), Double1TestRecipe.VALUE);
		assertBeans(new Descriptor<>(Double.class).setName(Double1TestRecipe.NAME), Double1TestRecipe.VALUE);
		assertBeans(new Descriptor<>(Integer.class), IntTestRecipe.VALUE, RequiresPropAorBRecipe.VALUE);
		assertBeans(new Descriptor<>(Integer.class).setName(IntTestRecipe.NAME), IntTestRecipe.VALUE);
		assertBeans(new Descriptor<>(Integer.class).setName(RequiresPropAorBRecipe.NAME), RequiresPropAorBRecipe.VALUE);
		assertBeans(new Descriptor<>(String.class), RequiresPropARecipe.VALUE, RequiresPropANotBRecipe.VALUE, RequiresPropNotBRecipe.VALUE);
		assertBeans(new Descriptor<>(String.class).setName(RequiresPropARecipe.NAME), RequiresPropARecipe.VALUE);
		assertBeans(new Descriptor<>(String.class).setName(RequiresPropANotBRecipe.NAME), RequiresPropANotBRecipe.VALUE);
		assertBeans(new Descriptor<>(String.class).setName(RequiresPropNotBRecipe.NAME), RequiresPropNotBRecipe.VALUE);
		assertBeans(new Descriptor<>(Long.class));
		assertBeans(new Descriptor<>(ApplicationContext.class), engine);
		assertBeans(new Descriptor<>(Object.class), Double1TestRecipe.VALUE, IntTestRecipe.VALUE, RequiresPropAorBRecipe.VALUE, RequiresPropARecipe.VALUE, RequiresPropANotBRecipe.VALUE,
				RequiresPropNotBRecipe.VALUE, engine);
	}

	/**
	 * Verify that only beans meeting requirements are available
	 */
	@Test
	public void testBPropertySpecified() {
		System.setProperty("B", "");
		try (MockedStatic<RegistryFile> registry = Mockito.mockStatic(RegistryFile.class)) {
			registry.when(RegistryFile::read)
					.thenReturn(new HashSet<>(Arrays.asList(Double1TestRecipe.class.getName(), RequiresPropARecipe.class.getName(), RequiresPropBRecipe.class.getName(),
							RequiresPropABRecipe.class.getName(), RequiresPropAConfigRecipe.class.getName(), RequiresPropANotBRecipe.class.getName(), RequiresPropBNotARecipe.class.getName(),
							RequiresPropNotARecipe.class.getName(), RequiresPropNotBRecipe.class.getName(), RequiresPropAorBRecipe.class.getName())));
			engine.init();
		}

		Assertions.assertEquals(6, engine.getBeanCount());
		assertBeans(new Descriptor<>(Double.class), Double1TestRecipe.VALUE, RequiresPropBRecipe.VALUE);
		assertBeans(new Descriptor<>(Double.class).setName(Double1TestRecipe.NAME), Double1TestRecipe.VALUE);
		assertBeans(new Descriptor<>(Double.class).setName(RequiresPropBRecipe.NAME), RequiresPropBRecipe.VALUE);
		assertBeans(new Descriptor<>(Integer.class), RequiresPropAorBRecipe.VALUE);
		assertBeans(new Descriptor<>(Integer.class).setName(RequiresPropAorBRecipe.NAME), RequiresPropAorBRecipe.VALUE);
		assertBeans(new Descriptor<>(String.class), RequiresPropBNotARecipe.VALUE, RequiresPropNotARecipe.VALUE);
		assertBeans(new Descriptor<>(String.class).setName(RequiresPropBNotARecipe.NAME), RequiresPropBNotARecipe.VALUE);
		assertBeans(new Descriptor<>(String.class).setName(RequiresPropNotARecipe.NAME), RequiresPropNotARecipe.VALUE);
		assertBeans(new Descriptor<>(Long.class));
		assertBeans(new Descriptor<>(ApplicationContext.class), engine);
		assertBeans(new Descriptor<>(Object.class), Double1TestRecipe.VALUE, RequiresPropBRecipe.VALUE, RequiresPropAorBRecipe.VALUE, RequiresPropBNotARecipe.VALUE, RequiresPropNotARecipe.VALUE, engine);
	}

	/**
	 * Verify that only beans meeting requirements are available
	 */
	@Test
	public void testABPropertySpecified() {
		System.setProperty("A", "");
		System.setProperty("B", "");
		try (MockedStatic<RegistryFile> registry = Mockito.mockStatic(RegistryFile.class)) {
			registry.when(RegistryFile::read)
					.thenReturn(new HashSet<>(Arrays.asList(Double1TestRecipe.class.getName(), RequiresPropARecipe.class.getName(), RequiresPropBRecipe.class.getName(),
							RequiresPropABRecipe.class.getName(), RequiresPropAConfigRecipe.class.getName(), RequiresPropANotBRecipe.class.getName(), RequiresPropBNotARecipe.class.getName(),
							RequiresPropNotARecipe.class.getName(), RequiresPropNotBRecipe.class.getName(), RequiresPropAorBRecipe.class.getName())));
			engine.init();
		}

		Assertions.assertEquals(8, engine.getBeanCount());
		assertBeans(new Descriptor<>(Double.class), Double1TestRecipe.VALUE, RequiresPropBRecipe.VALUE, RequiresPropBNestedRecipe.VALUE);
		assertBeans(new Descriptor<>(Double.class).setName(Double1TestRecipe.NAME), Double1TestRecipe.VALUE);
		assertBeans(new Descriptor<>(Double.class).setName(RequiresPropBRecipe.NAME), RequiresPropBRecipe.VALUE);
		assertBeans(new Descriptor<>(Double.class).setName(RequiresPropBNestedRecipe.NAME), RequiresPropBNestedRecipe.VALUE);
		assertBeans(new Descriptor<>(Integer.class), RequiresPropABRecipe.VALUE, IntTestRecipe.VALUE, RequiresPropAorBRecipe.VALUE);
		assertBeans(new Descriptor<>(Integer.class).setName(RequiresPropABRecipe.NAME), RequiresPropABRecipe.VALUE);
		assertBeans(new Descriptor<>(Integer.class).setName(IntTestRecipe.NAME), IntTestRecipe.VALUE);
		assertBeans(new Descriptor<>(Integer.class).setName(RequiresPropAorBRecipe.NAME), RequiresPropAorBRecipe.VALUE);
		assertBeans(new Descriptor<>(String.class), RequiresPropARecipe.VALUE);
		assertBeans(new Descriptor<>(String.class).setName(RequiresPropARecipe.NAME), RequiresPropARecipe.VALUE);
		assertBeans(new Descriptor<>(Long.class));
		assertBeans(new Descriptor<>(ApplicationContext.class), engine);
		assertBeans(new Descriptor<>(Object.class), Double1TestRecipe.VALUE, RequiresPropBRecipe.VALUE, RequiresPropBNestedRecipe.VALUE, RequiresPropABRecipe.VALUE, IntTestRecipe.VALUE,
				RequiresPropAorBRecipe.VALUE, RequiresPropARecipe.VALUE, engine);
	}

	/**
	 * Verify that initializing the engine prevents changing the specified environment
	 */
	@Test
	public void testCannotChangeEnvironmentsAfterEngineInit() {
		testABEnvironmentSpecified();
		Assertions.assertThrows(RuntimeException.class, () -> engine.addEnvironments("qwerty"));
	}

	/**
	 * Verify that beans can properly be retrieved when there is are multiple primary, basic, and fallback beans.
	 */
	@Test
	public void testMultiplePrimaryBeans() {
		try (MockedStatic<RegistryFile> registry = Mockito.mockStatic(RegistryFile.class)) {
			registry.when(RegistryFile::read)
					.thenReturn(new HashSet<>(Arrays.asList(PrimaryStringRecipe1.class.getName(), PrimaryStringRecipe2.class.getName(), PrimaryStringRecipe3.class.getName(),
							BasicStringRecipe1.class.getName(), BasicStringRecipe2.class.getName(), BasicStringRecipe3.class.getName(), FallbackStringRecipe1.class.getName(),
							FallbackStringRecipe2.class.getName(), FallbackStringRecipe3.class.getName())));
			engine.init();
		}

		Assertions.assertEquals(10, engine.getBeanCount());
		assertBeans(new Descriptor<>(String.class), PrimaryStringRecipe1.VALUE, PrimaryStringRecipe2.VALUE, PrimaryStringRecipe3.VALUE, BasicStringRecipe1.VALUE, BasicStringRecipe2.VALUE,
				BasicStringRecipe3.VALUE);
	}

	/**
	 * Verify that beans can properly be retrieved when there is a single primary bean and many basic and fallback beans.
	 */
	@Test
	public void testSinglePrimaryBean() {
		try (MockedStatic<RegistryFile> registry = Mockito.mockStatic(RegistryFile.class)) {
			registry.when(RegistryFile::read).thenReturn(new HashSet<>(Arrays.asList(PrimaryStringRecipe1.class.getName(), BasicStringRecipe1.class.getName(), BasicStringRecipe2.class.getName(),
					BasicStringRecipe3.class.getName(), FallbackStringRecipe1.class.getName(), FallbackStringRecipe2.class.getName(), FallbackStringRecipe3.class.getName())));
			engine.init();
		}

		Assertions.assertEquals(8, engine.getBeanCount());
		Assertions.assertEquals(PrimaryStringRecipe1.VALUE, engine.getBean(new Descriptor<>(String.class)));
		Assertions.assertEquals(4, engine.count(new Descriptor<>(String.class)));
		CollectionAssert.assertEquivalent(engine.getAllBeans(new Descriptor<>(String.class)), PrimaryStringRecipe1.VALUE, BasicStringRecipe1.VALUE, BasicStringRecipe2.VALUE, BasicStringRecipe3.VALUE);
	}

	/**
	 * Verify that beans can properly be retrieved when there is are no primary, but multiple basic and fallback beans.
	 */
	@Test
	public void testNoPrimaryMultipleBasicBeans() {
		try (MockedStatic<RegistryFile> registry = Mockito.mockStatic(RegistryFile.class)) {
			registry.when(RegistryFile::read).thenReturn(new HashSet<>(Arrays.asList(BasicStringRecipe1.class.getName(), BasicStringRecipe2.class.getName(), BasicStringRecipe3.class.getName(),
					FallbackStringRecipe1.class.getName(), FallbackStringRecipe2.class.getName(), FallbackStringRecipe3.class.getName())));
			engine.init();
		}

		Assertions.assertEquals(7, engine.getBeanCount());
		assertBeans(new Descriptor<>(String.class), BasicStringRecipe1.VALUE, BasicStringRecipe2.VALUE, BasicStringRecipe3.VALUE);
	}

	/**
	 * Verify that beans can properly be retrieved when there is no primary bean, a single basic and multiple fallback beans.
	 */
	@Test
	public void testNoPrimarySingleBasicBean() {
		try (MockedStatic<RegistryFile> registry = Mockito.mockStatic(RegistryFile.class)) {
			registry.when(RegistryFile::read).thenReturn(new HashSet<>(
					Arrays.asList(BasicStringRecipe3.class.getName(), FallbackStringRecipe1.class.getName(), FallbackStringRecipe2.class.getName(), FallbackStringRecipe3.class.getName())));
			engine.init();
		}

		Assertions.assertEquals(5, engine.getBeanCount());
		assertBeans(new Descriptor<>(String.class), BasicStringRecipe3.VALUE);
	}

	/**
	 * Verify that beans can properly be retrieved when there are no primary or basic, but multiple fallback beans.
	 */
	@Test
	public void testNoPrimaryNoBasicMultipleFallbackBeans() {
		try (MockedStatic<RegistryFile> registry = Mockito.mockStatic(RegistryFile.class)) {
			registry.when(RegistryFile::read)
					.thenReturn(new HashSet<>(Arrays.asList(FallbackStringRecipe1.class.getName(), FallbackStringRecipe2.class.getName(), FallbackStringRecipe3.class.getName())));
			engine.init();
		}

		Assertions.assertEquals(4, engine.getBeanCount());
		assertBeans(new Descriptor<>(String.class), FallbackStringRecipe1.VALUE, FallbackStringRecipe2.VALUE, FallbackStringRecipe3.VALUE);
	}

	/**
	 * Verify that beans can properly be retrieved when there are no primary or basic, but a single fallback bean.
	 */
	@Test
	public void testNoPrimaryNoBasicSingleFallbackBean() {
		try (MockedStatic<RegistryFile> registry = Mockito.mockStatic(RegistryFile.class)) {
			registry.when(RegistryFile::read).thenReturn(new HashSet<>(Collections.singletonList(FallbackStringRecipe2.class.getName())));
			engine.init();
		}

		Assertions.assertEquals(2, engine.getBeanCount());
		assertBeans(new Descriptor<>(String.class), FallbackStringRecipe2.VALUE);
	}

	/**
	 * Verify that the engine will read environments from the environments property
	 */
	@Test
	public void testEnvironmentProperty() {
		// Verify that if the property is empty/missing, no environments are set
		System.clearProperty("environments");
		CollectionAssert.assertEmpty(new Engine().getEnvironments());
		System.setProperty("environments", "");
		CollectionAssert.assertEmpty(new Engine().getEnvironments());
		System.setProperty("environments", "   		   ");
		CollectionAssert.assertEmpty(new Engine().getEnvironments());

		// Verify a single environment can be set
		System.setProperty("environments", "abc");
		CollectionAssert.assertEquivalent(Collections.singletonList("abc"), new Engine().getEnvironments());

		// Verify two environments can be set
		System.setProperty("environments", "abc,def");
		CollectionAssert.assertEquivalent(Arrays.asList("abc", "def"), new Engine().getEnvironments());

		// Verify multiple environments can be set
		System.setProperty("environments", "abc,def,123,qwerty,asdf,wasd,zxcv");
		CollectionAssert.assertEquivalent(Arrays.asList("abc", "def", "123", "qwerty", "asdf", "wasd", "zxcv"), new Engine().getEnvironments());

		// Verify can mix and match property environments and code ones
		System.setProperty("environments", "abc");
		engine = new Engine();
		CollectionAssert.assertEquivalent(Collections.singletonList("abc"), engine.getEnvironments());
		engine.addEnvironments("def", "ghi");
		CollectionAssert.assertEquivalent(Arrays.asList("abc", "def", "ghi"), engine.getEnvironments());
		engine.addEnvironments("123", "345", "678", "901");
		CollectionAssert.assertEquivalent(Arrays.asList("abc", "def", "ghi", "123", "345", "678", "901"), engine.getEnvironments());

		// Verify that if the property is empty/missing, no environments are set
		System.clearProperty("environments");
		CollectionAssert.assertEmpty(new Engine().getEnvironments());
	}

	/**
	 * Verify that the dynamic blueprints are properly stored and retrieved
	 */
	@Test
	public void testDynamicBlueprints() {
		engine.addBlueprint(mockLevel11Driver);
		engine.addBlueprint(mockLevel12Driver);
		engine.addBlueprint(mockLevel21Driver);
		engine.addBlueprint(mockLevel22Driver);
		engine.addBlueprint(mockLevel31Driver);
		engine.addBlueprint(mockLevel32Driver);
		engine.addBlueprint(mockSeparateLevel11Driver);
		engine.addBlueprint(mockSeparateLevel12Driver);
		engine.addBlueprint(mockSeparateLevel21Driver);
		engine.addBlueprint(mockSeparateLevel22Driver);

		CollectionAssert.assertEquivalent(engine.getBlueprints(Level3.class), mockLevel31Driver, mockLevel32Driver);
		CollectionAssert.assertEquivalent(engine.getBlueprints(Level2.class), mockLevel21Driver, mockLevel22Driver, mockLevel31Driver, mockLevel32Driver);
		CollectionAssert.assertEquivalent(engine.getBlueprints(Level1.class), mockLevel11Driver, mockLevel12Driver, mockLevel21Driver, mockLevel22Driver, mockLevel31Driver, mockLevel32Driver);
		CollectionAssert.assertEquivalent(engine.getBlueprints(SeparateLevel2.class), mockSeparateLevel21Driver, mockSeparateLevel22Driver);
		CollectionAssert.assertEquivalent(engine.getBlueprints(SeparateLevel1.class), mockSeparateLevel11Driver, mockSeparateLevel12Driver, mockSeparateLevel21Driver, mockSeparateLevel22Driver);
		CollectionAssert.assertEquivalent(engine.getBlueprints(Blueprint.class), mockLevel11Driver, mockLevel12Driver, mockLevel21Driver, mockLevel22Driver, mockLevel31Driver, mockLevel32Driver,
				mockSeparateLevel11Driver, mockSeparateLevel12Driver, mockSeparateLevel21Driver, mockSeparateLevel22Driver);
	}

	/**
	 * Verify that original recipe can be loaded
	 */
	@Test
	public void testOriginalIntRecipe() {
		try (MockedStatic<RegistryFile> registry = Mockito.mockStatic(RegistryFile.class)) {
			registry.when(RegistryFile::read).thenReturn(new HashSet<>(Arrays.asList(IntTestRecipe.class.getName(), Double1TestRecipe.class.getName(), BasicStringRecipe1.class.getName())));
			engine.init();
		}

		Assertions.assertEquals(4, engine.getBeanCount());
		assertBeans(new Descriptor<>(Integer.class), IntTestRecipe.VALUE);
		assertBeans(new Descriptor<>(Double.class), Double1TestRecipe.VALUE);
		assertBeans(new Descriptor<>(String.class), BasicStringRecipe1.VALUE);
		assertBeans(new Descriptor<>(ApplicationContext.class), engine);
	}

	/**
	 * Verify that a recipe can be replaced if the original is present
	 */
	@Test
	public void testReplacementIntRecipe() {
		try (MockedStatic<RegistryFile> registry = Mockito.mockStatic(RegistryFile.class)) {
			try (MockedStatic<ReplacementRegistryFile> replaceRegistry = Mockito.mockStatic(ReplacementRegistryFile.class)) {
				registry.when(RegistryFile::read).thenReturn(new HashSet<>(Arrays.asList(IntTestRecipe.class.getName(), Double1TestRecipe.class.getName(), BasicStringRecipe1.class.getName())));
				replaceRegistry.when(ReplacementRegistryFile::read).thenReturn(new HashSet<>(Arrays.asList(ReplaceIntRecipe.class.getName())));
				engine.init();
			}
		}

		Assertions.assertEquals(4, engine.getBeanCount());
		assertBeans(new Descriptor<>(Integer.class), ReplaceIntRecipe.VALUE);
		assertBeans(new Descriptor<>(Double.class), Double1TestRecipe.VALUE);
		assertBeans(new Descriptor<>(String.class), BasicStringRecipe1.VALUE);
		assertBeans(new Descriptor<>(ApplicationContext.class), engine);
	}

	/**
	 * Verify that a recipe cannot be replaced if the original is not present
	 */
	@Test
	public void testReplacementIntRecipeNoOriginal() {
		try (MockedStatic<RegistryFile> registry = Mockito.mockStatic(RegistryFile.class)) {
			try (MockedStatic<ReplacementRegistryFile> replaceRegistry = Mockito.mockStatic(ReplacementRegistryFile.class)) {
				registry.when(RegistryFile::read).thenReturn(new HashSet<>(Arrays.asList(Double1TestRecipe.class.getName(), BasicStringRecipe1.class.getName())));
				replaceRegistry.when(ReplacementRegistryFile::read).thenReturn(new HashSet<>(Arrays.asList(ReplaceIntRecipe.class.getName())));
				Assertions.assertThrows(BeanReplacementException.class, () -> engine.init());
			}
		}
	}

	/**
	 * Verify that a recipe cannot be replaced if the there are multiple original matches
	 */
	@Test
	public void testReplacementIntRecipeTooManyOriginals() {
		try (MockedStatic<RegistryFile> registry = Mockito.mockStatic(RegistryFile.class)) {
			try (MockedStatic<ReplacementRegistryFile> replaceRegistry = Mockito.mockStatic(ReplacementRegistryFile.class)) {
				registry.when(RegistryFile::read).thenReturn(new HashSet<>(Arrays.asList(BasicStringRecipe1.class.getName(), BasicStringRecipe2.class.getName(), BasicStringRecipe3.class.getName())));
				replaceRegistry.when(ReplacementRegistryFile::read).thenReturn(new HashSet<>(Arrays.asList(ReplaceStringRecipe.class.getName())));
				Assertions.assertThrows(BeanReplacementException.class, () -> engine.init());
			}
		}
	}

	/**
	 * Helper for verifying that the expected beans are retrieved for the given descriptor
	 * 
	 * @param <BEAN_TYPE> of the bean to retrieve
	 * @param desc        {@link Descriptor} of the bean(s)
	 * @param values      BEAN_TYPE... listing the values/beans that are expected
	 */
	@SafeVarargs
	private <BEAN_TYPE> void assertBeans(Descriptor<BEAN_TYPE> desc, BEAN_TYPE... values) {
		int numBeansExpected = values.length;

		// Make sure the right count is returned
		Assertions.assertEquals(numBeansExpected, engine.count(desc));

		// Make sure the single bean retrieval works
		if (numBeansExpected == 1)
			Assertions.assertEquals(values[0], engine.getBean(desc));
		else
			Assertions.assertThrows(BeanRetrievalException.class, () -> engine.getBean(desc));

		// Make sure that all beans can be retrieved
		CollectionAssert.assertEquivalent(engine.getAllBeans(desc), values);
	}
}
