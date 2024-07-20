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

import tendril.codegen.VisibilityType;
import tendril.codegen.field.type.Type;

/**
 * Representation of a method that appears in an interface
 * 
 * @param <RETURN_TYPE> indicating the return {@link Type} of the method
 */
public class JMethodInterface<RETURN_TYPE extends Type> extends JMethod<RETURN_TYPE> {

    /**
     * CTOR
     * 
     * @param visibility     {@link VisibilityType} indicating the desired visibility of the method
     * @param returnType     RETURN_TYPE representing what the method returns
     * @param name           {@link String} the name of the method
     * @param implementation {@link List} of {@link String} lines of code with the implementation of the method
     */
    public JMethodInterface(VisibilityType visibility, RETURN_TYPE returnType, String name, List<String> implementation) {
        super(visibility, returnType, name, implementation);
    }

    /**
     * @see tendril.codegen.classes.method.JMethod#generateSignatureStart(boolean)
     */
    @Override
    protected String generateSignatureStart(boolean hasImplementation) {
        if (VisibilityType.PUBLIC == visibility)
            return hasImplementation ? "default " : "";

        return visibility.toString() + " ";
    }

}
