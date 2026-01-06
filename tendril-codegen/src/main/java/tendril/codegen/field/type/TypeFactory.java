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
import tendril.codegen.generics.GenericType;

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
        if (kind == TypeKind.TYPEVAR)
            return GenericFactory.create(mirror.toString());

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
        ClassType type = createClassType(decl.asElement().toString());
        decl.getTypeArguments().forEach(gen -> type.addGeneric(GenericFactory.create(create(gen))));
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
        return createClassType(Object.class);
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
            return createClassType(klass);
        }
    }
    
    /**
     * Create a {@link ClassType} from the specified {@link Class}
     * 
     * @param klass {@link Class} to use as the basis for the {@link ClassType}
     * @param generics {@link GenericType}... listing what generics to apply to the {@link ClassType}
     * @return {@link ClassType} for the {@link Class}
     */
    public static ClassType createClassType(Class<?> klass, GenericType... generics) {
    	return createClassType(klass.getPackageName(), klass.getSimpleName(), generics);
    }
    
    /**
     * Create a {@link ClassType} from the specified fully qualified class name
     * 
     * @param fullyQualifiedName {@link String} of the class
     * @param generics {@link GenericType}... listing what generics to apply to the {@link ClassType}
     * @return {@link ClassType} for the fully qualified class name
     */
    public static ClassType createClassType(String fullyQualifiedName, GenericType... generics) {
        int lastDot = fullyQualifiedName.lastIndexOf('.');
        if (lastDot <= 0)
            throw new DefinitionException(fullyQualifiedName, "Invalid fully qualified class \"" + fullyQualifiedName + "\". Hint: default package is not supported");

        String packageName = fullyQualifiedName.substring(0, lastDot);
        String className = fullyQualifiedName.substring(lastDot + 1);
        return createClassType(packageName, className, generics);
    }
    
    /**
     * Create the {@link ClassType} for the class details
     * 
     * @param packageName {@link String} the name of the package in which the class appears
     * @param className {@link String} the name of the class
     * @param generics {@link GenericType}... listing what generics to apply to the {@link ClassType}
     * @return {@link ClassType} for the class details
     */
    public static ClassType createClassType(String packageName, String className, GenericType... generics) {
        if (packageName == null || packageName.isBlank())
            throw new DefinitionException(className, "Invalid package \"" + packageName + "\" - valid (non default) package is required");
        
        // Create the type and apply the generics
        ClassType type = new ClassType(packageName, className);
        for (GenericType g: generics)
        	type.addGeneric(g);
        
        return type;
    }
    
    /**
     * Derive a new class definition from an existing one, such that the specified suffix is applied to the generated class name
     * 
     * @param original {@link ClassType} the existing {@link ClassType} to which to append the suffix
     * @param classSuffix {@link String} the suffix to apply to generate a new class definition
     * @return {@link ClassType} of the new class
     */
    public static ClassType createClassType(ClassType original, String classSuffix) {
    	ClassType newClass = createClassType(original.getPackageName(), original.getClassName() + classSuffix);
    	for (GenericType g: original.getGenerics()) {
    		newClass.addGeneric(g);
    	}
    	
    	return newClass;
    }
}
