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

import tendril.codegen.VisibilityType;
import tendril.codegen.classes.method.InterfaceMethodBuilder;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.Type;
import tendril.util.TendrilUtil;

/**
 * Builder for the creation and definition of interfaces
 */
class InterfaceBuilder extends ClassBuilder {

    /**
     * CTOR
     * 
     * @param type {@link ClassType} which has the basic class description included
     */
    InterfaceBuilder(ClassType type) {
        super(type);
    }
    
    /**
     * @see tendril.codegen.classes.ClassBuilder#buildConstructor()
     */
    @Override
    public ConstructorBuilder buildConstructor() {
        throw new IllegalArgumentException("Interfaces cannot have a constructor");
    }

    /**
     * @see tendril.codegen.classes.ClassBuilder#createMethodBuilder(java.lang.String)
     */
    @Override
    protected <RETURN_TYPE extends Type> MethodBuilder<RETURN_TYPE> createMethodBuilder(String name) {
        return new InterfaceMethodBuilder<RETURN_TYPE>(this, name);
    }

    /**
     * @see tendril.codegen.field.TypeBuilder#validate()
     */
    @Override
    protected void validate() {
        if (TendrilUtil.oneOfMany(visibility, VisibilityType.PROTECTED, VisibilityType.PRIVATE))
            throw new IllegalArgumentException("Illegal visibility " + visibility.name() + ". Only PUBLIC and PACKAGE_PRIVATE are allowed");
    }

    /**
     * @see tendril.codegen.BaseBuilder#create()
     */
    @Override
    protected JClass create() {
        return new JClassInterface(type);
    }

    /**
     * @see tendril.codegen.classes.ClassBuilder#extendsClass(tendril.codegen.field.type.ClassType)
     */
    @Override
    public ClassBuilder extendsClass(ClassType parent) {
        return super.implementsInterface(parent);
    }
    
    /**
     * @see tendril.codegen.classes.ClassBuilder#implementsInterface(tendril.codegen.field.type.ClassType)
     */
    @Override
    public ClassBuilder implementsInterface(ClassType iface) {
        throw new IllegalArgumentException("Interfaces cannot implement anything, they can only extend other interfaces.");
    }
}
