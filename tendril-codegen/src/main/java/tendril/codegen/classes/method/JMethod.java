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
import java.util.Set;

import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.Type;

/**
 * Representation of a method
 * 
 * @param <RETURN_TYPE> indicating the return {@link Type} of the method
 */
public abstract class JMethod<RETURN_TYPE extends Type> extends JAbstractMethodElement<RETURN_TYPE> {

    /**
     * CTOR
     * 
     * @param returnType     RETURN_TYPE representing what the method returns
     * @param name           {@link String} the name of the method
     * @param implementation {@link List} of {@link String} lines of code with the implementation of the method
     */
    protected JMethod(RETURN_TYPE returnType, String name, List<String> implementation) {
        super(returnType, name, implementation);
    }

    /**
     * @see tendril.codegen.classes.method.JAbstractMethodElement#generateSignature(java.util.Set, boolean)
     */
    @Override
    protected String generateSignature(Set<ClassType> classImports, boolean hasImplementation) {
        StringBuilder signature = new StringBuilder(generateSignatureStart(hasImplementation));
        signature.append(getType().getSimpleName() + " " + getName());
        signature.append("(" + generateParameters(classImports) + ")");
        signature.append(generateSignatureEnd(hasImplementation));
        return signature.toString();
    }

    /**
     * Generate the start of the method signature (start until the method return type)
     * 
     * @param hasImplementation boolean true if the method has an implementation present
     * @return {@link String} the code for the start of the method signature.
     */
    protected abstract String generateSignatureStart(boolean hasImplementation);

    /**
     * Generate the end of the method signature (after the closing parameter bracket).
     * 
     * @param hasImplementation boolean true if the method has an implementation present
     * @return {@link String} the code for the end of the method signature.
     */
    protected String generateSignatureEnd(boolean hasImplementation) {
        if (hasImplementation)
            return " {";
        return ";";
    }
}
