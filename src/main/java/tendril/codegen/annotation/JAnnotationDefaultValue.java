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
package tendril.codegen.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

import tendril.codegen.CodeBuilder;
import tendril.codegen.field.JValue;
import tendril.dom.type.core.ClassType;

/**
 * Annotation which takes a default {@code value} parameter
 */
public class JAnnotationDefaultValue extends JAnnotation {
	/** The value to apply to the default parameter */
	private final JValue<?> value;

	/**
	 * CTOR
	 * 
	 * @param klass {@link Class} extending {@link Annotation} defining the annotation
	 * @param value {@link JValue} containing the value to apply to the default parameter
	 */
	public JAnnotationDefaultValue(Class<? extends Annotation> klass, JValue<?> value) {
		super(klass);
		this.value = value;
		
		// Make sure that the annotation is actually one which takes a default value
		Method[] methods = klass.getDeclaredMethods();
		if (methods.length != 1 || !"value".equals(methods[0].getName()))
			throw new IllegalArgumentException(getName() + " annotation must have exactly one parameter named value");
	}

	/**
	 * @see tendril.codegen.BaseElement#generateSelf(tendril.codegen.CodeBuilder, java.util.Set)
	 */
	@Override
	protected void generateSelf(CodeBuilder builder, Set<ClassType> classImports) {
		builder.append(name + "(" + value.generate(classImports) + ")");
	}

}
