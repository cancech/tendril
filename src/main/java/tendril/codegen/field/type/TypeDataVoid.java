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
package tendril.codegen.field.type;

import tendril.dom.type.core.VoidType;
import tendril.dom.type.value.ValueElement;

/**
 * Representation of a void data type
 */
class TypeDataVoid extends TypeData<VoidType> {
    
    /**
     * CTOR
     */
    TypeDataVoid() {
        super(VoidType.INSTANCE, "void");
    }
    
    /**
     * @see tendril.codegen.field.type.TypeData#isVoid()
     */
    @Override
    public boolean isVoid() {
        return true;
    }

    /**
     * Cannot produce a value for a void data type. IllegalArgumentException is thrown when attempted.
     * 
     * @see tendril.codegen.field.type.TypeData#asValue(java.lang.Object)
     */
    @Override
    public ValueElement<VoidType, ?> asValue(Object value) {
        throw new IllegalArgumentException("A void type cannot hold any value");
    }
    
}
