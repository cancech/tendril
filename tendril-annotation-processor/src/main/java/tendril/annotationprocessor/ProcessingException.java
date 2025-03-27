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
package tendril.annotationprocessor;

/**
 * Exception to be thrown when there is an during annotation processing
 */
public class ProcessingException extends RuntimeException {
    /** Serial ID */
    private static final long serialVersionUID = -2996805748124184514L;

    /**
     * CTOR
     * 
     * @param reason {@link String} indicating the reason of the failure
     */
    public ProcessingException(String reason) {
        super(reason);
    }
    
    /**
     * CTOR
     * 
     * @param reason {@link String} indicating the reason of the failure
     * @param cause {@link Exception} which triggered the exception
     */
    public ProcessingException(String reason, Exception cause) {
        super(reason, cause);
    }
}
