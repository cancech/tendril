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

/**
 * {@link Type} representing a generic type that can be applied to a class.
 */
public abstract class GenericType implements Type {

    /** The name of the generic (as in how it is referred to internally) */
    private final String name;

    /**
     * CTOR
     * 
     * @param name {@link String} the name to apply to the generic
     */
    GenericType(String name) {
        this.name = name;
    }

    /**
     * Generate the code which is to be used for this generic when it is used to define a class or method. For example:
     * <ul>
     *      <li>public class MyClass&lt;DEFINITION&gt;</li>
     *      <li>public &lt;DEFINITION&gt; void myMethod()</li>
     * </ul>
     * 
     * In order for the generic to be usable to define the generic of a class, it <b>cannot</b>:
     * <ul>
     *      <li>be a wild-card (public class MyClass&lt;?&gt; and public &lt;?&gt; void myMethod() are not valid)</li>
     *      <li>resolve to an explicit class (public class MyClass&lt;MyOtherClass&gt; and public &lt;MyOtherClass&gt; void myMethod() are not valid)</li>
     * </ul>
     * 
     * @return {@link String} the code for the generic when used to define a class or method
     */
    public String generateDefinition() {
        return getSimpleName();
    }

    /**
     * Generate the code which is to be used for this generic when it is used to apply it to a variable. For example:
     * <ul>
     *      <li>public class MyClass extends MyOtherClass&lt;APPLICATION&gt;</li>
     *      <li>private MyOtherClass&lt;APPLICATION&gt; field</li>
     *      <li>public void myMethod(MyOtherClass&lt;APPLICATION&gt; param)</li>
     * </ul>
     * 
     * When applying the generic to variable, it can be either a wild-card (?), an elsewhere defined generic (i.e.: &lt;T&gt;) or an explicit
     * class (i.e.: &lt;MyClass&gt;).
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
        // Intentionally left blank, few will actually need this
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
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "<" + getSimpleName() + ">";
    }
}
