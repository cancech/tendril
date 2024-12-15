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
package tendril.codegen.generics;

import java.util.Set;

import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.Type;
import tendril.codegen.field.value.JValue;
import tendril.codegen.field.value.JValueFactory;

/**
 * {@link Type} representing a generic type that can be applied to a class.
 */
public class GenericType implements Type {

    /** The name of the generic (as in how it is referred to internally) */
    private final String name;
    /** Boolean true if the generic is a wild-card (i.e.: not a explicitly defined type) */
    private boolean isWildcard = false;
    /** The type of class that the generic explicitly resolves to (i.e: the exact class applied to an elsewhere defined generic */
    private ClassType classType = null;

    /**
     * CTOR - a "no name" generic which can resolve to anything (i.e.: <?>).
     */
    GenericType() {
        this("?");
        isWildcard = true;
    }

    /**
     * CTOR - a generic with an explicit name but which does not have an explicit type (i.e.: <T>)
     * 
     * @param name {@link String} the name to apply to the generic
     */
    GenericType(String name) {
        this.name = name;
    }

    /**
     * CTOR - a generic which explicitly resolves to the specifies class (i.e.: <MyClass>)
     * 
     * @param type {@link ClassType} of the class to resolve to
     */
    GenericType(ClassType type) {
        this(type.getSimpleName());
        this.classType = type;
    }
    
    /**
     * Check whether the generic is (contains) a wild-card
     * 
     * @return boolean true if it is
     */
    protected boolean isWildcard() {
        return isWildcard;
    }
    
    /**
     * Generate the code which is to be used for this generic when it is used to define a class or method. For example:
     * <ul>
     *      <li>public class MyClass<DEFINITION></li>
     *      <li>public <DEFINITION> void myMethod()</li>
     * </ul>
     * 
     * In order for the generic to be usable to define the generic of a class, it <b>cannot</b>:
     * <ul>
     *      <li>be a wild-card (public class MyClass<?> and public <?> void myMethod() are not valid)</li>
     *      <li>resolve to an explicit class (public class MyClass<MyOtherClass> and public <MyOtherClass> void myMethod() are not valid)</li>
     * </ul>
     * 
     * @return {@link String} the code for the generic when used to define a class or method
     */
    public String generateDefinition() {
        if (isWildcard || classType != null)
            throw new IllegalArgumentException("A wildcard generic cannot be used in a definition");
        return getSimpleName();
    }

    /**
     * Generate the code which is to be used for this generic when it is used to apply it to a variable. For example:
     * <ul>
     *      <li>public class MyClass extends MyOtherClass<APPLICATION></li>
     *      <li>private MyOtherClass<APPLICATION> field</li>
     *      <li>public void myMethod(MyOtherClass<APPLICATION> param)</li>
     * </ul>
     * 
     * When applying the generic to variable, it can be either a wild-card (?), an elsewhere defined generic (i.e.: <T>) or an explicit
     * class (i.e.: <MyClass>).
     * 
     * @return {@link String} the code for the generic when used to apply it to a variable
     */
    public String generateApplication() {
        return getSimpleName();
    }

    /**
     * @see tendril.codegen.field.type.Importable#registerImport(java.util.Set)
     */
    @Override
    public void registerImport(Set<ClassType> classImports) {
        if (classType != null)
            classImports.add(classType);
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
        return name;
    }

    /**
     * @see tendril.codegen.field.type.Type#isTypeOf(java.lang.Object)
     */
    @Override
    public boolean isTypeOf(Object value) {
        if (classType == null)
            return true;

        return classType.isTypeOf(value);
    }

    /**
     * @see tendril.codegen.field.type.Type#isAssignableFrom(tendril.codegen.field.type.Type)
     */
    @Override
    public boolean isAssignableFrom(Type other) {
        if (classType == null)
            return true;

        return classType.isAssignableFrom(other);
    }

    /**
     * @see tendril.codegen.field.type.Type#asValue(java.lang.Object)
     */
    @Override
    public JValue<?, ?> asValue(Object value) {
        if (!isTypeOf(value))
            throw new IllegalArgumentException("Invalid object provided: require " + name + " but received " + value.getClass().getName());

        return JValueFactory.create(value);
    }
}
