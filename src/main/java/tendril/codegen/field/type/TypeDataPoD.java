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

import tendril.dom.type.core.PoDType;
import tendril.dom.type.value.ValueElement;

/**
 * Representation of plain ol' data types
 */
class TypeDataPoD extends TypeData<PoDType> {

    /**
     * CTOR
     * 
     * @param type {@link PoDType} indication which POD to represent
     */
    TypeDataPoD(PoDType type) {
        super(type, type.toString());
    }

    /**
     * @see tendril.codegen.field.type.TypeData#asValue(java.lang.Object)
     */
    @Override
    public ValueElement<PoDType, ?> asValue(Object value) {
        switch (type) {
            case BOOLEAN:
                return new ValueElement<PoDType, Boolean>(PoDType.BOOLEAN, (Boolean) value);
            case BYTE:
                return new ValueElement<PoDType, Byte>(PoDType.BYTE, (Byte) value);
            case CHAR:
                return new ValueElement<PoDType, Character>(PoDType.CHAR, (Character) value);
            case DOUBLE:
                return new ValueElement<PoDType, Double>(PoDType.DOUBLE, (Double) value);
            case FLOAT:
                return new ValueElement<PoDType, Float>(PoDType.FLOAT, (Float) value);
            case INT:
                return new ValueElement<PoDType, Integer>(PoDType.INT, (Integer) value);
            case LONG:
                return new ValueElement<PoDType, Long>(PoDType.LONG, (Long) value);
            case SHORT:
                return new ValueElement<PoDType, Short>(PoDType.SHORT, (Short) value);
            default:
                throw new IllegalArgumentException("Invalid type specified: " + type);
        }
    }

}
