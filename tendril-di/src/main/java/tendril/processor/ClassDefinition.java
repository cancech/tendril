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
package tendril.processor;

import tendril.codegen.field.type.ClassType;

/**
 * Wrapper which associates a {@link ClassType} with the code implementing it.
 */
public class ClassDefinition {
    /** The representation of the class being defined */
    private final ClassType type;
    /** The code which defined the class */
    private final String code;
    
    /**
     * CTOR
     * 
     * @param type {@link ClassType} representing the class being defined
     * @param code {@link String} the code performing the definition
     */
    public ClassDefinition(ClassType type, String code) {
        this.type = type;
        this.code = code;
    }

    /**
     * Get the type of class defined
     * 
     * @return {@link ClassType}
     */
    public ClassType getType() {
        return type;
    }
    
    /**
     * Get the code for the class.
     * 
     * @return {@link String}
     */
    String getCode() {
        return code;
    }
}
