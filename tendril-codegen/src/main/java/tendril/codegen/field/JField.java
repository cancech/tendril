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
import tendril.codegen.VisibilityType;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.Type;
import tendril.codegen.field.value.JValue;

/**
 * Representation of an instance field in a class
 * 
 * @param <DATA_TYPE> indicating the type of data that is to be stored in the field
 */
public class JField<DATA_TYPE extends Type> extends NamedType<DATA_TYPE> {
    /** The visibility of the field */
    private final VisibilityType visibility;
    /** The value applied to the field */
    private JValue<DATA_TYPE, ?> value;
    
    /**
     * CTOR with no field applied
     * 
     * @param visibility {@link VisibilityType} of the field
     * @param type DATA_TYPE indicating what type of data is stored in the field
     * @param name {@link String} of the field
     */
    public JField(VisibilityType visibility, DATA_TYPE type, String name) {
        this(visibility, type, name, null);
    }

    /**
     * CTOR with a field applied. If {@code null} is passed as the field value, it will be treated as "not initialized"
     * 
     * @param visibility {@link VisibilityType} of the field
     * @param type DATA_TYPE indicating what type of data is stored in the field
     * @param name {@link String} of the field
     * @param value {@link JValue} representing the value to apply to the field.
     */
    public JField(VisibilityType visibility, DATA_TYPE type, String name, JValue<DATA_TYPE, ?> value) {
        super(type, name);
        this.visibility = visibility;
        this.value = value;
    }
    
    /**
     * @see tendril.codegen.field.NamedType#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof JField))
            return false;
        
        @SuppressWarnings("unchecked")
        JField<DATA_TYPE> otherField = (JField<DATA_TYPE>) other;
        if (visibility != otherField.visibility)
            return false;
        
        if (value == null) {
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
        
        String code = visibility.toString();
        if (!code.isEmpty())
            code += " ";
        code += type.getSimpleName() + " " + name;
        
        if (value == null)
            return code + ";";
            
        return code + " = " + value.generate(classImports) + ";";
    }

}
