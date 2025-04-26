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

import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.WildcardType;

import tendril.codegen.DefinitionException;
import tendril.codegen.generics.GenericFactory;

/**
 * Factory to facilitate the creation of {@link Type} instances
 */
public abstract class TypeFactory {

    /**
     * Hidden CTOR
     */
    private TypeFactory() {
    }

    /**
     * Creates a {@link Type} for the given {@link TypeMirror} definition
     * 
     * @param mirror {@link TypeMirror} defining the data type
     * @return {@link Type} representing the data type
     */
    public static Type create(TypeMirror mirror) {
        TypeKind kind = mirror.getKind();
        if (kind == TypeKind.VOID)
            return VoidType.INSTANCE;
        if (kind.isPrimitive())
            return PrimitiveType.valueOf(kind.toString());
        if (kind == TypeKind.DECLARED)
            return asClassType(mirror);
        if (kind == TypeKind.ARRAY)
            return new ArrayType<Type>(create(((javax.lang.model.type.ArrayType) mirror).getComponentType()));
        if (kind == TypeKind.WILDCARD)
            return asWildcardType(mirror);

        throw new DefinitionException("Unknown type: " + mirror + "[" + kind + "]");
    }
    
    /**
     * Convert the {@link TypeMirror} to a {@link ClassType}
     * 
     * @param mirror {@link TypeMirror} representing an object
     * @return {@link Type} representing the class
     */
    private static Type asClassType(TypeMirror mirror) {
        DeclaredType decl = (DeclaredType) mirror;
        ClassType type = new ClassType(decl.asElement().toString());
        decl.getTypeArguments().forEach(gen -> type.addGeneric(GenericFactory.create((ClassType) create(gen))));
        return type;
    }
    
    /**
     * Convert the {@link TypeMirror} to {@link ClassType}
     * 
     * @param mirror {@link TypeMirror} representing a Wildcard (i.e.: generic with ?)
     * @return {@link Type} presenting the class (i.e.: the "concrete" type of the wildcard)
     */
    private static Type asWildcardType(TypeMirror mirror) {
        WildcardType wild = (WildcardType) mirror;
        // If it's an extends wildcard
        TypeMirror genMirror = wild.getExtendsBound();
        if (genMirror != null)
            return create(genMirror);
        // If it's a super wildcard
        genMirror = wild.getSuperBound();
        if (genMirror != null)
            return create(genMirror);
        
        // If it's a pure wildcard, then default to Object
        return new ClassType(Object.class);
    }
    
    /**
     * Create a {@link Type} from a defining {@link Class}
     * 
     * @param klass {@link Class} defining the data type
     * @return {@link Type} representing the data type
     */
    public static Type create(Class<?> klass) {
        if (klass.isArray())
            return new ArrayType<Type>(create(klass.getComponentType()));
        try {
            return PrimitiveType.from(klass);
        } catch (Exception e) {
            return new ClassType(klass);
        }
    }
}
