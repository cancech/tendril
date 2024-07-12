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
import java.util.Set;

import tendril.codegen.CodeBuilder;
import tendril.dom.type.core.ClassType;

/**
 * Annotation which takes no parameters, merely acting as
 */
public class JAnnotationMarker extends JAnnotation {

	/**
	 * CTOR
	 * 
	 * @param klass {@link Class} extending {@link Annotation} defining the annotation
	 */
	public JAnnotationMarker(Class<? extends Annotation> klass) {
		super(klass);

		// Make sure that the interface is in face a marker
		if (klass.getDeclaredMethods().length != 0)
			throw new IllegalArgumentException(getName() + " is not a marker annotation, it has parameters.");
	}

	/**
	 * @see tendril.codegen.BaseElement#generateSelf(tendril.codegen.CodeBuilder, java.util.Set)
	 */
	@Override
	protected void generateSelf(CodeBuilder builder, Set<ClassType> classImports) {
		builder.append(name);
	}
}
