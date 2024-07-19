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

import tendril.codegen.classes.ImportElement;
import tendril.codegen.field.value.JValue;
import tendril.codegen.field.value.JValueFactory;

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
     * @see tendril.codegen.field.type.Type#isAssignableFrom(tendril.codegen.field.type.Type)
     */
    @SuppressWarnings("unlikely-arg-type")
    @Override
    public boolean isAssignableFrom(Type other) {
        return super.equals(other);
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
        return getClassName();
    }

    /**
     * @see tendril.codegen.field.type.Type#asValue(java.lang.Object)
     */
    @Override
    public JValue<?, ?> asValue(Object value) {
        if (!isTypeOf(value))
            throw new IllegalArgumentException("Invalid object provided: require " + getFullyQualifiedName() + " but received " + value.getClass().getName());
        
        return JValueFactory.create(value);
    }
}
