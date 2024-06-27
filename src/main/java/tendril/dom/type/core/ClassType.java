package tendril.dom.type.core;

import tendril.dom.classes.ImportElement;
import tendril.dom.type.Type;

/**
 * Represents a class or other declared element. Incorporates all of the necessary details for the element, including the ability to "generate" new elements that derive from it.
 */
public class ClassType extends ImportElement implements Type {

    /**
     * CTOR
     * 
     * @param klass {@link Class} the specific class definition which is being described
     */
    public ClassType(Class<?> klass) {
        super(klass);
    }

    /**
     * CTOR
     * 
     * @param fullyQualifiedName {@link String} the fully qualified name of the defined class
     */
    public ClassType(String fullyQualifiedName) {
        super(fullyQualifiedName);
    }

    /**
     * CTOR
     * 
     * @param packageName {@link String} the name of the package where the defined class lives
     * @param className   {@link String} the name of the class itself
     */
    public ClassType(String packageName, String className) {
        super(packageName, className);
    }

    /**
     * Derive a new class definition from the current one, such that the specified suffix is applied to the generated class name
     * 
     * @param classSuffix {@link String} the suffix to apply to generate a new class definition
     * @return {@link ClassType} of the new class
     */
    public ClassType generateFromClassSuffix(String classSuffix) {
        return new ClassType(getPackageName(), getClassName() + classSuffix);
    }

    /**
     * @see tendril.dom.type.Type#isAssignableTo(tendril.dom.type.Type)
     */
    @SuppressWarnings("unlikely-arg-type")
    @Override
    public boolean isAssignableTo(Type other) {
        return super.equals(other);
    }
}
