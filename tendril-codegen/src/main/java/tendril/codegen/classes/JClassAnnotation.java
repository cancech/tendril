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

import java.util.List;

import tendril.codegen.DefinitionException;
import tendril.codegen.field.type.ClassType;

/**
 * Representation of an annotation
 */
public class JClassAnnotation extends JClassInterface {

    /**
     * CTOR
     * 
     * @param data {@link ClassType} the information about the class
     */
    protected JClassAnnotation(ClassType data) {
        super(data);
    }

    /**
     * @see tendril.codegen.classes.JClassInterface#getClassKeyword()
     */
    @Override
    protected String getClassKeyword() {
        return "@" + super.getClassKeyword();
    }

    /**
     * @see tendril.codegen.classes.JClassInterface#setParentClass(tendril.codegen.classes.JClass)
     */
    @Override
    public void setParentClass(JClass parent) {
        if (parent != null)
            throw new DefinitionException(type, "Annotations cannot have an explicit parent class");
    }

    /**
     * @see tendril.codegen.classes.JClass#setParentInterfaces(java.util.List)
     */
    @Override
    public void setParentInterfaces(List<JClass> ifaces) {
        if (!ifaces.isEmpty())
            throw new DefinitionException(type, "Annotations cannot implement any interfaces");
    }
}
