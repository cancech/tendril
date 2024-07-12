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
package tendril.dom.type;

/**
 * Marker interface through which to identify legitimate types for {@link TypedElement}s
 */
public interface Type {

    /**
     * Check if this can be assigned to the other type
     * 
     * @param other {@link Type} to check assignment to
     * @return boolean true if this can be assigned to other
     */
    boolean isAssignableTo(Type other);

}
