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
package tendril.codegen.field;

import tendril.codegen.VisibilityType;
import tendril.codegen.field.type.Type;

/**
 * Builder for processing the visibility characteristics of elements
 * 
 * @param <DATA_TYPE> extending {@link Type} the element represents
 * @param <ELEMENT>   extending {@link JVisibleType} the builder produces
 * @param <BUILDER>   indicating which specific child builder is being employed
 */
public abstract class VisibileTypeBuilder<DATA_TYPE extends Type, ELEMENT extends JVisibleType<DATA_TYPE>, BUILDER extends VisibileTypeBuilder<DATA_TYPE, ELEMENT, BUILDER>>
        extends TypeBuilder<DATA_TYPE, ELEMENT, BUILDER> {

    /** The visibility that has bee specified for the element */
    protected VisibilityType visibility = VisibilityType.PACKAGE_PRIVATE;
    /** Flag for whether the element is static */
    protected boolean isStatic = false;

    /**
     * CTOR
     * 
     * @param name {@link String} the name of the element being defined
     */
    public VisibileTypeBuilder(String name) {
        super(name);
    }

    /**
     * Set the visibility of the defined element
     * 
     * @param visibility {@link VisibilityType} to apply
     * 
     * @return BUILDER
     */
    public BUILDER setVisibility(VisibilityType visibility) {
        this.visibility = visibility;
        return get();
    }

    /**
     * Set the static status of the element
     * 
     * @param isStatic boolean true if it is to be static
     * @return BUILDER
     */
    public BUILDER setStatic(boolean isStatic) {
        this.isStatic = isStatic;
        return get();
    }

    /**
     * @see tendril.codegen.BaseBuilder#applyDetails(tendril.codegen.JBase)
     */
    @Override
    protected ELEMENT applyDetails(ELEMENT element) {
        element.setVisibility(visibility);
        element.setStatic(isStatic);
        return super.applyDetails(element);
    }
}
