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
package tendril.processor;

import javax.annotation.processing.Processor;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;

import com.google.auto.service.AutoService;

import tendril.annotationprocessor.exception.TendrilException;
import tendril.bean.Replaces;
import tendril.bean.recipe.ReplacesRegistry;
import tendril.codegen.annotation.JAnnotation;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.TypeFactory;

/**
 * Processor for the {@link Replaces} annotation, which will generate the appropriate Recipe for the specified Provider
 */
@SupportedAnnotationTypes("tendril.bean.Replaces")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
@AutoService(Processor.class)
public class ReplacesProcessor extends AbstactBeanProcessor {

	/**
	 * CTOR
	 */
	public ReplacesProcessor() {
		super(ReplacesRegistry.class);
	}

	
	@Override
	protected void validateType(TypeElement type) throws TendrilException {
		super.validateType(type);
		
		Replaces annon = type.getAnnotation(Replaces.class);
		try {
			System.err.println("REPLACE VALUE: " + annon.value());
		} catch (MirroredTypeException ex) {
			System.err.println("REPLACE EX: " + ex.getTypeMirror());
		}
	}
	
	@Override
	protected void validateClass() throws TendrilException {
		ClassType replacesAnnotation = TypeFactory.createClassType(Replaces.class);
		for (JAnnotation a: currentClass.getAnnotations()) {
			System.err.println("==========VALIDATING: " + a);
			// TODO allow for specific annotation to be retrieved
			if (a.getType().equals(replacesAnnotation)) {
				// TODO allow for specific value to be retrieved
				System.err.println("=========REPLACE VALUE: " + a.getValue(a.getAttributes().get(0)).getValue());
				break;
			}
		}
		super.validateClass();
	}
}
