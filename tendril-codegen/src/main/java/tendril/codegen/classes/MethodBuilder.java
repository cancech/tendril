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
import tendril.codegen.VisibilityType;
import tendril.codegen.annotation.JAnnotation;
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
public abstract class MethodBuilder<RETURN_TYPE extends Type> {
    /** The class containing the method */
    private final JClass encompassingClass;

    /** Representation of what the method is to return */
    protected final RETURN_TYPE returnType;
    /** The name of the method */
    protected final String name;
    /** The visibility of the method */
    protected VisibilityType visibility = VisibilityType.PUBLIC;
    /** List of individual lines of code that comprise the method implementation. If null, no implementation is present */
    protected List<String> linesOfCode = null;
    /** List of annotations applied to the method */
    private final List<JAnnotation> annotations = new ArrayList<>();
    /** List of parameters the method is to take */
    private final List<JParameter<?>> parameters = new ArrayList<>();

    /**
     * CTOR
     * 
     * @param encompassingClass {@link JClass} which contain the method
     * @param returnType        RETURN_TYPE representing what the method returns
     * @param name              {@link String} the name of the method
     */
    protected MethodBuilder(JClass encompassingClass, RETURN_TYPE returnType, String name) {
        this.encompassingClass = encompassingClass;
        this.returnType = returnType;
        this.name = name;
    }

    /**
     * Set the visibility of the method. By default the method is public.
     * 
     * @param visibility {@link VisibilityType} to employ
     * @return {@link MethodBuilder}
     */
    public MethodBuilder<RETURN_TYPE> setVisibility(VisibilityType visibility) {
        this.visibility = visibility;
        return this;
    }

    /**
     * Add an annotation to the method
     * 
     * @param annotation {@link JAnnotation} to apply
     * @return {@link MethodBuilder}
     */
    public MethodBuilder<RETURN_TYPE> addAnnotation(JAnnotation annotation) {
        annotations.add(annotation);
        return this;
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
     * Validate the provided values to ensure that they are sane for the enclosing class and build the method. The method is automatically added to the enclosing class.
     * 
     * @throws IllegalArgumentException if any issue is encountered with the provided method details
     */
    public void build() throws IllegalArgumentException {
        Utilities.throwIfNotValidIdentifier(name);
        validateData();
        JMethod<RETURN_TYPE> method = buildMethod(returnType, name);
        for (JAnnotation anno : annotations)
            method.addAnnotation(anno);
        for (JParameter<?> param : parameters)
            method.addParameter(param);
        encompassingClass.addMethod(method);
    }

    /**
     * Check if the class has any code/implementation available.
     * 
     * @return true if code/implementation is present
     */
    protected boolean hasCode() {
        return linesOfCode != null;
    }

    /**
     * Validate the data, ensuring that it is applicable for the enclosing class.
     * 
     * @throws IllegalArgumentException for any issues encountered
     */
    protected abstract void validateData() throws IllegalArgumentException;

    /**
     * Build the method using the provided details.
     * 
     * @param returnType RETURN_TYPE representing what the method returns
     * @param name       {@link String} the name of the method
     * @return {@link JMethod} representation for the enclosing class
     */
    protected abstract JMethod<RETURN_TYPE> buildMethod(RETURN_TYPE returnType, String name);
}
