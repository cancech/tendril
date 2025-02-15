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

import tendril.codegen.DefinitionException;
import tendril.codegen.VisibilityType;
import tendril.codegen.classes.ClassBuilder;
import tendril.codegen.classes.MethodBuilder;
import tendril.codegen.field.type.Type;

/**
 * Builder for creating abstract methods
 * 
 * @param <RETURN_TYPE> indicating the return {@link Type} of the method
 */
public class AbstractMethodBuilder<RETURN_TYPE extends Type> extends MethodBuilder<RETURN_TYPE> {

    /**
     * CTOR - for use when creating methods nested within a class being defined
     * 
     * @param classBuilder {@link ClassBuilder} building the class to which the method belongs
     * @param name         {@link String} the name of the method
     */
    public AbstractMethodBuilder(ClassBuilder classBuilder, String name) {
        super(classBuilder, name);
    }

    /**
     * Method must either have an implementation or or not be private
     * 
     * @see tendril.codegen.BaseBuilder#validate()
     */
    @Override
    protected void validate() {
        if (VisibilityType.PRIVATE == visibility && !hasCode())
            throw new DefinitionException(classBuilder.getType(), "Abstract method " + name + " cannot be private");

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
