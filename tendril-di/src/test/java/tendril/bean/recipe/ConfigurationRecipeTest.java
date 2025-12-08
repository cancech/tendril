package tendril.bean.recipe;

import static org.mockito.Mockito.verifyNoInteractions;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import tendril.bean.qualifier.Descriptor;
import tendril.bean.requirement.Requirement;
import tendril.context.Engine;
import tendril.test.AbstractUnitTest;
import tendril.test.bean.SingleCtorBean;

/**
 * Test case for the {@link ConfigurationRecipe}
 */
public class ConfigurationRecipeTest extends AbstractUnitTest {
	
    // Mocks to use for testing
    @Mock
    private Engine mockEngine;
    @Mock
    private Descriptor<SingleCtorBean> mockDescriptor;
    
    // Instance to test
    private ConfigurationRecipe<SingleCtorBean> recipe;
    
    // Concrete instance to use for testing
    private class TestConfigurationRecipe extends ConfigurationRecipe<SingleCtorBean> {

		protected TestConfigurationRecipe(boolean isPrimary, boolean isFallback) {
			super(mockEngine, SingleCtorBean.class, isPrimary, isFallback);
		}

		@Override
		public Map<String, AbstractRecipe<?>> getNestedRecipes() {
			return new HashMap<>();
		}

		@Override
		protected void setupRequirement(Requirement requirement) {
		}

		@Override
		protected SingleCtorBean createInstance(Engine engine) {
            return new SingleCtorBean();
		}
    	
    };

    /**
     * @see tendril.test.AbstractUnitTest#prepareTest()
     */
	@Override
	protected void prepareTest() {
		recipe = new TestConfigurationRecipe(false, false);
	}
	
    /**
     * Verify that the primary and fallback flags are properly handled
     */
    @Test
    public void testRecipePrimaryFallbackFlags() {
    	TestConfigurationRecipe r1 = new TestConfigurationRecipe(false, false);
    	Assertions.assertFalse(r1.isPrimary());
    	Assertions.assertFalse(r1.isFallback());

    	TestConfigurationRecipe r2 = new TestConfigurationRecipe(true, false);
    	Assertions.assertTrue(r2.isPrimary());
    	Assertions.assertFalse(r2.isFallback());
    	
    	TestConfigurationRecipe r3 = new TestConfigurationRecipe(false, true);
    	Assertions.assertFalse(r3.isPrimary());
    	Assertions.assertTrue(r3.isFallback());

    	TestConfigurationRecipe r4 = new TestConfigurationRecipe(true, true);
    	Assertions.assertTrue(r4.isPrimary());
    	Assertions.assertTrue(r4.isFallback());
    }
    
    /**
     * Verify that nothing is done to the descriptor as it is not used for Configurations
     */
    @Test
    public void testSetupDescriptor() {
    	recipe.setupDescriptor(mockDescriptor);
    	verifyNoInteractions(mockDescriptor);
    }

}
