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
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import tendril.BeanRetrievalException;
import tendril.bean.qualifier.Descriptor;
import tendril.processor.registration.RegistryFile;
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
import tendril.test.recipe.RequiresABRecipe;
import tendril.test.recipe.RequiresAConfigRecipe;
import tendril.test.recipe.RequiresANotBRecipe;
import tendril.test.recipe.RequiresARecipe;
import tendril.test.recipe.RequiresAorBRecipe;
import tendril.test.recipe.RequiresBNestedRecipe;
import tendril.test.recipe.RequiresBNotARecipe;
import tendril.test.recipe.RequiresBRecipe;
import tendril.test.recipe.RequiresNotARecipe;
import tendril.test.recipe.RequiresNotBRecipe;
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
			registry.when(RegistryFile::read).thenReturn(new HashSet<>(Arrays.asList(Double1TestRecipe.class.getName(), RequiresARecipe.class.getName(), RequiresBRecipe.class.getName(),
					RequiresABRecipe.class.getName(), RequiresAConfigRecipe.class.getName(), RequiresAorBRecipe.class.getName())));
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
			registry.when(RegistryFile::read)
					.thenReturn(new HashSet<>(Arrays.asList(Double1TestRecipe.class.getName(), RequiresARecipe.class.getName(), RequiresBRecipe.class.getName(), RequiresABRecipe.class.getName(),
							RequiresAConfigRecipe.class.getName(), RequiresANotBRecipe.class.getName(), RequiresBNotARecipe.class.getName(), RequiresNotARecipe.class.getName(),
							RequiresNotBRecipe.class.getName(), RequiresAorBRecipe.class.getName())));
			engine.init();
		}

		Assertions.assertEquals(6, engine.getBeanCount());
		Assertions.assertEquals(Double1TestRecipe.VALUE, engine.getBean(new Descriptor<>(Double.class).setName(Double1TestRecipe.NAME)));
		Assertions.assertEquals(RequiresARecipe.VALUE, engine.getBean(new Descriptor<>(String.class).setName(RequiresARecipe.NAME)));
		Assertions.assertEquals(IntTestRecipe.VALUE, engine.getBean(new Descriptor<>(Integer.class).setName(IntTestRecipe.NAME)));
		Assertions.assertEquals(RequiresANotBRecipe.VALUE, engine.getBean(new Descriptor<>(String.class).setName(RequiresANotBRecipe.NAME)));
		Assertions.assertEquals(RequiresNotBRecipe.VALUE, engine.getBean(new Descriptor<>(String.class).setName(RequiresNotBRecipe.NAME)));
		Assertions.assertEquals(RequiresAorBRecipe.VALUE, engine.getBean(new Descriptor<>(Integer.class).setName(RequiresAorBRecipe.NAME)));

		CollectionAssert.assertEmpty(engine.getAllBeans(new Descriptor<>(Long.class)));
		CollectionAssert.assertEquivalent(engine.getAllBeans(new Descriptor<>(Double.class)), Double1TestRecipe.VALUE);
		CollectionAssert.assertEquivalent(engine.getAllBeans(new Descriptor<>(String.class)), RequiresARecipe.VALUE, RequiresANotBRecipe.VALUE, RequiresNotBRecipe.VALUE);
		CollectionAssert.assertEquivalent(engine.getAllBeans(new Descriptor<>(Integer.class)), IntTestRecipe.VALUE, RequiresAorBRecipe.VALUE);
	}

	/**
	 * Verify that only beans meeting requirements are available
	 */
	@Test
	public void testBEnvironmentSpecified() {
		engine.setEnvironments("B");
		try (MockedStatic<RegistryFile> registry = Mockito.mockStatic(RegistryFile.class)) {
			registry.when(RegistryFile::read)
					.thenReturn(new HashSet<>(Arrays.asList(Double1TestRecipe.class.getName(), RequiresARecipe.class.getName(), RequiresBRecipe.class.getName(), RequiresABRecipe.class.getName(),
							RequiresAConfigRecipe.class.getName(), RequiresANotBRecipe.class.getName(), RequiresBNotARecipe.class.getName(), RequiresNotARecipe.class.getName(),
							RequiresNotBRecipe.class.getName(), RequiresAorBRecipe.class.getName())));
			engine.init();
		}

		Assertions.assertEquals(5, engine.getBeanCount());
		Assertions.assertEquals(Double1TestRecipe.VALUE, engine.getBean(new Descriptor<>(Double.class).setName(Double1TestRecipe.NAME)));
		Assertions.assertEquals(RequiresBRecipe.VALUE, engine.getBean(new Descriptor<>(Double.class).setName(RequiresBRecipe.NAME)));
		Assertions.assertEquals(RequiresBNotARecipe.VALUE, engine.getBean(new Descriptor<>(String.class).setName(RequiresBNotARecipe.NAME)));
		Assertions.assertEquals(RequiresNotARecipe.VALUE, engine.getBean(new Descriptor<>(String.class).setName(RequiresNotARecipe.NAME)));
		Assertions.assertEquals(RequiresAorBRecipe.VALUE, engine.getBean(new Descriptor<>(Integer.class).setName(RequiresAorBRecipe.NAME)));

		CollectionAssert.assertEmpty(engine.getAllBeans(new Descriptor<>(Long.class)));
		CollectionAssert.assertEquivalent(engine.getAllBeans(new Descriptor<>(Double.class)), Double1TestRecipe.VALUE, RequiresBRecipe.VALUE);
		CollectionAssert.assertEquivalent(engine.getAllBeans(new Descriptor<>(String.class)), RequiresBNotARecipe.VALUE, RequiresNotARecipe.VALUE);
		CollectionAssert.assertEquivalent(engine.getAllBeans(new Descriptor<>(Integer.class)), RequiresAorBRecipe.VALUE);
	}

	/**
	 * Verify that only beans meeting requirements are available
	 */
	@Test
	public void testABEnvironmentSpecified() {
		engine.setEnvironments("A", "B");
		try (MockedStatic<RegistryFile> registry = Mockito.mockStatic(RegistryFile.class)) {
			registry.when(RegistryFile::read)
					.thenReturn(new HashSet<>(Arrays.asList(Double1TestRecipe.class.getName(), RequiresARecipe.class.getName(), RequiresBRecipe.class.getName(), RequiresABRecipe.class.getName(),
							RequiresAConfigRecipe.class.getName(), RequiresANotBRecipe.class.getName(), RequiresBNotARecipe.class.getName(), RequiresNotARecipe.class.getName(),
							RequiresNotBRecipe.class.getName(), RequiresAorBRecipe.class.getName())));
			engine.init();
		}

		Assertions.assertEquals(7, engine.getBeanCount());
		Assertions.assertEquals(Double1TestRecipe.VALUE, engine.getBean(new Descriptor<>(Double.class).setName(Double1TestRecipe.NAME)));
		Assertions.assertEquals(RequiresARecipe.VALUE, engine.getBean(new Descriptor<>(String.class).setName(RequiresARecipe.NAME)));
		Assertions.assertEquals(RequiresBRecipe.VALUE, engine.getBean(new Descriptor<>(Double.class).setName(RequiresBRecipe.NAME)));
		Assertions.assertEquals(RequiresABRecipe.VALUE, engine.getBean(new Descriptor<>(Integer.class).setName(RequiresABRecipe.NAME)));
		Assertions.assertEquals(IntTestRecipe.VALUE, engine.getBean(new Descriptor<>(Integer.class).setName(IntTestRecipe.NAME)));
		Assertions.assertEquals(RequiresBNestedRecipe.VALUE, engine.getBean(new Descriptor<>(Double.class).setName(RequiresBNestedRecipe.NAME)));
		Assertions.assertEquals(RequiresAorBRecipe.VALUE, engine.getBean(new Descriptor<>(Integer.class).setName(RequiresAorBRecipe.NAME)));

		CollectionAssert.assertEmpty(engine.getAllBeans(new Descriptor<>(Long.class)));
		CollectionAssert.assertEquivalent(engine.getAllBeans(new Descriptor<>(Double.class)), Double1TestRecipe.VALUE, RequiresBRecipe.VALUE, RequiresBNestedRecipe.VALUE);
		CollectionAssert.assertEquivalent(engine.getAllBeans(new Descriptor<>(String.class)), RequiresARecipe.VALUE);
		CollectionAssert.assertEquivalent(engine.getAllBeans(new Descriptor<>(Integer.class)), RequiresABRecipe.VALUE, IntTestRecipe.VALUE, RequiresAorBRecipe.VALUE);
	}

	/**
	 * Verify that initializing the engine prevents changing the specified environment
	 */
	@Test
	public void testCannotChangeEnvironmentsAfterEngineInit() {
		testABEnvironmentSpecified();
		Assertions.assertThrows(RuntimeException.class, () -> engine.setEnvironments("qwerty"));
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

		Assertions.assertEquals(9, engine.getBeanCount());
		Assertions.assertThrows(BeanRetrievalException.class, () -> engine.getBean(new Descriptor<>(String.class)));
		CollectionAssert.assertEquivalent(engine.getAllBeans(new Descriptor<>(String.class)), PrimaryStringRecipe1.VALUE, PrimaryStringRecipe2.VALUE, PrimaryStringRecipe3.VALUE,
				BasicStringRecipe1.VALUE, BasicStringRecipe2.VALUE, BasicStringRecipe3.VALUE);
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

		Assertions.assertEquals(7, engine.getBeanCount());
		Assertions.assertEquals(PrimaryStringRecipe1.VALUE, engine.getBean(new Descriptor<>(String.class)));
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

		Assertions.assertEquals(6, engine.getBeanCount());
		Assertions.assertThrows(BeanRetrievalException.class, () -> engine.getBean(new Descriptor<>(String.class)));
		CollectionAssert.assertEquivalent(engine.getAllBeans(new Descriptor<>(String.class)), BasicStringRecipe1.VALUE, BasicStringRecipe2.VALUE, BasicStringRecipe3.VALUE);
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

		Assertions.assertEquals(4, engine.getBeanCount());
		Assertions.assertEquals(BasicStringRecipe3.VALUE, engine.getBean(new Descriptor<>(String.class)));
		CollectionAssert.assertEquivalent(engine.getAllBeans(new Descriptor<>(String.class)), BasicStringRecipe3.VALUE);
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

		Assertions.assertEquals(3, engine.getBeanCount());
		Assertions.assertThrows(BeanRetrievalException.class, () -> engine.getBean(new Descriptor<>(String.class)));
		CollectionAssert.assertEquivalent(engine.getAllBeans(new Descriptor<>(String.class)), FallbackStringRecipe1.VALUE, FallbackStringRecipe2.VALUE, FallbackStringRecipe3.VALUE);
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

		Assertions.assertEquals(1, engine.getBeanCount());
		Assertions.assertEquals(FallbackStringRecipe2.VALUE, engine.getBean(new Descriptor<>(String.class)));
		CollectionAssert.assertEquivalent(engine.getAllBeans(new Descriptor<>(String.class)), FallbackStringRecipe2.VALUE);
	}
}
