package tendril.junit5;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstanceFactory;
import org.junit.jupiter.api.extension.TestInstanceFactoryContext;
import org.junit.jupiter.api.extension.TestInstantiationException;

import tendril.TendrilStartupException;
import tendril.context.ApplicationContext;
import tendril.context.ApplicationContextBuilder;
import tendril.context.launch.TendrilRunner;
import tendril.test.TendrilTest;
import tendril.test.context.TestEngine;

/**
 * JUnit extension to run the test class in an {@link ApplicationContext}. This allows for dependency injection to be performed and uses the test case in lieu of a {@link TendrilRunner}
 */
public class TendrilTestExtension implements TestInstanceFactory {

	/**
	 * CTOR
	 */
	public TendrilTestExtension() {
	}

	/**
	 * Creates the instance of the class which runs the test. The test class is created via the {@link ApplicationContext} to ensure that any/all dependency injection the test class performs can be
	 * fulfilled.
	 * 
	 * @see org.junit.jupiter.api.extension.TestInstanceFactory#createTestInstance(org.junit.jupiter.api.extension.TestInstanceFactoryContext, org.junit.jupiter.api.extension.ExtensionContext)
	 */
	@Override
	public Object createTestInstance(TestInstanceFactoryContext factoryContext, ExtensionContext extensionContext) throws TestInstantiationException {
		try {
			return prepareEngine(factoryContext.getTestClass()).getTestRunner();
		} catch (Exception e) {
			throw new TendrilStartupException(e);
		}
	}

	/**
	 * Prepare the engine in which the test is to be executed
	 * 
	 * @param testClass {@link Class} where the test is defined
	 */
	private TestEngine prepareEngine(Class<?> testClass) {
		TestEngine engine = new TestEngine(testClass);
		ApplicationContextBuilder builder = new ApplicationContextBuilder(engine);

		// Load details from the test annotation
		TendrilTest annon = testClass.getAnnotation(TendrilTest.class);
		builder.setEnvironments(annon.environments());
		engine.setProperties(annon.properties());
		return (TestEngine) builder.build();
	}
}
