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

import tendril.codegen.classes.method.JMethod;
import tendril.codegen.field.type.Type;
import tendril.codegen.field.value.JValue;

/**
 * Used to build methods, allowing for their wide permutation possibilities to be accounted for in a relatively straightforward manner. The method is by default public and with no implementation.
 * Error checking is performed to ensure that the method is properly defined such that it can be considered valid for the encompassing class.
 * 
 * Note, if a valid {@link ClassBuilder} is provided, methods can only be created via {@code finish()} and are automatically added to the encompassing class.
 * Note, if an invalid {@link ClassBuilder} is provided (null), methods can only be created via {@code build()}.
 * Note, no error checking or other validation is performed on the specified code/implementation of the method.
 * 
 * @param <RETURN_TYPE> extends {@link Type} indicating what the method is to return
 */
public abstract class MethodBuilder<RETURN_TYPE extends Type> extends NestedClassMethodElementBuilder<RETURN_TYPE, JMethod<RETURN_TYPE>, MethodBuilder<RETURN_TYPE>> {
    
    /**
     * CTOR
     * 
     * @param classBuilder {@link ClassBuilder} building the class to which the method belongs
     * @param name         {@link String} the name of the method
     */
    protected MethodBuilder(ClassBuilder classBuilder, String name) {
        super(classBuilder, name);
    }

    /**
     * Add a default value to the method. Note this only works if the method is being applied to an annotation.
     * 
     * @param value {@link JValue} to apply as the default value
     * @return {@link MethodBuilder}
     */
    public MethodBuilder<RETURN_TYPE> setDefaultValue(JValue<RETURN_TYPE, ?> value) {
        throw new IllegalArgumentException("Only annotations support default values for methods values");
    }
    
    /**
     * @see tendril.codegen.classes.NestedClassElementBuilder#addToClass(tendril.codegen.classes.ClassBuilder, tendril.codegen.field.JVisibleType)
     */
    @Override
    protected void addToClass(ClassBuilder classBuilder, JMethod<RETURN_TYPE> toAdd) {
        classBuilder.add(toAdd);
    }
}
