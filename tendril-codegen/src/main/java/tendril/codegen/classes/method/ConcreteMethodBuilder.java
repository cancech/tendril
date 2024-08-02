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

import tendril.codegen.classes.ClassBuilder;
import tendril.codegen.classes.MethodBuilder;
import tendril.codegen.field.type.Type;

/**
 * Builder for concrete methods (must have an implementation.
 * 
 * @param <RETURN_TYPE> indicating the return {@link Type} of the method
 */
public class ConcreteMethodBuilder<RETURN_TYPE extends Type> extends MethodBuilder<RETURN_TYPE> {

    /**
     * CTOR
     * 
     * @param classBuilder {@link ClassBuilder} building the class to which the method belongs
     * @param name         {@link String} the name of the method
     */
    public ConcreteMethodBuilder(ClassBuilder classBuilder, String name) {
        super(classBuilder, name);
    }

    /**
     * Method must have an implementation.
     * 
     * @see tendril.codegen.BaseBuilder#validate()
     */
    @Override
    protected void validate() {
        if (!hasCode())
            throw new IllegalArgumentException("Concrete methods much have an implementation");

        super.validate();
    }

    /**
     * @see tendril.codegen.BaseBuilder#create()
     */
    @Override
    protected JMethodDefault<RETURN_TYPE> create() {
        return new JMethodDefault<RETURN_TYPE>(type, name, linesOfCode);
    }

}
