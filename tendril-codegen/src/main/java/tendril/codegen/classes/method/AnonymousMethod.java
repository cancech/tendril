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

import java.util.Set;

import tendril.codegen.CodeGenerationException;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.Type;

/**
 * A simple anonymous method which cannot be used for code generation. This is to be used to represent method data in situations where the full method details are either not available or not
 * necessary. Namely during code generation when loading details from client code.
 * 
 * @param <RETURN_TYPE> indicating the return {@link Type} of the method
 */
public class AnonymousMethod<RETURN_TYPE extends Type> extends JMethod<RETURN_TYPE> {

    /**
     * CTOR
     * 
     * @param returnType RETURN_TYPE of the method
     * @param name       {@link String} the name of the method
     */
    public AnonymousMethod(RETURN_TYPE returnType, String name) {
        super(returnType, name, null);
    }

    /**
     * @see tendril.codegen.classes.method.JMethod#generateSignatureStart(boolean)
     */
    @Override
    protected String generateSignatureStart(boolean hasImplementation) {
        throw new CodeGenerationException("An annonymous method cannot be used to generate code");
    }

    /**
     * @see tendril.codegen.JBase#generateSelf(java.util.Set)
     */
    @Override
    public String generateSelf(Set<ClassType> classImports) {
        throw new CodeGenerationException("An annonymous method cannot be used to generate code");
    }

}
