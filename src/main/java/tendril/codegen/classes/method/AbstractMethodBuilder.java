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
import tendril.codegen.classes.JClass;
import tendril.codegen.classes.MethodBuilder;
import tendril.codegen.field.type.TypeData;
import tendril.dom.method.MethodElement;
import tendril.dom.type.Type;

/**
 * Builder for creating abstract methods
 * 
 * @param <RETURN_TYPE> the {@link Type} the method returns
 */
public class AbstractMethodBuilder<RETURN_TYPE extends Type> extends MethodBuilder<RETURN_TYPE> {

    /**
     * CTOR
     * 
     * @param encompassingClass {@link JClass} which contain the method
     * @param returnType        {@link TypeData} representing what the method returns
     * @param name              {@link String} the name of the method
     */
	public AbstractMethodBuilder(JClass encompassingClass, TypeData<RETURN_TYPE> returnType, String name) {
		super(encompassingClass, returnType, name);
	}

	/**
	 * Method must either have an implementation or or not be private
	 *  
	 * @see tendril.codegen.classes.MethodBuilder#validateData()
	 */
	@Override
	protected void validateData() throws IllegalArgumentException {
		if (VisibilityType.PRIVATE == visibility && !hasCode())
			throw new IllegalArgumentException("An abstract method cannot be private");
	}

	/**
	 * @see tendril.codegen.classes.MethodBuilder#buildMethod(tendril.dom.method.MethodElement)
	 */
	@Override
	protected JMethod<RETURN_TYPE> buildMethod(MethodElement<RETURN_TYPE> methodElement) {
		return new JMethodDefault<RETURN_TYPE>(visibility, methodElement, linesOfCode);
	}

}
