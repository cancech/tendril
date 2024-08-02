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

import tendril.codegen.JBase;
import tendril.codegen.field.type.Type;
import tendril.codegen.field.type.TypedElement;

/**
 * Represents situation where a Data {@link Type} is associated with a name. This can come in a variety of different forms, such as:
 * 
 * <ul>
 * <li>Variables - {@link Type} is the type of the variable, name is its name</li>
 * <li>Parameters - {@link Type} is the type of the parameter, name is its name)</li>
 * <li>Methods - {@link Type} is the return type of the method, name is its name</li>
 * </ul>
 * 
 * @param <DATA_TYPE> the {@link Type} indicating what kind of data structure is represented
 */
public abstract class JType<DATA_TYPE extends Type> extends JBase implements TypedElement<DATA_TYPE> {

    /** The {@link Type} of the named element */
    protected final DATA_TYPE type;

    /**
     * CTOR
     * 
     * @param type DATA_TYPE indicating what {@link Type} is the data structure
     * @param name {@link String} the name of the element
     */
    public JType(DATA_TYPE type, String name) {
        super(name);
        this.type = type;
    }

    /**
     * @see tendril.codegen.field.type.TypedElement#getType()
     */
    @Override
    public DATA_TYPE getType() {
        return type;
    }
    
    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return type.hashCode() + super.hashCode();
    }
    
    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof JType))
            return false;
        
        JType<?> otherElem = (JType<?>) other;
        return type.equals(otherElem.getType()) && name.equals(otherElem.getName());
    }
}
