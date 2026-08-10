package tendril.junit5;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstanceFactory;
import org.junit.jupiter.api.extension.TestInstanceFactoryContext;
import org.junit.jupiter.api.extension.TestInstantiationException;

import tendril.TendrilStartupException;
import tendril.bean.duplicate.Blueprint;
import tendril.context.ApplicationContext;
import tendril.context.ApplicationContextBuilder;
import tendril.context.launch.TendrilRunner;
import tendril.test.TendrilTest;
import tendril.test.TestBlueprints;
import tendril.test.TestProperties;
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
		for (Blueprint db : getBlueprints(testClass))
			builder.addBlueprint(db);
		for (Map.Entry<String, String> p: getProperties(testClass).entrySet())
			System.setProperty(p.getKey(), p.getValue());

		// Load details from the test annotation
		TendrilTest annon = testClass.getAnnotation(TendrilTest.class);
		builder.setEnvironments(annon.environments());
		engine.setProperties(annon.properties());
		
		return (TestEngine) builder.build();
	}

	/**
	 * Recursively check the test class and all of its parents for static methods defining {@link Blueprint}s. These must be static methods annotated with {@link TestBlueprints} with
	 * checks performed to ensure that the method has the correct return type and parameters (namely none). All of the {@link Blueprint}s defined in all of the different classes are combined
	 * creating a "superset" of all {@link Blueprint}s in the test class inheritance hierarchy.
	 * 
	 * @param testClass {@link Class} where to look for the {@link Blueprint} defining static method
	 * @return {@link List} of {@link Blueprint}s that are defined by the test class
	 */
	private List<Blueprint> getBlueprints(Class<?> testClass) {
		List<Blueprint> blueprints = new ArrayList<>();
		List<Blueprint> classBlueprints = getMethodResult(testClass, TestBlueprints.class, List.class, List.class.getName() + "<" + Blueprint.class.getName() + ">");
		if (classBlueprints != null)
			blueprints.addAll(classBlueprints);
		
		// Collect any blueprints defined in the parent class and add those it inherits from
		Class<?> parent = testClass.getSuperclass();
		if (parent != null)
			blueprints.addAll(getBlueprints(parent));
		
		return blueprints;
	}
	
	/**
	 * Recursively check the test class and all of its parents for static methods defining system properties. These must be static methods annotated with {@link TestProperties} with
	 * checks performed to ensure that the method has the correct return type and parameters (namely none). All of the properties defined in all of the different classes are combined
	 * creating a "superset" of all properties in the test class inheritance hierarchy.
	 * 
	 * @param testClass {@link Class} where to look for the {@link Blueprint} defining static method
	 * @return {@link Map} of {@link String} to {@link String} properties that are defined by the test class
	 */
	private Map<String, String> getProperties(Class<?> testClass) {
		Map<String, String> props = new HashMap<>();
		Map<String, String> classProps = getMethodResult(testClass, TestProperties.class, Map.class, Map.class.getName() + "<String, String>");
		if (classProps != null)
			props.putAll(classProps);
		
		// Collect any properties defined in the parent class and add those it inherits from
		Class<?> parent = testClass.getSuperclass();
		if (parent != null)
			props.putAll(getProperties(parent));
		
		return props;
	}
	
	/**
	 * Helper to try and call all static methods defined on the testClass that have the specified annotation applied.
	 * 
	 * @param <T> The expected return type from the annotated method
	 * @param testClass {@link Class} in which the test is defined and in which to search for annotated methods
	 * @param requiredAnnotation {@link Class} extending {@link Annotation} representing the annotation to look for
	 * @param returnType {@link Class} indicating the expected return type of the method
	 * @param returnErrorMsg {@link String} to include in the resulting error message if the annotated method has the incorrect return type
	 * 
	 * @return {@code T} that the annotated method on the class returned
	 */
	private <T> T getMethodResult(Class<?> testClass, Class<? extends Annotation> annotation, Class<?> returnType, String returnErrorMsg) {
		for (Method m : testClass.getDeclaredMethods()) {

			// Make sure that the method is properly configured
			if (!m.isAnnotationPresent(annotation))
				continue;

			if (!Modifier.isStatic(m.getModifiers()))
				throwException(testClass, m, annotation.getSimpleName() + " can only be applied to static methods");
			if (m.getReturnType() != returnType)
				throwException(testClass, m, "Unexpected return type, must be " + returnErrorMsg);
			if (m.getParameterCount() > 0)
				throwException(testClass, m, "Cannot take any parameters");
			try {
				@SuppressWarnings("deprecation")
				boolean origAccess = m.isAccessible();
				m.setAccessible(true);
				@SuppressWarnings("unchecked")
				T result = (T) m.invoke(testClass);
				m.setAccessible(origAccess);
				return result;
			} catch (Exception e) {
				throw new TendrilStartupException(getIntroMessage(testClass, m), e);
			}
		}
		
		return null;
	}

	/**
	 * Helper to through an exception if an error accessing/retrieving the {@link Blueprint} defining method is encountered.
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
		return "Error processing " + testClass.getName() + "::" + m.getName() + "()";
	}
}
