package tendril.junit5;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstanceFactory;
import org.junit.jupiter.api.extension.TestInstanceFactoryContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import org.junit.jupiter.api.extension.TestInstancePreConstructCallback;
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
public class TendrilTestExtension implements TestInstancePostProcessor, TestInstancePreConstructCallback, TestInstanceFactory {

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
			Class<?> testClass = factoryContext.getTestClass();
			ApplicationContextBuilder builder = new ApplicationContextBuilder(new TestEngine(testClass));
			loadAnnotation(testClass, builder);
			TestEngine engine = (TestEngine) builder.build();
			return engine.getTestRunner();
		} catch (Exception e) {
			throw new TendrilStartupException(e);
		}
	}

	/**
	 * Load the annotation with the details of the tendril test
	 * 
	 * @param testClass {@link Class} where the test is defined
	 * @param builder   {@link ApplicationContextBuilder} where the test context is being created
	 */
	private void loadAnnotation(Class<?> testClass, ApplicationContextBuilder builder) {
		TendrilTest annon = testClass.getAnnotation(TendrilTest.class);
		builder.setEnvironments(annon.environments());
	}

	/**
	 * TBD - kept as a placeholder for the time being.
	 * 
	 * @see org.junit.jupiter.api.extension.TestInstancePreConstructCallback#preConstructTestInstance(org.junit.jupiter.api.extension.TestInstanceFactoryContext,
	 *      org.junit.jupiter.api.extension.ExtensionContext)
	 */
	@Override
	public void preConstructTestInstance(TestInstanceFactoryContext factoryContext, ExtensionContext context) throws Exception {
		// TODO TBD if this is required
	}

	/**
	 * TBD - kept as a placeholder for the time being.
	 * 
	 * @see org.junit.jupiter.api.extension.TestInstancePostProcessor#postProcessTestInstance(java.lang.Object, org.junit.jupiter.api.extension.ExtensionContext)
	 */
	@Override
	public void postProcessTestInstance(Object testInstance, ExtensionContext context) throws Exception {
		// TODO TBD if this is required
	}
}
