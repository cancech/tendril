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

import java.util.Arrays;

import tendril.codegen.DefinitionException;
import tendril.codegen.Utilities;
import tendril.codegen.classes.JClass;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.Type;

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
     * Creates a generic that resolves to a specific {@link Type} (i.e.: Class or Generic). Note that this can only be used when
     * applying the generic to an elsewhere defined element (i.e.: for variables, parameters, or parent class/interfaces).
     * <p>
     * Note that this {@link GenericType} <b>cannot</b> have nested generic of its own (i.e.: &lt;MyClass&gt; but not &lt;MyClass&lt;T&gt;&gt;)
     * </p>
     * @param type {@link Type} to apply to the generic
     * 
     * @return {@link GenericType}
     */
    public static GenericType create(Type type) {
        if (type instanceof ClassType)
            return new SimpleExplicitGeneric((ClassType) type);
        else if (type instanceof GenericType)
            return (GenericType)type;
        
        throw new DefinitionException(GenericType.class.getSimpleName() + " can only be used with " + ClassType.class.getSimpleName() + " or " +
                GenericType.class.getSimpleName() + ". " + type.getSimpleName() + " cannot be used.");
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
        return new SimpleExplicitGeneric(type.getType());
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
    
    /**
     * Creates a generic that resolves to a name and which extends one (or more) parent classes/interfaces
     * 
     * @param name {@link String} the name of the generic type
     * @param extended {@link ClassType}... listing of the types that the generic extends (must be at least one)
     * 
     * @return {@link GenericType}
     */
    public static GenericType createExtends(String name, ClassType...extended) {
        Utilities.throwIfNotValidIdentifier(name);
        if (extended.length == 0)
            throw new DefinitionException("Generic must extend at least one class");
        
        return new CompoundExtendsGeneric(name, Arrays.asList(extended));
    }
    
    /**
     * Creates a generic that resolves to a name and which extends one (or more) parent classes/interfaces
     * 
     * @param name {@link String} the name of the generic type
     * @param extended {@link JClass}... listing of the types that the generic extends (must be at least one)
     * 
     * @return {@link GenericType}
     */
    public static GenericType createExtends(String name, JClass...extended) {
        return createExtends(name, asType(extended));
    }
    
    /**
     * Helper which converts a JClass array to ClassType array.
     * 
     * @param classes {@link JClass}[]
     * @return {@link ClassType}[]
     */
    private static ClassType[] asType(JClass[] classes) {
        ClassType[] types = new ClassType[classes.length];
        for (int i = 0; i < classes.length; i++)
            types[i] = classes[i].getType();
        
        return types;
    }
    
    /**
     * Creates a generic that is defined as a "super" of another class/interface
     * 
     * @param supered {@link ClassType} this generic references as its "super"
     * 
     * @return {@link GenericType}
     */
    public static GenericType createSuper(ClassType supered) {
        return new CompoundSuperGeneric(supered);
    }
    
    /**
     * Creates a generic that is defined as a "super" of another class/interface
     * 
     * @param supered {@link JClass} this generic references as its "super"
     * 
     * @return {@link GenericType}
     */
    public static GenericType createSuper(JClass supered) {
        return createSuper(supered.getType());
    }
    
    /**
     * Creates a wildcard generic that extends one (or more) parent classes/interfaces
     * 
     * @param extended {@link ClassType}... listing of the types that the generic extends (must be at least one)
     * 
     * @return {@link GenericType}
     */
    public static GenericType createWildcardExtends(ClassType...extended) {
        if (extended.length == 0)
            throw new DefinitionException("Generic must extend at least one class");
        
        return new CompoundExtendsWildcardGeneric(Arrays.asList(extended));
    }
    
    /**
     * Creates a wildcard generic that extends one (or more) parent classes/interfaces
     * 
     * @param extended {@link JClass}... listing of the types that the generic extends (must be at least one)
     * 
     * @return {@link GenericType}
     */
    public static GenericType createWildcardExtends(JClass...extended) {
        return createWildcardExtends(asType(extended));
    }
}
