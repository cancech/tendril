package tendril.test.context;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import tendril.TendrilStartupException;
import tendril.bean.recipe.AbstractRecipe;
import tendril.context.Engine;
import tendril.context.launch.TendrilRunner;

/**
 * Test override of the default {@link Engine} such that it blocks the user of the normal {@link TendrilRunner} and allows for the creation and retrieval of the test class as a test runner "out of
 * turn" from normal dependency injection
 */
public class TestEngine extends Engine {

	/** The class of the test */
	private final Class<?> testClass;
	private String[] testProperties = {};

	/**
	 * CTOR
	 * 
	 * @param testClass {@link Class} in which the test is defined and represents the test to be executed
	 */
	public TestEngine(Class<?> testClass) {
		this.testClass = testClass;
	}

	/**
	 * Creates and retrieves the test class by means of its recipe. This ensures that the class is not only created, but it is also has its injections fulfilled.
	 * 
	 * @return {@link Object} instance of the test class that was instantiated
	 * 
	 * @throws InstantiationException    if an issue was encountered creating the test instance
	 * @throws IllegalAccessException    if an issue was encountered creating the test instance
	 * @throws IllegalArgumentException  if an issue was encountered creating the test instance
	 * @throws InvocationTargetException if an issue was encountered creating the test instance
	 * @throws SecurityException         if an issue was encountered creating the test instance
	 * @throws ClassNotFoundException    if an issue was encountered creating the test instance
	 */
	public Object getTestRunner() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, SecurityException, ClassNotFoundException {
		@SuppressWarnings("unchecked")
		Class<? extends AbstractRecipe<?>> recipeClass = (Class<? extends AbstractRecipe<?>>) Class.forName(testClass.getName() + "Recipe");
		AbstractRecipe<?> recipe = (AbstractRecipe<?>) recipeClass.getConstructors()[0].newInstance(this);
		return recipe.get();
	}

	/**
	 * Add properties that are to be used to augment the System properties for the purpose of the test. The system properties as such are not touched, with these merely augmenting them.
	 * 
	 * @param properties {@link String}... properties to add
	 */
	public void setProperties(String... properties) {
		testProperties = properties;
	}

	/**
	 * 
	 * @see tendril.context.Engine#systemPropertyList()
	 */
	@Override
	protected List<String> systemPropertyList() {
		List<String> props = super.systemPropertyList();
		for (String p: testProperties)
			props.add(p);
		return props;
	}

	/**
	 * Should not be used in a test environment. Throws an exception if called.
	 * 
	 * @see tendril.context.Engine#start()
	 */
	@Override
	public void start() {
		throw new TendrilStartupException("The application context is not started through normal means when running in a test environment");
	}
}
