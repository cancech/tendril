/*
 * Copyright 2024 Jaroslav Bosak
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://opensource.org/license/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tendril.codegen.field.type;

import java.util.List;

import tendril.codegen.DefinitionException;
import tendril.codegen.JGeneric;
import tendril.codegen.field.value.JValue;
import tendril.codegen.field.value.JValueFactory;
import tendril.codegen.generics.GenericFactory;
import tendril.codegen.generics.GenericType;

/**
 * Represents a class or other declared element. Incorporates all of the necessary details for the element, including the ability to "generate" new elements that derive from it.
 */
public class ClassType extends JGeneric implements Type {

	/** The name of the package where the importable item lives */
	private final String packageName;
	/** The name of the importable element (class or equivalent) */
	private final String className;

	/**
	 * CTOR
	 * 
	 * @param packageName {@link String} the name of the package where the defined class lives
	 * @param className   {@link String} the name of the class itself
	 */
	ClassType(String packageName, String className) {
		this.packageName = packageName;
		this.className = className;
	}

	/**
	 * Get the name of the package
	 * 
	 * @return {@link String} name
	 */
	public String getPackageName() {
		return packageName;
	}

	/**
	 * @see tendril.codegen.field.type.Type#getClassName()
	 */
	@Override
	public String getClassName() {
		return className;
	}

	/**
	 * Get the fully qualified name of the class (or equivalent)
	 * 
	 * @return {@link String} fully qualified name
	 */
	public String getFullyQualifiedName() {
		return packageName + "." + className;
	}

	/**
	 * @see tendril.codegen.field.type.Type#isAssignableFrom(tendril.codegen.field.type.Type)
	 */
	@Override
	public boolean isAssignableFrom(Type other) {
		// First off, if the other is the same type, then it must be assignable
		if (isTypeOf(other))
			return true;

		try {
			// If not explicitly the same type, then check whether the other type references a class
			if (other instanceof ClassType otherClass && getDefinedClass().isAssignableFrom(otherClass.getDefinedClass())) {
				return compareGenerics(otherClass);
			} else
				return false;
		} catch (Exception ex) {
			// Cannot do an in-depth comparison, so perform the most basic surface level check
			return equals(other);
		}
	}

	/**
	 * Compare the generics between this class and the other to make sure that they're the same
	 * 
	 * @param otherClass {@link ClassType} whose generics to compare against
	 * @return boolean true if the generics are the same
	 */
	private boolean compareGenerics(ClassType otherClass) {
		// If they are the same class, then make sure that any generics match
		List<GenericType> myGenerics = getGenerics();
		List<GenericType> otherGenerics = otherClass.getGenerics();
		if (myGenerics.size() != otherGenerics.size())
			return false;
		for (int i = 0; i < myGenerics.size(); i++) {
			if (!myGenerics.get(i).asClassType().isAssignableFrom(otherGenerics.get(i).asClassType()))
				return false;
		}

		// At this point no mismatch in the generics, so the two types are assignable
		return true;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ClassType other)
			return this.packageName.equals(other.packageName) && this.className.equals(other.className);

		return false;
	}

	/**
	 * @see tendril.codegen.field.type.Type#isTypeOf(java.lang.Object)
	 */
	@Override
	public boolean isTypeOf(Object value) {
		// If this instance is a class reference (i.e.: Class<Type>)
		if (isClassClass() && value instanceof ClassType otherClassType) {
			if (otherClassType.isClassClass()) {
				List<GenericType> otherGenerics = otherClassType.getGenerics();
				if (getGenerics().isEmpty() || otherGenerics.isEmpty())
					// Technically true, as this would throw a warning
					return true;
				return getGenerics().get(0).isAssignableFrom(otherClassType.getGenerics().get(0).asClassType());
			}
			return getGenerics().get(0).isAssignableFrom(otherClassType);
		}

		// If it's the same class then it must be assignable
		if (value instanceof ClassType otherClassType) {
			if (this.equals(otherClassType) && compareGenerics(otherClassType))
				return true;
		} else if (value.getClass().getName().equals(getFullyQualifiedName()))
			return true;

		// Otherwise check if it the classes are assignable
		try {
			// If the other instance is a Class, then check not just the class itself, but also its inheritance hierarchy
			if (value instanceof ClassType otherClassType)
				return checkIfInherited(getDefinedClass(), otherClassType);
			return getDefinedClass().isAssignableFrom(value.getClass());
		} catch (ClassNotFoundException ex) {
			// Failure to load one class (or the other) means that we cannot definitively way whether the two are assignable.
			// Assume that they are and leave it up to the compiler to figure out for sure.
			return true;
		}
	}

	/**
	 * Check if the other class is "the same" as this class at any level of its inheritance hierarchy. This check not just simply whether the class can be assigned, but also whether or not the
	 * generics or assignable.
	 * 
	 * @param me {@link Class} that this instance is representing
	 * @param other {@link ClassType} to check if it inherits from this class
	 * @return boolean {@code true} if a valid inheritance is present (including generics)
	 * @throws ClassNotFoundException if there is a failure the load a {@link Class}
	 */
	private boolean checkIfInherited(Class<?> me, ClassType other) throws ClassNotFoundException {
		Class<?> otherClass = other.getDefinedClass();
		// If the other class is not outright assignable, then there is nothing more to be done
		if (!me.isAssignableFrom(otherClass))
			return false;

		// If the generics of the two match, then assume that they are the same
		if (compareGenerics(other))
			return true;

		try {
			// Otherwise check the super class
			java.lang.reflect.Type superT = otherClass.getGenericSuperclass();
			if (superT != null && checkIfInherited(me, asClassType(superT)))
				return true;
		} catch (DefinitionException e) {
			// Something went wrong.. ignore
		}
		// Otherwise check all inherited interfaces
		for (java.lang.reflect.Type ifaceT : otherClass.getGenericInterfaces()) {
			try {
				if (ifaceT != null && checkIfInherited(me, asClassType(ifaceT)))
					return true;
			} catch (DefinitionException e) {
				// Something went wrong.. ignore
			}
		}

		// Otherwise not a match
		return false;
	}

	/**
	 * Convert a reflected {@link java.lang.reflect.Type} to {@link ClassType}
	 * @param t {@link java.lang.reflect.Type} to convert
	 * @return the resulting {@link ClassType}
	 */
	private ClassType asClassType(java.lang.reflect.Type t) {
		String[] name = t.getTypeName().split("<");
		// Anything prior to the < is the classname
		ClassType clsType = TypeFactory.createClassType(name[0]);

		// Anything after are the generics assigned to it
		if (name.length > 1) {
			for (String g : name[1].split(">")[0].split(","))
				clsType.addGeneric(GenericFactory.create(TypeFactory.createClassType(g.strip())));
		}

		return clsType;
	}

	/**
	 * Helper which checks of the ClassType represents a class object (i.e.: Class<Type>)
	 * 
	 * @return boolean true if it does
	 */
	private boolean isClassClass() {
		return Class.class.getPackageName().equals(packageName) && Class.class.getSimpleName().equals(className);
	}

	/**
	 * @see tendril.codegen.field.type.Type#isVoid()
	 */
	@Override
	public boolean isVoid() {
		return false;
	}

	/**
	 * @see tendril.codegen.field.type.Type#getCodeName()
	 */
	@Override
	public String getCodeName() {
		return getFullyQualifiedName() + getGenericsApplicationKeyword(false);
	}

	/**
	 * @see tendril.codegen.field.type.Type#asValue(java.lang.Object)
	 */
	@Override
	public JValue<?, ?> asValue(Object value) {
		if (!isTypeOf(value))
			throw new DefinitionException(this, "Invalid object provided: require " + getFullyQualifiedName() + " but received " + value.getClass().getName());

		return JValueFactory.create(value);
	}

	/**
	 * Get the {@link Class} for the defined element.
	 * 
	 * @return {@link Class}
	 * @throws ClassNotFoundException if no {@link Class} exists
	 */
	public Class<?> getDefinedClass() throws ClassNotFoundException {
		return Class.forName(getFullyQualifiedName());
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return packageName.hashCode() + className.hashCode();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getFullyQualifiedName();
	}

	/**
	 * @see tendril.codegen.field.type.Type#asClassType()
	 */
	@Override
	public ClassType asClassType() {
		return this;
	}
}
