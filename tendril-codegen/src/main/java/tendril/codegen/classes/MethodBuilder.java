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

import tendril.codegen.Utilities;
import tendril.codegen.classes.method.JMethod;
import tendril.codegen.field.JParameter;
import tendril.codegen.field.type.Type;
import tendril.codegen.field.value.JValue;

/**
 * Used to build methods, allowing for their wide permutation possibilities to be accounted for in a relatively straightforward manner. The method is by default public and with no implementation.
 * Error checking is performed to ensure that the method is properly defined such that it can be considered valid for the encompassing class.
 * 
 * Note, no error checking or other validation is performed on the specified code/implementation of the method.
 * 
 * @param <RETURN_TYPE> extends {@link Type} indicating what the method is to return
 */
public abstract class MethodBuilder<RETURN_TYPE extends Type> extends NestedClassElementBuilder<RETURN_TYPE, JMethod<RETURN_TYPE>, MethodBuilder<RETURN_TYPE>> {
    /** List of individual lines of code that comprise the method implementation. If null, no implementation is present */
    protected List<String> linesOfCode = null;
    /** List of parameters the method is to take */
    protected final List<JParameter<?>> parameters = new ArrayList<>();
    
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
     * Add a parameter to the method
     * 
     * @param parameter {@link JParameter} to add
     * @return {@link MethodBuilder}
     */
    public MethodBuilder<RETURN_TYPE> addParameter(JParameter<?> parameter) {
        parameters.add(parameter);
        return this;
    }

    /**
     * Mark the method as one with an empty (blank) implementation. This is distinct from a method that has no implementation. Any implementation that may be present will be destroyed.
     * 
     * @return {@link MethodBuilder}
     */
    public MethodBuilder<RETURN_TYPE> emptyImplementation() {
        linesOfCode = new ArrayList<>();
        return this;
    }

    /**
     * Add lines of code. These lines are appended to the end of the existing stored implementation.
     * 
     * @param lines {@link String}... lines to append
     * @return {@link MethodBuilder}
     */
    public MethodBuilder<RETURN_TYPE> addCode(String... lines) {
        if (linesOfCode == null)
            linesOfCode = new ArrayList<>();

        for (String s : lines)
            linesOfCode.add(s);

        return this;
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
     * @see tendril.codegen.BaseBuilder#validate()
     */
    @Override
    protected void validate() {
        super.validate();
        Utilities.throwIfNotValidIdentifier(name);
    }

    /**
     * @see tendril.codegen.BaseBuilder#applyDetails(tendril.codegen.JBase)
     */
    @Override
    protected JMethod<RETURN_TYPE> applyDetails(JMethod<RETURN_TYPE> method) {
        super.applyDetails(method);
        for (JParameter<?> param : parameters)
            method.addParameter(param);

        return method;
    }
    
    /**
     * @see tendril.codegen.classes.NestedClassElementBuilder#addToClass(tendril.codegen.classes.ClassBuilder, tendril.codegen.field.JVisibleType)
     */
    @Override
    protected void addToClass(ClassBuilder classBuilder, JMethod<RETURN_TYPE> toAdd) {
        classBuilder.add(toAdd);
    }

    /**
     * Check if the class has any code/implementation available.
     * 
     * @return true if code/implementation is present
     */
    protected boolean hasCode() {
        return linesOfCode != null;
    }
}
