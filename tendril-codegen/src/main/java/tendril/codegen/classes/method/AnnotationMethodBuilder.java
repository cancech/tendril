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
package tendril.codegen.classes.method;

import tendril.codegen.VisibilityType;
import tendril.codegen.classes.ClassBuilder;
import tendril.codegen.classes.MethodBuilder;
import tendril.codegen.field.type.Type;
import tendril.codegen.field.value.JValue;

/**
 * Builder for creating annotation methods
 * 
 * @param <RETURN_TYPE> indicating the return {@link Type} of the method
 */
public class AnnotationMethodBuilder<RETURN_TYPE extends Type> extends InterfaceMethodBuilder<RETURN_TYPE> {

    /** The default value to apply to the annotation attribute */
    private JValue<RETURN_TYPE, ?> defaultValue = null;

    /**
     * CTOR - for use when creating an arbitrary method
     * 
     * @param name         {@link String} the name of the method
     */
    public AnnotationMethodBuilder(String name) {
        this(null, name);
    }

    /**
     * CTOR - for use when creating methods nested within a class being defined
     * 
     * @param classBuilder {@link ClassBuilder} building the class to which the method belongs
     * @param name         {@link String} the name of the method
     */
    public AnnotationMethodBuilder(ClassBuilder classBuilder, String name) {
        super(classBuilder, name);
        setVisibility(VisibilityType.PUBLIC);
    }

    /**
     * @see tendril.codegen.classes.MethodBuilder#addCode(java.lang.String[])
     */
    @Override
    public MethodBuilder<RETURN_TYPE> addCode(String... lines) {
        throw new IllegalArgumentException("Annotation attributes cannot have any implemnetation");
    }

    /**
     * @see tendril.codegen.classes.MethodBuilder#emptyImplementation()
     */
    @Override
    public MethodBuilder<RETURN_TYPE> emptyImplementation() {
        throw new IllegalArgumentException("Annotation attributes cannot have any implemnetation");
    }

    /**
     * @see tendril.codegen.classes.MethodBuilder#setDefaultValue(tendril.codegen.field.value.JValue)
     */
    @Override
    public MethodBuilder<RETURN_TYPE> setDefaultValue(JValue<RETURN_TYPE, ?> value) {
        defaultValue = value;
        return this;
    }

    /**
     * Annotation methods follow the same rules as interfaces, with the added restrictions:
     * 
     * <ul>
     * <li>Methods cannot be void</li>
     * </ul>
     * 
     * @see tendril.codegen.classes.method.InterfaceMethodBuilder#validate()
     */
    @Override
    protected void validate() {
        super.validate();
        
        if (type.isVoid())
            throw new IllegalArgumentException("Annotation methods cannot be void");
        
        if (defaultValue != null) {
            RETURN_TYPE valueType = defaultValue.getType();
            if (!type.isAssignableFrom(valueType))
                throw new IllegalArgumentException("Invalid default value type. Expected " + type + " but " + valueType + " was provided");
        }
    }

    /**
     * @see tendril.codegen.classes.method.InterfaceMethodBuilder#create()
     */
    @Override
    protected JMethodInterface<RETURN_TYPE> create() {
        return new JMethodAnnotation<RETURN_TYPE>(type, name, defaultValue);
    }

}
