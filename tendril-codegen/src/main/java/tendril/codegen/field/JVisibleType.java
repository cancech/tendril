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
 * Abstract type that adds visibility to the element
 * 
 * @param <DATA_TYPE> extending {@link Type} indicating the type of data the element represents
 */
public abstract class JVisibleType<DATA_TYPE extends Type> extends JType<DATA_TYPE> {

    /** The visibility of the method */
    protected VisibilityType visibility = VisibilityType.PACKAGE_PRIVATE;
    /** Flag for whether or not the element is static */
    private boolean isStatic = false;

    /**
     * CTOR
     * 
     * @param type DATA_TYPE of the element
     * @param name {@link String} of the element
     */
    public JVisibleType(DATA_TYPE type, String name) {
        super(type, name);
    }

    /**
     * Set the visibility of the element
     * 
     * @param visibility {@link VisibilityType} of the element
     */
    public void setVisibility(VisibilityType visibility) {
        this.visibility = visibility;
    }

    /**
     * Get the visibility of the element
     * 
     * @return {@link VisibilityType}
     */
    public VisibilityType getVisibility() {
        return visibility;
    }

    /**
     * Set the static state of the element
     * 
     * @param isStatic boolean true if it is to be static
     */
    public void setStatic(boolean isStatic) {
        this.isStatic = isStatic;
    }

    /**
     * Get the static state of the element
     * 
     * @return true if the element is static
     */
    public boolean isStatic() {
        return isStatic;
    }
    
    /**
     * Get the keyword to use in the code.
     * 
     * @return {@link String} they keyword (or nothing if static is not applied).
     */
    public String getStaticKeyword() {
        if (!isStatic)
            return "";
        
        return "static ";
    }
    
}
