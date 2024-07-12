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
package tendril.codegen.field;

import java.util.List;
import java.util.Set;

import tendril.dom.type.core.ClassType;

/**
 * Representation of an array value
 * 
 * @param <TYPE> the type of element to be stored in the array
 */
public class JValueArray<TYPE> extends JValue<List<JValue<TYPE>>> {

    /**
     * CTOR
     * 
     * @param values {@link List} of {@link JValue}s representing all of the elements to appear in the array
     */
    protected JValueArray(List<JValue<TYPE>> values) {
        super(values);
    }

    /**
     * @see tendril.codegen.field.JValue#generate(java.util.Set)
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
}
