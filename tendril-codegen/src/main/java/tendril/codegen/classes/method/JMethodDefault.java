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
package tendril.codegen.classes.method;

import java.util.List;

import tendril.codegen.DefinitionException;
import tendril.codegen.field.type.Type;

/**
 * Representation of a default method that appears in (abstract) classes
 * 
 * @param <RETURN_TYPE> indicating the return {@link Type} of the method
 */
class JMethodDefault<RETURN_TYPE extends Type> extends JMethod<RETURN_TYPE> {

    /**
     * CTOR
     * 
     * @param returnType     RETURN_TYPE representing what the method returns
     * @param name           {@link String} the name of the method
     * @param implementation {@link List} of {@link String} lines of code with the implementation of the method
     */
    JMethodDefault(RETURN_TYPE returnType, String name, List<String> implementation) {
        super(returnType, name, implementation);
    }

    /**
     * @see tendril.codegen.classes.method.JMethod#generateSignatureStart(boolean)
     */
    @Override
    protected String generateSignatureStart(boolean hasImplementation) {
        String start = visibility.getKeyword();
        if (!hasImplementation) {
            if (isStatic())
                throw new DefinitionException(type, "Abstract method cannot be static");
            if (isFinal())
                throw new DefinitionException(type, "Abstract method cannot be final");
            return start + "abstract ";
        }
        return start + getStaticKeyword() + getFinalKeyword();
    }
}
