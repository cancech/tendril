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

import tendril.bean.Replaces;
import tendril.bean.recipe.ReplacesRegistry;

/**
 * Processor for the {@link Replaces} annotation, which will generate the appropriate Recipe for the specified Provider
 */
@SupportedAnnotationTypes("tendril.bean.Replaces")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
@AutoService(Processor.class)
public class ReplacesProcessor extends AbstractBeanProcessor {

	/**
	 * CTOR
	 */
	public ReplacesProcessor() {
		super(Replaces.class, ReplacesRegistry.class);
	}
}
