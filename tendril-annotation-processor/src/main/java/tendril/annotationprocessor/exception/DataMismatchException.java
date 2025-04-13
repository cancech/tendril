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
package tendril.annotationprocessor.exception;

import tendril.codegen.field.type.Type;

/**
 * Exception to be thrown when there is a data mismatch between two types
 */
public class DataMismatchException extends TendrilException {
    /** Serial ID */
    private static final long serialVersionUID = 8767889713272940187L;

    /**
     * CTOR
     * 
     * @param expected {@link Type} representing what was expected to be received
     * @param received {@link Type} what was actually received
     */
    public DataMismatchException(Type expected, Type received) {
        this(expected, received.toString());
    }

    /**
     * CTOR
     * 
     * @param expected {@link Type} representing what was expected to be received
     * @param received {@link String} indicating what was actually received
     */
    public DataMismatchException(Type expected, String received) {
        this("Invalid type, expected " + expected + " but received " + received);
    }

    /**
     * CTOR
     * 
     * @param message {@link String} containing the cause of the exception
     */
    public DataMismatchException(String message) {
        super(message);
    }
}
