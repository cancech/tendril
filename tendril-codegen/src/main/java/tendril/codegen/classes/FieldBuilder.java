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

import tendril.codegen.field.JField;
import tendril.codegen.field.type.Type;
import tendril.codegen.field.value.JValue;

/**
 * Builder which can be used for the purpose of creating Field nested within Classes
 */
public class FieldBuilder<DATA_TYPE extends Type> extends NestedClassElementBuilder<DATA_TYPE, JField<DATA_TYPE>, FieldBuilder<DATA_TYPE>> {

    /** Value that is to be applied to the field */
    private JValue<DATA_TYPE, ?> value = null;

    /**
     * CTOR - for use when creating an arbitrary field
     * 
     * @param name {@link String} the name of the field
     */
    public FieldBuilder(String name) {
        this(null, name);
    }

    public FieldBuilder(ClassBuilder classBuilder, String name) {
        super(classBuilder, name);
    }

    /**
     * CTOR - for use when creating field nested within a class being defined
     * 
     * @param classBuilder {@link ClassBuilder} building the class to which the field belongs
     * @param name         {@link String} the name of the field
     */
    public FieldBuilder<DATA_TYPE> setValue(JValue<DATA_TYPE, ?> value) {
        this.value = value;
        return this;
    }

    /**
     * @see tendril.codegen.classes.NestedClassElementBuilder#addToClass(tendril.codegen.classes.ClassBuilder, tendril.codegen.field.JVisibleType)
     */
    @Override
    protected void addToClass(ClassBuilder classBuilder, JField<DATA_TYPE> toAdd) {
        classBuilder.add(toAdd);
    }

    /**
     * @see tendril.codegen.BaseBuilder#create()
     */
    @Override
    protected JField<DATA_TYPE> create() {
        return new JField<>(type, name);
    }

    /**
     * @see tendril.codegen.field.VisibileTypeBuilder#applyDetails(tendril.codegen.field.JVisibleType)
     */
    @Override
    protected JField<DATA_TYPE> applyDetails(JField<DATA_TYPE> element) {
        element.setValue(value);
        return super.applyDetails(element);
    }

}
