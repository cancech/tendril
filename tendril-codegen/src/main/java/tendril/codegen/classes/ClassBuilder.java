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
package tendril.codegen.classes;

import java.util.ArrayList;
import java.util.List;

import tendril.codegen.classes.method.JMethod;
import tendril.codegen.field.VisibileTypeBuilder;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.PrimitiveType;
import tendril.codegen.field.type.Type;
import tendril.codegen.field.type.VoidType;

/**
 * The common logic/capability to power builders which create class definitions of different types.
 */
public abstract class ClassBuilder extends VisibileTypeBuilder<ClassType, JClass, ClassBuilder> {

    /** The methods that are to be applied to the class */
    protected final List<JMethod<?>> methods = new ArrayList<>();
    
    /**
     * Get the class builder for creating concrete classes
     * 
     * @param type {@link ClassType} of the class to build
     * @return {@link ClassBuilder}
     */
    public static ClassBuilder forConcreteClass(ClassType type) {
        return new ConcreteClassBuilder(type);
    }
    
    /**
     * Get the class builder for creating abstract classes
     * 
     * @param type {@link ClassType} of the class to build
     * @return {@link ClassBuilder}
     */
    public static ClassBuilder forAbstractClass(ClassType type) {
        return new AbstractClassBuilder(type);
    }
    
    /**
     * Get the class builder for creating interfaces
     * 
     * @param type {@link ClassType} of the interface to build
     * @return {@link ClassBuilder}
     */
    public static ClassBuilder forInterface(ClassType type) {
        return new InterfaceBuilder(type);
    }
    
    /**
     * Get the class builder for creating annotations
     * 
     * @param type {@link ClassType} of the annotation to build
     * @return {@link ClassBuilder}
     */
    public static ClassBuilder forAnnotation(ClassType type) {
        return new AnnotationBuilder(type);
    }

    /**
     * CTOR
     * 
     * @param type {@link ClassType} which has the basic class description included
     */
    ClassBuilder(ClassType type) {
        super(type.getSimpleName());
        setType(type);
    }

    /**
     * @see tendril.codegen.field.VisibileTypeBuilder#applyDetails(tendril.codegen.field.JVisibleType)
     */
    @Override
    protected JClass applyDetails(JClass element) {
        methods.forEach(m -> element.addMethod(m));
        return super.applyDetails(element);
    }

    /**
     * Create a method builder through which to add a new void method to the class.
     * 
     * @param name {@link String} the name of the method
     * @return {@link MethodBuilder} for creating a the method
     */
    public MethodBuilder<VoidType> buildMethod(String name) {
        return createAndCustomizeMethodBuilder(VoidType.INSTANCE, name);
    }

    /**
     * Create a method builder through which to add a new method which returns a primitive to the class.
     * 
     * @param returnType {@link PrimitiveType} representing which primitive to return
     * @param name       {@link String} the name of the method
     * @return {@link MethodBuilder} for creating a the method
     */
    public MethodBuilder<PrimitiveType> buildMethod(PrimitiveType returnType, String name) {
        return createAndCustomizeMethodBuilder(returnType, name);
    }

    /**
     * Create a method builder through which to add a new method which returns a Class object.
     * 
     * @param returnType {@link Class} of the object that is to be returned
     * @param name       {@link String} the name of the method
     * @return {@link MethodBuilder} for creating a the method
     */
    public MethodBuilder<ClassType> buildMethod(Class<?> returnType, String name) {
        return buildMethod(new ClassType(returnType), name);
    }

    /**
     * Create a method builder through which to add a new method which returns a Class object.
     * 
     * @param returnType {@link ClassType} representing the class that is to be returned
     * @param name       {@link String} the name of the method
     * @return {@link MethodBuilder} for creating a the method
     */
    public MethodBuilder<ClassType> buildMethod(ClassType returnType, String name) {
        return createAndCustomizeMethodBuilder(returnType, name);
    }

    /**
     * Helper which triggers the creation of the builder and immediately applies the specified return type to it.
     * 
     * @param <RETURN_TYPE> extending {@link Type} indicating what the nature of the return of the method is
     * @param returnType    RETURN_TYPE what specifically the method is to return
     * @param name          {@link String} the name of the method
     * @return
     */
    private <RETURN_TYPE extends Type> MethodBuilder<RETURN_TYPE> createAndCustomizeMethodBuilder(RETURN_TYPE returnType, String name) {
        MethodBuilder<RETURN_TYPE> builder = createMethodBuilder(name);
        builder.setType(returnType);
        return builder;
    }

    /**
     * Creates the {@link MethodBuilder} instance that is to be used for the purpose of defining methods
     * 
     * @param <RETURN_TYPE> extends {@link Type} indicating what type is to be returned by the method
     * @param name          {@link String} the name of the method to build
     * @return {@link MethodBuilder}
     */
    protected abstract <RETURN_TYPE extends Type> MethodBuilder<RETURN_TYPE> createMethodBuilder(String name);

    /**
     * To be called by the created {@link MethodBuilder} when it {@code finish}es creating the method.
     * 
     * @param method {@link JMethod} that is to be added to the class
     */
    void addMethod(JMethod<?> method) {
        methods.add(method);
    }
}
