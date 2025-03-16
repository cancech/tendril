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
package tendril.codegen.generics;

import java.util.Set;

import tendril.codegen.CodeGenerationException;
import tendril.codegen.DefinitionException;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.Type;
import tendril.codegen.field.value.JValue;
import tendril.codegen.field.value.JValueFactory;

/**
 * {@link GenericType} representing a generic that is given an explicit Class for a type (i.e.: <MyClass>)
 */
class SimpleExplicitGeneric extends GenericType {

    /** The type of class that the generic explicitly resolves to (i.e: the exact class applied to an elsewhere defined generic */
    private final ClassType classType;

    /**
     * CTOR
     * 
     * @param type {@link ClassType} that the generic resolves to
     */
    SimpleExplicitGeneric(ClassType type) {
        super(type.getSimpleName());
        this.classType = type;
    }

    /**
     * @see tendril.codegen.generics.GenericType#generateDefinition()
     */
    @Override
    public String generateDefinition() {
        throw new CodeGenerationException("A generic explicitely resolved to a type cannot be used in a definition");
    }

    /**
     * @see tendril.codegen.field.type.Importable#registerImport(java.util.Set)
     */
    @Override
    public void registerImport(Set<ClassType> classImports) {
        classImports.add(classType);
    }

    /**
     * @see tendril.codegen.field.type.Type#isTypeOf(java.lang.Object)
     */
    @Override
    public boolean isTypeOf(Object value) {
        return classType.isTypeOf(value);
    }

    /**
     * @see tendril.codegen.field.type.Type#isAssignableFrom(tendril.codegen.field.type.Type)
     */
    @Override
    public boolean isAssignableFrom(Type other) {
        return classType.isAssignableFrom(other);
    }

    /**
     * @see tendril.codegen.field.type.Type#asValue(java.lang.Object)
     */
    @Override
    public JValue<?, ?> asValue(Object value) {
        if (!isTypeOf(value))
            throw new DefinitionException("Invalid object provided: require " + getSimpleName() + " but received " + value.getClass().getName());

        return JValueFactory.create(value);
    }

    /**
     * @see tendril.codegen.field.type.Type#asClassType()
     */
    @Override
    public ClassType asClassType() {
        return classType;
    }

}
