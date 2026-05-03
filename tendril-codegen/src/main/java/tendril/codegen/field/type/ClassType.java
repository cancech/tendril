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
import java.util.Set;

import tendril.codegen.DefinitionException;
import tendril.codegen.JGeneric;
import tendril.codegen.field.value.JValue;
import tendril.codegen.field.value.JValueFactory;
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
     * Get the name of the class (or equivalent)
     * 
     * @return {@link String} name
     */
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
    			// If they are the same class, then make sure that any generics match
    			List<GenericType> myGenerics = getGenerics();
    			List<GenericType> otherGenerics = otherClass.getGenerics();
    			if (myGenerics.size() != otherGenerics.size())
    				return false;
    			for (int i = 0; i < myGenerics.size(); i++) {
    				if (!myGenerics.get(i).asClassType().equals(otherGenerics.get(i).asClassType()))
    					return false;
    			}
    			
    			// At this point no mismatch in the generics, so the two types are assignable
    			return true;
    		} else
    			return false;
    	} catch (Exception ex) {
    		// Cannot do an in-depth comparison, so perform the most basic surface level check
    		return equals(other);
    	}
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
    			return getGenerics().get(0).isAssignableFrom(otherClassType.getGenerics().get(0).asClassType());
    		}
			return getGenerics().get(0).isAssignableFrom(otherClassType);
    	}

    	// Otherwise, just simply check if the value is of the correct type
    	return value.getClass().getName().equals(getFullyQualifiedName());
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
     * @see tendril.codegen.field.type.Importable#registerImport(java.util.Set)
     */
    @Override
    public void registerImport(Set<ClassType> classImports) {
        classImports.add(this);
        registerGenerics(classImports);
    }

    /**
     * @see tendril.codegen.field.type.Type#isVoid()
     */
    @Override
    public boolean isVoid() {
        return false;
    }

    /**
     * @see tendril.codegen.field.type.Type#getSimpleName()
     */
    @Override
    public String getSimpleName() {
        return getClassName() + getGenericsApplicationKeyword(false);
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
