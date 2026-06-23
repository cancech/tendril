package tendril.processor.recipe;

import tendril.codegen.VisibilityType;
import tendril.codegen.classes.JClass;
import tendril.codegen.field.JContainedType;
import tendril.codegen.field.JVisibleType;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.Type;

/**
 *Helper containing various static methods which can be used to aid in the generation of bean or configuration recipes.
 */
public abstract class RecipeGeneratorHelper {


	/**
	 * Get the class reference for the specified type. If this is a class which contains generics, then it will be "appropriately wrapped" so that it
	 * works properly for the Java compiler. 
	 * 
	 * @param type {@link Type} whose class reference is desired
	 * @return {@link String} with the code necessary to retrieve the class reference
	 */
	public static String getClassReference(Type type) {
		// TODO the resulting code generates a warning, see about either getting rid of it or adding @SuppressWarnings("unchecked") to the containing method
		String classReference = type.getClassName() + ".class";
		
		if (type instanceof ClassType classType && classType.hasGenerics())
			return "(Class<" + type.getSimpleName() + ">) (Class<?>) " + classReference;
		
		return classReference;
	}
    
	/**
	 * Check to see whether reflection is required to access the specified element from the creator
	 * 
	 * @param creatorType ClassType of the creator from where the element is to be accessed
	 * @param element {@link JContainedType} that is to be accessed
	 * @return boolean true if reflection is required
	 */
    public static boolean requiresReflection(ClassType creatorType, JContainedType<?> element) {
        JContainedType<?> container = element.getContainer();
        if (container == null)
        	return false;
        
        if (container instanceof JClass klass) {
        	ClassType containerType = klass.getType();
        	VisibilityType elementVisibility = ((JVisibleType<?>) element).getVisibility();
        	if (containerType.getPackageName().equals(creatorType.getPackageName()))
        		return elementVisibility == VisibilityType.PRIVATE;
        	else
        		return elementVisibility != VisibilityType.PUBLIC;
        }
        
        return true;
    }
}
