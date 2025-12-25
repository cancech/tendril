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

/**
 * Exception to be thrown when an invalid configuration is encountered
 */
public class InvalidConfigurationException extends TendrilException{
    /** Serial ID */
    private static final long serialVersionUID = -3513542401048886484L;

    /**
     * CTOR
     * 
     * @param reason {@link String} causing the exception
     */
    public InvalidConfigurationException(String reason) {
        super(reason);
    }

    /**
     * CTOR
     * 
     * @param reason {@link String} causing the exception
     * @param cause {@link Exception} which triggered the exception
     */
    public InvalidConfigurationException(String reason, Exception cause) {
        super(reason, cause);
    }

}
