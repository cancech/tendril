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
package tendril.codegen.field.value;

import java.util.List;
import java.util.Set;

import tendril.codegen.field.type.ArrayType;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.Type;
import tendril.util.TendrilStringUtil;

/**
 * Representation of an array value
 * 
 * 
 * @param <DATA_TYPE> representing the {@link Type} of the value
 * @param <VALUE_TYPE> the specific (Java) type storing the values in the array
 */
public class JValueArray<DATA_TYPE extends Type, VALUE_TYPE> extends JValue<ArrayType<DATA_TYPE>, List<JValue<DATA_TYPE, VALUE_TYPE>>> {

    /**
     * CTOR
     * 
     * @param dataType DATA_TYPE representing what it contained within the array
     * @param values {@link List} of {@link JValue}s representing all of the elements to appear in the array
     */
    JValueArray(DATA_TYPE dataType, List<JValue<DATA_TYPE, VALUE_TYPE>> values) {
        super(new ArrayType<>(dataType), values);
    }

    /**
     * @see tendril.codegen.field.value.JValue#generate(java.util.Set)
     */
    @Override
    public String generate(Set<ClassType> classImports) {
        String result = "{";
        for (int i = 0; i < value.size(); i++) {
            result += value.get(i).generate(classImports);
            if (i < value.size() - 1)
                result += ", ";
        }

        return result + "}";
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Array [" + TendrilStringUtil.join(getValue()) + "]";
    }
}
