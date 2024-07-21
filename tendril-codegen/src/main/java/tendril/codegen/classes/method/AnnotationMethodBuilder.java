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
import tendril.codegen.field.type.Type;

/**
 * Builder for creating annotation methods
 * 
 * @param <RETURN_TYPE> indicating the return {@link Type} of the method
 */
public class AnnotationMethodBuilder<RETURN_TYPE extends Type> extends InterfaceMethodBuilder<RETURN_TYPE> {

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

}
