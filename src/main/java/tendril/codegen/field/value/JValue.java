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
package tendril.codegen.field.value;

import java.util.Set;

import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.Type;
import tendril.codegen.field.type.TypedElement;

/**
 * The base representation of a value
 * 
 * @param <DATA_TYPE> representing the {@link Type} of the value
 * @param <VALUE_TYPE> the specific (Java) type storing the value
 */
public abstract class JValue<DATA_TYPE extends Type, VALUE_TYPE> implements TypedElement<DATA_TYPE> {
    
    /** Representation of the type of data stored */
    protected final DATA_TYPE type;
    /** Representation of the "raw data" of the value */
    protected final VALUE_TYPE value;

    /**
     * CTOR
     * 
     * @param type  DATA_TYPE representing what the data structure type of the value is
     * @param value VALUE_TYPE wrapping the data of the value
     */
    public JValue(DATA_TYPE type, VALUE_TYPE value) {
        this.type = type;
        this.value = value;
    }
    
    /**
     * Perform a check to verify if the value matches the other data type. Or to put it more explicitly, can this type be assigned to the otherType.
     * 
     * @param otherType DATA_TYPE of the type to be checked against
     * @return boolean true if this value is an instance of the other type
     */
    public boolean isInstanceOf(Type otherType) {
        if (otherType == null || type == null)
            return false;

        return otherType.isAssignableFrom(type);
    }

    /**
     * Get the type of this value
     * 
     * @return DATA_TYPE
     */
    public DATA_TYPE getType() {
        return type;
    }

    /**
     * Get the wrapper value data
     * 
     * @return VALUE_TYPE wrapping the "raw data" of the value
     */
    public VALUE_TYPE getValue() {
        return value;
    }

    /**
     * Generate a code representation of the value
     * 
     * @param classImports {@link Set} of {@link ClassType} where any imports for this value are to be registered
     * @return {@link String} code representing the value
     */
    public abstract String generate(Set<ClassType> classImports);
}
