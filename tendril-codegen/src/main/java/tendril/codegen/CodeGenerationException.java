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
package tendril.codegen;

/**
 * Exception that is thrown when there is an issue during code generation
 */
public class CodeGenerationException extends RuntimeException {
    /** Serial ID */
    private static final long serialVersionUID = 1640740898935626846L;

    /**
     * CTOR
     * 
     * @param reason {@link String} the reason for the exception
     */
    public CodeGenerationException(String reason) {
        super(reason);
    }
}
