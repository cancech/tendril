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
package tendril.codegen.field.type;

import tendril.codegen.field.value.JValue;

/**
 * Marker interface through which to identify legitimate types for {@link TypedElement}s
 */
public interface Type extends Importable {
    
    /**
     * Check if the type is void.
     * 
     * @return boolean true if this is a void type
     */
    boolean isVoid();
    
    /**
     * Get the simple name/code of the type
     * 
     * @return {@link String}
     */
    String getSimpleName();
    
    /**
     * Check if the value is of this type
     * 
     * @param value {@link Object} the value to check
     * @return boolean true if the value is of this type
     */
    boolean isTypeOf(Object value);

    /**
     * Check if this can be assigned from the other type
     * 
     * @param other {@link Type} to check assignment from
     * @return boolean true if this can be assigned from other
     */
    boolean isAssignableFrom(Type other);

    /**
     * Create a value for the DATA_TYPE
     * 
     * @param value {@link Object} the value to employ
     * @return {@link JValue}
     */
    JValue<?, ?> asValue(Object value);
}
