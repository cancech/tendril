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
package tendril.util;

/**
 * Functional interface to facilitate the comparison of two items
 * 
 * @param <T> the type of elements to be compared
 */
public interface Comparator<T> {

    /**
     * Perform the comparison, returning true if the two are deemed to be equal
     * 
     * @param lhs T which appears on the left hand side of the comparison
     * @param rhs T which appears on the right hand side of the comparison
     * @return boolean true if the two are deemed to be equal
     */
    boolean compare(T lhs, T rhs);
}
