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

import tendril.codegen.DefinitionException;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.Type;
import tendril.codegen.field.value.JValue;
import tendril.codegen.field.value.JValueFactory;

/**
 * {@link GenericType} representing a generic type that can be applied to a class (i.e.: <T>).
 */
class SimpleGeneric extends GenericType {
    
    /**
     * CTOR
     * 
     * @param name {@link String} the name to apply to the generic
     */
    SimpleGeneric(String name) {
        super(name);
    }

    /**
     * @see tendril.codegen.field.type.Type#isTypeOf(java.lang.Object)
     */
    @Override
    public boolean isTypeOf(Object value) {
        return true;
    }

    /**
     * @see tendril.codegen.field.type.Type#isAssignableFrom(tendril.codegen.field.type.Type)
     */
    @Override
    public boolean isAssignableFrom(Type other) {
        return true;
    }

    /**
     * @see tendril.codegen.field.type.Type#asValue(java.lang.Object)
     */
    @Override
    public JValue<?, ?> asValue(Object value) {
        return JValueFactory.create(value);
    }

    /**
     * @see tendril.codegen.field.type.Type#asClassType()
     */
    @Override
    public ClassType asClassType() {
        throw new DefinitionException("Cannot derive a ClassType for the generic");
    }
}
