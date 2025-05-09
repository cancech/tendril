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

/**
 * Marker interface to allow for direct comparisons between items which have a type. The following can be used:
 * 
 * <ul>
 * <li>{@link VoidType} specifically for void methods</li>
 * <li>{@link ClassType} to represent a particular class or other Declared type</li>
 * <li>{@link PrimitiveType} to represent the different primitives available within Java</li>
 * </ul>
 * 
 * @param <DATA_TYPE> The representation of the type of data which is stored within.
 */
public interface TypedElement<DATA_TYPE extends Type> {
    
    /**
     * Get the DATA_TYPE for the element
     * 
     * @return The {@link Type}
     */
    DATA_TYPE getType();
}
