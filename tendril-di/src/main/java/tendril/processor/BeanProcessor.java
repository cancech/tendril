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

import com.google.auto.service.AutoService;

import tendril.bean.Bean;
import tendril.bean.NoBeanOverrideClass;
import tendril.bean.recipe.Registry;
import tendril.codegen.JBase;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.TypeFactory;

/**
 * Processor for the {@link Bean} annotation, which will generate the appropriate Recipe for the specified Provider
 */
@SupportedAnnotationTypes("tendril.bean.Bean")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
@AutoService(Processor.class)
public class BeanProcessor extends AbstractBeanProcessor {
	/** The type which indicate no override is present */
	private static final ClassType noOverride = TypeFactory.createClassType(NoBeanOverrideClass.class);

	/**
	 * CTOR
	 */
	public BeanProcessor() {
		super(Registry.class);
	}

	/**
	 * @see tendril.processor.AbstractBeanProcessor#getClassOverrideType()
	 */
	@Override
	protected ClassType getClassOverrideType() {
		return getOverride(currentClass);
	}

	/**
	 * @see tendril.processor.AbstractBeanProcessor#getMethodOverrideType()
	 */
	@Override
	protected ClassType getMethodOverrideType() {
		return getOverride(currentMethod);
	}

	/**
	 * Get the override that is applied to the {@link Bean} annotation on the element
	 * 
	 * @param element {@link JBase} where to look for the annotation
	 * @return {@link ClassType} to use as the override or {@code null} if none specified
	 */
	private ClassType getOverride(JBase element) {
		ClassType overrideType = AnnotationHelper.retrieveBeanOverride(element);
		if (overrideType == null || overrideType.equals(noOverride))
			return null;

		return overrideType;
	}
}
