package tendril.test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

import tendril.bean.duplicate.Blueprint;

/**
 * Annotation to be applied to public static method(s) in a test class where the {@link Blueprint}s for a given test are to be defined. Note that when processing all such annotated methods in
 * the inheritance hierarchy of the test class will be processed and combined, meaning that the {@link Blueprint}s for a test class will include any that the class itself defines as well as any
 * from class higher in the inheritance hierarchy. The method must be public state, return a {@link List} of {@link Blueprint}s, and take no parameters.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TestBlueprints {
}
