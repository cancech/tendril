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

import tendril.codegen.classes.JClass;
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
     * CTOR
     * 
     * @param encompassingClass {@link JClass} which contain the method
     * @param returnType        RETURN_TYPE representing what the method returns
     * @param name              {@link String} the name of the method
     */
	public AnnotationMethodBuilder(JClass encompassingClass, RETURN_TYPE returnType, String name) {
		super(encompassingClass, returnType, name);
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
	    RETURN_TYPE valueType = value.getType();
	    if (!returnType.isAssignableFrom(valueType))
	        throw new IllegalArgumentException("Invalid value type. Expected " + returnType + " but " + valueType + " was provided");
	    defaultValue = value;
	    return this;
	}

	/**
	 * Annotation methods follow the same rules as interfaces, with the added restrictions:
	 * 
	 * <ul>
	 *     <li>Methods cannot be void</li>
	 * </ul>
	 * 
	 * @see tendril.codegen.classes.MethodBuilder#validateData()
	 */
	@Override
	protected void validateData() throws IllegalArgumentException {
	    if (returnType.isVoid())
	        throw new IllegalArgumentException("Annotation methods cannot be void");
	    
	    super.validateData();
	}
	
	/**
	 * @see tendril.codegen.classes.method.InterfaceMethodBuilder#buildMethod(tendril.codegen.field.type.Type, java.lang.String)
	 */
	@Override
	protected JMethod<RETURN_TYPE> buildMethod(RETURN_TYPE returnType, String name) {
	    return new JMethodAnnotation<RETURN_TYPE>(returnType, name, defaultValue);
	}

}
