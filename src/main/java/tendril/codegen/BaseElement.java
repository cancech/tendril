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
package tendril.codegen;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import tendril.codegen.annotation.JAnnotation;
import tendril.codegen.annotation.JAnnotationFactory;
import tendril.codegen.field.JValue;
import tendril.dom.type.core.ClassType;

/**
 * The base of any element that is to be part of the generated code.
 */
public abstract class BaseElement {
	/** The name of the element */
	protected final String name;
	/** List of annotations that are applied to the element */
	private final List<JAnnotation> annotations = new ArrayList<>();

	/**
	 * CTOR
	 * 
	 * @param name {@link String} the name of the element
	 */
	protected BaseElement(String name) {
		this.name = name;
	}
	
	/**
	 * Get the name of the element
	 * 
	 * @return {@link String}
	 */
	public String getName() {
		return name;
	}

	/**
	 * Add an annotation derived from the specified {@link Class}
	 * 
	 * @param annotation {@link Class} extending {@link Annotation} representing the annotation to apply
	 */
	public void annotate(Class<? extends Annotation> annotation) {
		annotations.add(JAnnotationFactory.create(annotation));
	}

	/**
	 * Add an annotation derived from the specified {@link Class}
	 * 
	 * @param annotation {@link Class} extending {@link Annotation} representing the annotation to apply
	 * @param value      {@link JValue} parameter for the annotation (must be named "value")
	 */
	public void annotate(Class<? extends Annotation> annotation, JValue<?> value) {
		annotations.add(JAnnotationFactory.create(annotation, value));
	}

	/**
	 * Add an annotation derived from the specified {@link Class}
	 * 
	 * @param annotation {@link Class} extending {@link Annotation} representing the annotation to apply
	 * @param parameters {@link Map} of {@link String} parameter names to their applied {@link JValue}s
	 */
	public void annotate(Class<? extends Annotation> annotation, Map<String, JValue<?>> parameters) {
		annotations.add(JAnnotationFactory.create(annotation, parameters));
	}

	/**
	 * Generate the code for the element. Performs the common code generation, relying on {@code generateSelf()} to perform the specific code generation for this specific element.
	 * 
	 * @param builder      {@link CodeBuilder} which is assembling/building the code
	 * @param classImports {@link Set} of {@link ClassType}s representing the imports for the code
	 */
	public void generate(CodeBuilder builder, Set<ClassType> classImports) {
		for (JAnnotation annon : annotations)
			annon.generate(builder, classImports);
		generateSelf(builder, classImports);
	}

	/**
	 * Generate the appropriate code that is specific and unique to this element. {@code generate()} takes care of the common portions of code generation, with this method performing what is unique to
	 * this particular element.
	 * 
	 * @param builder      {@link CodeBuilder} which is assembling/building the code
	 * @param classImports {@link Set} of {@link ClassType}s representing the imports for the code
	 */
	protected abstract void generateSelf(CodeBuilder builder, Set<ClassType> classImports);
}
