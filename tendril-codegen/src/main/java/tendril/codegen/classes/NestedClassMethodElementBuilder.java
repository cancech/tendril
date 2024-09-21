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
import tendril.codegen.classes.method.JAbstractMethodElement;
import tendril.codegen.field.type.Type;

/**
 * Builder for method like elements which are nested within a class
 * 
 * @param <DATA_TYPE> extending {@link Type} represents the type that the produced by the element (i.e.: method return)
 * @param <ELEMENT>   extending {@link JAbstractMethodElement} that the builder produces to represent the element
 * @param <BUILDER>   extending {@link NestedClassMethodElementBuilder} indicating the type of builder to be returned
 */
public abstract class NestedClassMethodElementBuilder<DATA_TYPE extends Type, ELEMENT extends JAbstractMethodElement<DATA_TYPE>, BUILDER extends NestedClassMethodElementBuilder<DATA_TYPE, ELEMENT, BUILDER>>
        extends NestedClassElementBuilder<DATA_TYPE, ELEMENT, BUILDER> implements ParameterizedElementBuilder<BUILDER> {

    /** List of individual lines of code that comprise the implementation. If null, no implementation is present */
    protected List<String> linesOfCode = null;
    /** List of parameters the element is to take */
    protected final List<JParameter<?>> parameters = new ArrayList<>();

    /**
     * CTOR
     * 
     * @param classBuilder {@link ClassBuilder} to which the built element belongs
     * @param name         {@link String} the name of the element to be created
     */
    protected NestedClassMethodElementBuilder(ClassBuilder classBuilder, String name) {
        super(classBuilder, name);
    }

    /**
     * Create a builder through which to add a new parameter to the method.
     * 
     * @param <PARAMETER_TYPE> extends {@link Type} indicating what the nature of the parameter is
     * @param type             PARAMETER_TYPE indicating what the exact type of the parameter it is
     * @param name             {@link String} of the parameter to create
     * @return {@link ParameterBuilder} to use to create the parameter
     */
    public <PARAMETER_TYPE extends Type> ParameterBuilder<PARAMETER_TYPE, BUILDER> buildParameter(PARAMETER_TYPE type, String name) {
        return new ParameterBuilder<>(this, type, name);
    }

    /**
     * Add a parameter to the element
     * 
     * @param parameter {@link JParameter} to add
     * @return {@link MethodBuilder}
     */
    public BUILDER addParameter(JParameter<?> parameter) {
        parameters.add(parameter);
        return get();
    }

    /**
     * Mark the method as one with an empty (blank) implementation. This is distinct from an element that has no implementation. Any implementation that may be present will be destroyed.
     * 
     * @return {@link MethodBuilder}
     */
    public BUILDER emptyImplementation() {
        linesOfCode = new ArrayList<>();
        return get();
    }

    /**
     * Add lines of code. These lines are appended to the end of the existing stored implementation.
     * 
     * @param lines {@link String}... lines to append
     * @return {@link MethodBuilder}
     */
    public BUILDER addCode(String... lines) {
        if (linesOfCode == null)
            linesOfCode = new ArrayList<>();

        for (String s : lines)
            linesOfCode.add(s);

        return get();
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
     * @see tendril.codegen.field.VisibileTypeBuilder#applyDetails(tendril.codegen.field.JVisibleType)
     */
    @Override
    protected ELEMENT applyDetails(ELEMENT element) {
        super.applyDetails(element);
        for (JParameter<?> param : parameters)
            element.addParameter(param);

        return element;
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
