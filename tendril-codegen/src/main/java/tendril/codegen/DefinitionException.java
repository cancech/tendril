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

import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.Type;

/**
 * Exception to be thrown when an issue is detected with the code definition
 */
public class DefinitionException extends RuntimeException {
    /** Serial ID */
    private static final long serialVersionUID = -3902603100011420424L;

    /**
     * CTOR
     * 
     * @param reason {@link String} the reason for the exception
     */
    public DefinitionException(String reason) {
        super(reason);
    }

    /**
     * CTOR
     * 
     * @param reason {@link String} the reason for the exception
     * @param cause {@link Exception} which ultimately cause this exception to happen
     */
    public DefinitionException(String reason, Exception cause) {
        super(reason, cause);
    }

    /**
     * CTOR
     * 
     * @param type {@link ClassType} whose definition is causing the exception
     * @param reason {@link String} the reason for the exception
     */
    public DefinitionException(ClassType type, String reason) {
        this(type.getFullyQualifiedName(), reason);
    }

    /**
     * CTOR
     * 
     * @param type {@link ClassType} whose definition is causing the exception
     * @param reason {@link String} the reason for the exception
     * @param cause {@link Exception} which ultimately cause this exception to happen
     */
    public DefinitionException(ClassType type, String reason, Exception cause) {
        this(type.getFullyQualifiedName(), reason, cause);
    }

    /**
     * CTOR
     * 
     * @param type {@link Type} whose definition is causing the exception
     * @param reason {@link String} the reason for the exception
     */
    public DefinitionException(Type type, String reason) {
        this(type.getSimpleName(), reason);
    }

    /**
     * CTOR
     * 
     * @param type {@link Type} whose definition is causing the exception
     * @param reason {@link String} the reason for the exception
     * @param cause {@link Exception} which ultimately cause this exception to happen
     */
    public DefinitionException(Type type, String reason, Exception cause) {
        this(type.getSimpleName(), reason, cause);
    }

    /**
     * CTOR
     * 
     * @param type {@link Class} whose definition is causing the exception
     * @param reason {@link String} the reason for the exception
     */
    public DefinitionException(Class<?> type, String reason) {
        this(type.getName(), reason);
    }

    /**
     * CTOR
     * 
     * @param type {@link Class} whose definition is causing the exception
     * @param reason {@link String} the reason for the exception
     * @param cause {@link Exception} which ultimately cause this exception to happen
     */
    public DefinitionException(Class<?> type, String reason, Exception cause) {
        this(type.getName(), reason, cause);
    }

    /**
     * CTOR
     * 
     * @param fullyQualifiedName {@link String} fully qualified name of the class whose definition is causing the exception
     * @param reason {@link String} the reason for the exception
     */
    private DefinitionException(String fullyQualifiedName, String reason) {
        super(fullyQualifiedName + " - " + reason);
    }

    /**
     * CTOR
     * 
     * @param fullyQualifiedName {@link String} fully qualified name of the class whose definition is causing the exception
     * @param reason {@link String} the reason for the exception
     * @param cause {@link Exception} which ultimately cause this exception to happen
     */
    private DefinitionException(String fullyQualifiedName, String reason, Exception cause) {
        super(fullyQualifiedName + " - " + reason, cause);
    }
}
