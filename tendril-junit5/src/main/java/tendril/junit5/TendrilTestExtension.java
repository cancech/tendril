package tendril.junit5;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstanceFactory;
import org.junit.jupiter.api.extension.TestInstanceFactoryContext;
import org.junit.jupiter.api.extension.TestInstantiationException;

import tendril.TendrilStartupException;
import tendril.bean.duplicate.BlueprintDriver;
import tendril.context.ApplicationContext;
import tendril.context.ApplicationContextBuilder;
import tendril.context.launch.TendrilRunner;
import tendril.test.TestBlueprints;
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
		for (BlueprintDriver db : getBlueprints(testClass))
			builder.addDynamicBlueprint(db);

		// Load details from the test annotation
		TendrilTest annon = testClass.getAnnotation(TendrilTest.class);
		builder.setEnvironments(annon.environments());
		engine.setProperties(annon.properties());
		return (TestEngine) builder.build();
	}

	/**
	 * Recursively check the test class and all of its parents for static methods defining {@link BlueprintDriver}s. These must be public static methods annotated with {@link TestBlueprints} with
	 * checks performed to ensure that the method has the correct return type and parameters (namely none). All of the {@link BlueprintDriver}s defined in all of the different classes are combined
	 * creating a "superset" of all {@link BlueprintDriver}s in the test class inheritance hierarchy.
	 * 
	 * @param testClass {@link Class} where to look for the {@link BlueprintDriver} defining static method
	 * @return {@link List} of {@link BlueprintDriver}s that are defined by the test class
	 */
	@SuppressWarnings("unchecked")
	private List<BlueprintDriver> getBlueprints(Class<?> testClass) {
		List<BlueprintDriver> blueprints = new ArrayList<>();
		for (Method m : testClass.getMethods()) {

			// Make sure that the method is properly configured
			if (!m.isAnnotationPresent(TestBlueprints.class))
				continue;
			if (!Modifier.isStatic(m.getModifiers()))
				throwException(testClass, m, TestBlueprints.class.getSimpleName() + " can only be applied to static methods");
			if (m.getReturnType() != List.class)
				throwException(testClass, m, "Unexpected return type, must be " + List.class.getName() + "<" + BlueprintDriver.class.getName() + ">");
			if (m.getParameterCount() > 0)
				throwException(testClass, m, "Cannot take any parameters");
			try {
				for (BlueprintDriver d : (List<BlueprintDriver>) m.invoke(blueprints))
					blueprints.add(d);
			} catch (Exception e) {
				throw new TendrilStartupException(getIntroMessage(testClass, m), e);
			}

			// Collect any blueprints defined in the parent class and add those
			Class<?> parent = testClass.getSuperclass();
			if (parent != null)
				blueprints.addAll(getBlueprints(parent));
		}
		return blueprints;
	}

	/**
	 * Helper to through an exception if an error accessing/retrieving the {@link BlueprintDriver} defining method is encountered.
	 * 
	 * @param testClass {@link Class} where the method is located
	 * @param m         {@link Method} on which the error was encountered
	 * @param msg       {@link String} the specific cause of the error
	 */
	private void throwException(Class<?> testClass, Method m, String msg) {
		throw new TendrilStartupException(getIntroMessage(testClass, m) + " - " + msg);
	}

	/**
	 * Prepare the intro message for the exception.
	 * 
	 * @param testClass {@link Class} where the method is located
	 * @param m         {@link Method} on which the error was encountered
	 * @return {@link String} with the intro portion of the error message
	 */
	private String getIntroMessage(Class<?> testClass, Method m) {
		return "Error processing " + testClass.getName() + "::" + m.getName();
	}
}
