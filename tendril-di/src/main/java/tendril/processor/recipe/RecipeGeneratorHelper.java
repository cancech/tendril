package tendril.processor.recipe;

import java.util.List;

import tendril.codegen.DefinitionException;
import tendril.codegen.VisibilityType;
import tendril.codegen.classes.JClass;
import tendril.codegen.field.JContainedType;
import tendril.codegen.field.JVisibleType;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.Type;
import tendril.codegen.field.type.TypeFactory;
import tendril.codegen.generics.CompoundExtendsGeneric;
import tendril.codegen.generics.GenericFactory;
import tendril.codegen.generics.GenericType;
import tendril.codegen.generics.SimpleExplicitGeneric;
import tendril.codegen.generics.SimpleWildcardGeneric;

/**
 * Helper containing various static methods which can be used to aid in the generation of bean or configuration recipes.
 */
public abstract class RecipeGeneratorHelper {

	/**
	 * Hidden CTOR as no instance of this class should be created
	 */
	private RecipeGeneratorHelper() {
	}

	/**
	 * Get the class reference for the specified type. If this is a class which contains generics, then it will be "appropriately wrapped" so that it works properly for the Java compiler.
	 * 
	 * @param type {@link Type} whose class reference is desired
	 * @return {@link String} with the code necessary to retrieve the class reference
	 */
	public static String getClassReference(Type type) {
		if (type instanceof GenericType gType)
			return getGenericTypeReference(gType);
		else if (type instanceof ClassType cType && cType.hasGenerics())
			return getClassTypeReference(cType);

		return type.asClassType().getFullyQualifiedName() + ".class";
	}

	/**
	 * Generate the reference to a class that is defined by a {@link ClassType} in a recipe. To be used with the {@code getGenericsReference}.
	 * 
	 * @param type {@link ClassType} representing the type of class to reference in the recipe
	 * @return {@link String} code to create the {@link ClassType} reference to for the class
	 */
	private static String getClassTypeReference(ClassType type) {
		String code = TypeFactory.class.getName() + ".createClassType(" + type.getFullyQualifiedName() + ".class";
		if (type.hasGenerics())
			code += getGenericsReference(type);

		return code + ")";
	}
	
	/**
	 * Generate the reference to a class that is defined by a {@link GenericType} in a recupe. To be used with the {@code getClassReference}.
	 * 
	 * @param type {@link GenericType} representing the type to be used in the recipe
	 * @return {@link String} code to create the {@link ClassType} reference for the generic
	 */
	private static String getGenericTypeReference(GenericType type) {
		if (type instanceof SimpleExplicitGeneric sGen)
			return getClassReference(sGen.asClassType());
		else if (type instanceof SimpleWildcardGeneric)
			return getClassReference(TypeFactory.createClassType(Object.class));
		else if (type instanceof CompoundExtendsGeneric cGen) {
			List<ClassType> parents = cGen.getParents();
			if (parents.size() > 1)
				throw new DefinitionException("Cannot proceed if the generic extends more than one class " + type.getCodeName());
			if (parents.isEmpty())
				return getClassReference(TypeFactory.createClassType(Object.class));
			return getClassReference(parents.get(0));
		}
		
		throw new DefinitionException("Unable to determine bean type for " + type.getCodeName());
	}

	/**
	 * Helper for generating the code to prepare the generics references to include in a {@link ClassType} reference for the specified class. This is to be used in conjunction with
	 * {@code getClassTypeReference} above.
	 * 
	 * @param cls {@link ClassType} whose generics are to be included in the code
	 * @return {@link String} the code for adding the generics of the specified {@link ClassType} in recipe code
	 */
	private static String getGenericsReference(ClassType cls) {
		StringBuilder genericParams = new StringBuilder();
		for (GenericType g : cls.getGenerics()) {
			ClassType gType;
			try {
				gType = g.asClassType();
			} catch (DefinitionException ex) {
				// Ignore, just means we stick with the Object type
				gType = TypeFactory.createClassType(Object.class);
			}
			genericParams.append(", ");
			genericParams.append(GenericFactory.class.getName());
			genericParams.append(".create(");
			genericParams.append(getClassTypeReference(gType));
			genericParams.append(")");
		}

		return genericParams.toString();
	}

	/**
	 * Check to see whether reflection is required to access the specified element from the creator
	 * 
	 * @param creatorType ClassType of the creator from where the element is to be accessed
	 * @param element     {@link JContainedType} that is to be accessed
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
