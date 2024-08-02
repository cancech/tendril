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

import tendril.codegen.VisibilityType;
import tendril.codegen.classes.ClassBuilder;
import tendril.codegen.classes.MethodBuilder;
import tendril.codegen.field.type.Type;

/**
 * Builder for creating interface methods
 * 
 * @param <RETURN_TYPE> indicating the return {@link Type} of the method
 */
public class InterfaceMethodBuilder<RETURN_TYPE extends Type> extends MethodBuilder<RETURN_TYPE> {

    /**
     * CTOR
     * 
     * @param classBuilder {@link ClassBuilder} building the class to which the method belongs
     * @param name         {@link String} the name of the method
     */
    public InterfaceMethodBuilder(ClassBuilder classBuilder, String name) {
        super(classBuilder, name);
    }

    /**
     * Interface methods may or may not have an implementation, unless they're private in which case they must have code
     * 
     * @see tendril.codegen.classes.MethodBuilder#validate()
     */
    @Override
    protected void validate() {
        super.validate();
        
        if (visibility != VisibilityType.PUBLIC && !(visibility == VisibilityType.PRIVATE && hasCode()))
            throw new IllegalArgumentException("Interface method can only be public, or private if it has an implementation");
    }

    /**
     * @see tendril.codegen.BaseBuilder#create()
     */
    @Override
    protected JMethodInterface<RETURN_TYPE> create() {
        return new JMethodInterface<RETURN_TYPE>(type, name, linesOfCode);
    }

}
