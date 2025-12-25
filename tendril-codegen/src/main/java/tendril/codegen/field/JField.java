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

import java.util.Set;

import tendril.codegen.CodeBuilder;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.Type;
import tendril.codegen.field.value.JValue;

/**
 * Representation of an instance field in a class
 * 
 * @param <DATA_TYPE> indicating the type of data that is to be stored in the field
 */
public class JField<DATA_TYPE extends Type> extends JVisibleType<DATA_TYPE> {
    /** The value applied to the field */
    private JValue<DATA_TYPE, ?> value = null;
    /** Custom code to be applied to initialize the field */
    private String customInitialization = "";

    /**
     * CTOR with no field applied
     * 
     * @param type DATA_TYPE indicating what type of data is stored in the field
     * @param name {@link String} of the field
     */
    public JField(DATA_TYPE type, String name) {
        super(type, name);
    }

    /**
     * Apply the specified value to the field
     * 
     * @param value {@link JValue}
     */
    public void setValue(JValue<DATA_TYPE, ?> value) {
        this.value = value;
    }

    /**
     * Get the value that is applied to the field
     * 
     * @return {@link JValue}
     */
    public JValue<DATA_TYPE, ?> getValue() {
        return value;
    }

    /**
     * Apply the specified custom initialization code to the field
     * 
     * @param code {@link String} to be used to initialize the field
     */
    public void setCustomInitialization(String code) {
        this.customInitialization = code;
    }
    
    /**
     * Get the custom initialization for the field
     * 
     * @return {@link String} the code through which to initialize the field
     */
    public String getCustomInitialization() {
    	return customInitialization;
    }

    /**
     * @see tendril.codegen.field.JType#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof JField))
            return false;

        JField<?> otherField = (JField<?>) other;
        if (customInitialization != otherField.customInitialization)
        	return false;
        else if (value == null) {
            if (otherField.value != null)
                return false;
        } else if (!value.equals(otherField.value))
            return false;

        return super.equals(other);
    }

    /**
     * @see tendril.codegen.JBase#appendSelf(tendril.codegen.CodeBuilder, java.util.Set)
     */
    @Override
    protected void appendSelf(CodeBuilder builder, Set<ClassType> classImports) {
        builder.append(generateSelf(classImports));
    }

    /**
     * @see tendril.codegen.JBase#generateSelf(java.util.Set)
     */
    @Override
    public String generateSelf(Set<ClassType> classImports) {
        type.registerImport(classImports);

        String code = visibility.getKeyword() + getStaticKeyword() + getFinalKeyword();
        code += type.getSimpleName() + getGenericsApplicationKeyword(true) + name;

        if (value != null)
            code += " = " + value.generate(classImports);
        else if (!customInitialization.isEmpty())
        	code += " = " + customInitialization;

        return code + ";";
    }

}
