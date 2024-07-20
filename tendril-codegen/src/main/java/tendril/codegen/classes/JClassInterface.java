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

import tendril.codegen.VisibilityType;
import tendril.codegen.classes.method.InterfaceMethodBuilder;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.Type;

/**
 * Representation of an interface
 */
public class JClassInterface extends JClass {

	/**
	 * CTOR
	 * 
	 * @param visibility {@link VisibilityType} what the visibility of the class is
	 * @param data       {@link ClassType} the information about the class
	 */
	protected JClassInterface(VisibilityType visibility, ClassType data) {
		super(visibility, data);
	}

	/**
	 * @see tendril.codegen.classes.JClass#classType()
	 */
	@Override
	protected String classType() {
		return "interface";
	}

	/**
	 * @see tendril.codegen.classes.JClass#createMethodBuilder(tendril.codegen.field.type.TypeData, java.lang.String)
	 */
	@Override
	protected <RETURN_TYPE extends Type> MethodBuilder<RETURN_TYPE> createMethodBuilder(RETURN_TYPE returnType, String name) {
		return new InterfaceMethodBuilder<RETURN_TYPE>(this, returnType, name);
	}

}
