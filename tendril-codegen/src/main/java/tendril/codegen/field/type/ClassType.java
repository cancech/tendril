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

import java.util.Set;

import tendril.codegen.DefinitionException;
import tendril.codegen.JGeneric;
import tendril.codegen.field.value.JValue;
import tendril.codegen.field.value.JValueFactory;

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
     * @param klass {@link Class} the specific class definition which is being described
     */
    public ClassType(Class<?> klass) {
        this(klass.getPackageName(), klass.getSimpleName());
    }

    /**
     * CTOR
     * 
     * @param fullyQualifiedName {@link String} the fully qualified name of the defined class
     */
    public ClassType(String fullyQualifiedName) {
        int lastDot = fullyQualifiedName.lastIndexOf('.');
        if (lastDot <= 0)
            throw new DefinitionException(this, "Invalid fully qualified class \"" + fullyQualifiedName + "\". Hint: default package is not supported");

        this.packageName = fullyQualifiedName.substring(0, lastDot);
        this.className = fullyQualifiedName.substring(lastDot + 1);
    }

    /**
     * CTOR
     * 
     * @param packageName {@link String} the name of the package where the defined class lives
     * @param className   {@link String} the name of the class itself
     */
    public ClassType(String packageName, String className) {
        if (packageName == null || packageName.isBlank())
            throw new DefinitionException(this, "Invalid package \"" + packageName + "\" - valid (non default) package is required");

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
     * Derive a new class definition from the current one, such that the specified suffix is applied to the generated class name
     * 
     * @param classSuffix {@link String} the suffix to apply to generate a new class definition
     * @return {@link ClassType} of the new class
     */
    public ClassType generateFromClassSuffix(String classSuffix) {
        return new ClassType(getPackageName(), getClassName() + classSuffix);
    }

    /**
     * @see tendril.codegen.field.type.Type#isAssignableFrom(tendril.codegen.field.type.Type)
     */
    @Override
    public boolean isAssignableFrom(Type other) {
        return equals(other);
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ClassType))
            return false;

        ClassType other = (ClassType) obj;
        return this.packageName.equals(other.packageName) && this.className.equals(other.className);
    }

    /**
     * @see tendril.codegen.field.type.Type#isTypeOf(java.lang.Object)
     */
    @Override
    public boolean isTypeOf(Object value) {
        return value.getClass().getName().equals(getFullyQualifiedName());
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
