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

import tendril.codegen.BaseBuilder;
import tendril.codegen.field.type.Type;

/**
 * Builder for applying a Data Type to the element
 * 
 * @param <DATA_TYPE> extending {@link Type} the element represents
 * @param <ELEMENT>   extending {@link JType} the builder produces
 * @param <BUILDER>   indicating which specific child builder is being employed
 */
public abstract class TypeBuilder<DATA_TYPE extends Type, ELEMENT extends JType<DATA_TYPE>, BUILDER extends TypeBuilder<DATA_TYPE, ELEMENT, BUILDER>> extends BaseBuilder<ELEMENT, BUILDER> {

    /** The data type to apply to the produced element */
    protected DATA_TYPE type = null;

    /**
     * CTOR
     * 
     * @param name {@link String} of the defined element
     */
    public TypeBuilder(String name) {
        super(name);
    }

    /**
     * Set the specific data type of the element
     * 
     * @param type DATA_TYPE of the element
     */
    public void setType(DATA_TYPE type) {
        this.type = type;
    }

    /**
     * @see tendril.codegen.BaseBuilder#validate()
     */
    @Override
    protected void validate() {
        if (type == null)
            throw new IllegalArgumentException("A valid type must be specified");
    }
}
