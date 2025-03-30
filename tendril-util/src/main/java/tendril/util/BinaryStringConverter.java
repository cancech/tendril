/*
 * Copyright 2025 Jaroslav Bosak
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
 * Works in conjunction with {@link TendrilStringUtil} to perform the conversion of an arbitrary object to a {@link String}, via a client defined mechanism.
 * 
 * @param <T> the type of the first {@link Object} the converter is to work with.
 * @param <U> the type of the second {@link Object} the converter is to work with.
 */
public interface BinaryStringConverter<T, U> {

    /**
     * Allows for the conversion of two objects such that they combine into a single {@link String}.
     * 
     * @param first T the first object
     * @param second U the second object
     * @return {@link String} resulting from the conversion
     */
    String convert(T first, U second);
}
