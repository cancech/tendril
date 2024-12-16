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

import tendril.codegen.Utilities;
import tendril.codegen.classes.JClass;
import tendril.codegen.field.type.ClassType;

/**
 * Factory for the creation of {@link GenericType}s
 */
public class GenericFactory {

    /**
     * Hidden CTOR
     */
    private GenericFactory() {
    }
    
    /**
     * Creates a generic type that resolves to a name, the most "traditional" generic type (i.e.: &lt;T&gt;). Note that
     * the name must be considered to be valid by Java standards.
     * 
     * @param name {@link String} the name to apply to the generic type
     * 
     * @return {@link GenericType}
     */
    public static GenericType create(String name) {
        Utilities.throwIfNotValidIdentifier(name);
        return new SimpleGeneric(name);
    }
    
    /**
     * Creates a generic that resolves to a specific class (i.e.: &lt;MyClass&gt;). Note that this can only be used when
     * applying the generic to an elsewhere defined element (i.e.: for variables, parameters, or parent class/interfaces).
     * <p>
     * Note that this {@link GenericType} <b>cannot</b> have nested generic of its own (i.e.: &lt;MyClass&gt; but not &lt;MyClass&lt;T&gt;&gt;)
     * </p>
     * @param type {@link ClassType} to apply to the generic
     * 
     * @return {@link GenericType}
     */
    public static GenericType create(ClassType type) {
        return new SimpleClassTypeGeneric(type);
    }
    
    /**
     * Creates a generic that resolves to a specific class (i.e.: &lt;MyClass&gt;). Note that this can only be used when
     * applying the generic to an elsewhere defined element (i.e.: for variables, parameters, or parent class/interfaces).
     * <p>
     * Note that this {@link GenericType} <b>can</b> have nested generic of its own (i.e.: &lt;MyClass&gt; but not &lt;MyClass&lt;T&gt;&gt;)
     * </p>
     * @param type {@link JClass} to apply to the generic
     * 
     * @return {@link GenericType}
     */
    public static GenericType create(JClass type) {
        return new SimpleJClassGeneric(type);
    }
    
    /**
     * Creates a generic that resolves to a wildcard (i.e.: &lt;?&gt;). Note that this can only be used when
     * applying the generic to an elsewhere defined element (i.e.: for variables, parameters, or parent class/interfaces).
     * 
     * @return {@link GenericType}
     */
    public static GenericType createWildcard() {
        return new SimpleWildcardGeneric();
    }
}
