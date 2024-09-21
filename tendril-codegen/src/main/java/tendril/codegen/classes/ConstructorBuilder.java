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

import tendril.codegen.classes.method.JConstructor;
import tendril.codegen.field.type.ClassType;

/**
 * Builder for creating constructors that can be added to a class, defined in a {@link ClassBuilder}
 */
public class ConstructorBuilder extends NestedClassMethodElementBuilder<ClassType, JConstructor, ConstructorBuilder> {

    /**
     * CTOR
     * 
     * @param classBuilder   {@link ClassBuilder} where the enclosing class is being defined
     * @param enclosingClass {@link ClassType} of the enclosing class
     */
    protected ConstructorBuilder(ClassBuilder classBuilder, ClassType enclosingClass) {
        super(classBuilder, enclosingClass.getSimpleName());
        super.setType(enclosingClass);
    }
    
    /**
     * @see tendril.codegen.field.TypeBuilder#setType(tendril.codegen.field.type.Type)
     */
    @Override
    public ConstructorBuilder setType(ClassType type) {
        throw new IllegalArgumentException("It is not possible to change the type of a constructor");
    }

    /**
     * @see tendril.codegen.classes.NestedClassMethodElementBuilder#validate()
     */
    @Override
    protected void validate() {
        if (isStatic)
            throw new IllegalArgumentException("Constructors cannot be static");
        if (isFinal)
            throw new IllegalArgumentException("Constructors cannot be final");
        if (!hasCode())
            throw new IllegalArgumentException("Constructor must have an implementation");
        super.validate();
    }

    /**
     * @see tendril.codegen.classes.NestedClassElementBuilder#addToClass(tendril.codegen.classes.ClassBuilder, tendril.codegen.field.JVisibleType)
     */
    @Override
    protected void addToClass(ClassBuilder classBuilder, JConstructor toAdd) {
        classBuilder.add(toAdd);
    }

    /**
     * @see tendril.codegen.BaseBuilder#create()
     */
    @Override
    protected JConstructor create() {
        return new JConstructor(type, linesOfCode);
    }

}
