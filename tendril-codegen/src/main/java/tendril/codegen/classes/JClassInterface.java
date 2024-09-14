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
package tendril.codegen.classes;

import tendril.codegen.field.type.ClassType;

/**
 * Representation of an interface
 */
public class JClassInterface extends JClass {

    /**
     * CTOR
     * 
     * @param data {@link ClassType} the information about the class
     */
    protected JClassInterface(ClassType data) {
        super(data);
    }

    /**
     * @see tendril.codegen.classes.JClass#classType()
     */
    @Override
    protected String classType() {
        return "interface";
    }

    /**
     * @see tendril.codegen.classes.JClass#setParentClass(tendril.codegen.field.type.ClassType)
     */
    @Override
    public void setParentClass(ClassType parent) {
        if (parent != null)
            throw new IllegalArgumentException("Interfaces cannot have an explicit parent class");
    }

    /**
     * @see tendril.codegen.classes.JClass#interfaceExtensionKeyword()
     */
    @Override
    protected String interfaceExtensionKeyword() {
        return "extends";
    }
}
