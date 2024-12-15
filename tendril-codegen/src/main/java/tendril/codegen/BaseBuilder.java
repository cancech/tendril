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
package tendril.codegen;

import java.util.ArrayList;
import java.util.List;

import tendril.codegen.annotation.JAnnotation;
import tendril.codegen.generics.GenericType;

/**
 * Builder which is responsible to collecting and applying the basic characteristics to elements
 * 
 * @param <ELEMENT> extending {@link JBase} indicating what the builder will produce
 * @param <BUILDER> indicating the specific child builder that is employed
 */
public abstract class BaseBuilder<ELEMENT extends JBase, BUILDER extends BaseBuilder<ELEMENT, BUILDER>> {

    /** The name of the element */
    protected final String name;
    /** List of annotations that are applied to the element */
    protected final List<JAnnotation> annotations = new ArrayList<>();
    /** List of generics that are applied to the element */
    protected final List<GenericType> generics = new ArrayList<>();
    /** Flag for whether the element is final */
    protected boolean isFinal = false;

    /**
     * CTOR
     * 
     * @param name {@link String} of the element
     */
    public BaseBuilder(String name) {
        this.name = name;
    }

    /**
     * Get the builder that is to be returned
     * 
     * @return BUILDER
     */
    @SuppressWarnings("unchecked")
    protected BUILDER get() {
        return (BUILDER) this;
    }

    /**
     * Set the final status of the element
     * 
     * @param isFinal boolean true if it is to be final
     * @return BUILDER
     */
    public BUILDER setFinal(boolean isFinal) {
        this.isFinal = isFinal;
        return get();
    }

    /**
     * Add an annotation to the element
     * 
     * @param annotation {@link JAnnotation} to apply
     * @return BUILDER
     */
    public BUILDER addAnnotation(JAnnotation annotation) {
        annotations.add(annotation);
        return get();
    }

    /**
     * Add a generic to the element
     * 
     * @param generic {@link GenericType} to apply
     * @return BUILDER
     */
    public BUILDER addGeneric(GenericType generic) {
        generics.add(generic);
        return get();
    }

    /**
     * Build the element, applying the specified details. The specified details are validated to ensure that it is a valid combination for the element being constructed.
     * 
     * @return ELEMENT created and customized per what was specified
     */
    public ELEMENT build() {
        validate();
        return applyDetails(create());
    }

    /**
     * Validate that the provided details to ensure that they are valid for the desired element. Any issues are raised via an {@link IllegalArgumentException}
     */
    protected abstract void validate();

    /**
     * Create the actual element, providing a new instance with each call.
     * 
     * @return ELEMENT
     */
    protected abstract ELEMENT create();

    /**
     * Apply the details to the element in accordance to what was specified.
     * 
     * @param element ELEMENT to which to apply the specified details
     * @return ELEMENT
     */
    protected ELEMENT applyDetails(ELEMENT element) {
        element.setFinal(isFinal);
        annotations.forEach(a -> element.addAnnotation(a));
        generics.forEach(g -> element.addGeneric(g));
        return element;
    }

}
