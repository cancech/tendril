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

import java.util.Collection;

/**
 * Utility class which holds miscellaneous capabilities used throughout the library
 */
public abstract class TendrilUtil {

    /**
     * Hidden CTOR
     */
    private TendrilUtil() {
    }

    /**
     * Check if an object to check is one of the various desired objects provided. Equality is determined via the equals method.
     * 
     * @param <T>     the type of objects to be checked
     * @param toCheck T the specific item to check
     * @param desired T... the various elements that the element is to be compared against
     * @return boolean true if toCheck is equal to one of the desired elements
     */
    @SafeVarargs
    public static <T> boolean oneOfMany(T toCheck, T... desired) {
        return oneOfMany((lhs, rhs) -> lhs.equals(rhs), toCheck, desired);
    }

    /**
     * Check if an object to check is one of the various desired objects provided. Equality is determined via the provided {@link Comparator}.
     * 
     * @param <T>        the type of objects to be checked
     * @param comparator {@link Comparator} to use to compare the element to check against the desired elements
     * @param toCheck    T the specific item to check
     * @param desired    T... the various elements that the element is to be compared against
     * @return boolean true if toCheck is equal to one of the desired elements
     */
    @SafeVarargs
    public static <T> boolean oneOfMany(Comparator<T> comparator, T toCheck, T... desired) {
        // See if there is a match with one of the desired elements
        for (T obj : desired) {
            if (toCheck == null) {
                if (obj == null)
                    return true;
            } else if (comparator.compare(toCheck, obj))
                return true;
        }

        return false;
    }
    
    /**
     * Check if the {@code toCheck} {@link Collection} contains any of the elements from the {@code toFind} {@link Collection}.
     * 
     * @param <T> the type of element to be contained within the {@link Collection}s
     * @param toCheck {@link Collection} in which to look for the elements
     * @param toFind {@link Collection} of elements to look for
     * @return boolean true if any of the elements from {@code toFind} are present in {@code toCheck}
     */
    public static <T> boolean containsAny(Collection<T> toCheck, Collection<T> toFind) {
        for (T t: toFind) {
            if (toCheck.contains(t))
                return true;
        }
        
        return false;
    }
}
