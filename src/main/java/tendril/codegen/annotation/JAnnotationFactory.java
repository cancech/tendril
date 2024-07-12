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
import java.util.Map;

import tendril.codegen.field.JValue;

/**
 * Factory to facilitate the creation of annotations, based on the specified parameters
 */
public class JAnnotationFactory {

	/**
	 * Creates a marker annotation, which takes no parameters
	 * 
	 * @param annotation {@link Class} extends {@link Annotation} defining the annotation to create
	 * @return {@link JAnnotation} for the marker annotation
	 */
	public static JAnnotation create(Class<? extends Annotation> annotation) {
		return new JAnnotationMarker(annotation);
	}

	/**
	 * Creates an annotation, which takes a single default (value) parameter
	 * 
	 * @param annotation {@link Class} extends {@link Annotation} defining the annotation to create
	 * @param value      {@link JValue} representing the default value for the annotation
	 * @return {@link JAnnotation} for the annotation with a default value
	 */
	public static JAnnotation create(Class<? extends Annotation> annotation, JValue<?> value) {
		return new JAnnotationDefaultValue(annotation, value);
	}

	/**
	 * Creates an annotation, which takes arbitrary parameters
	 * 
	 * @param annotation {@link Class} extends {@link Annotation} defining the annotation to create
	 * @param parameters {@link Map} of {@link String} parameter names to their {@link JValue} values
	 * @return {@link JAnnotation} for the annotation
	 */
	public static JAnnotation create(Class<? extends Annotation> annotation, Map<String, JValue<?>> parameters) {
		return new JAnnotationFull(annotation, parameters);
	}
}
