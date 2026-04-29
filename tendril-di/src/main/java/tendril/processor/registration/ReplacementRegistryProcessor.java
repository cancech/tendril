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
package tendril.processor.registration;

import javax.annotation.processing.Processor;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;

import com.google.auto.service.AutoService;

import tendril.bean.recipe.ReplacesRegistry;

/**
 * Annotation processor for recipes which are annotated with @{@link ReplacesRegistry} and are to be added to the recipe replacement registry
 */
@SupportedAnnotationTypes("tendril.bean.recipe.ReplacesRegistry")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
@AutoService(Processor.class)
public class ReplacementRegistryProcessor extends AbstractRegistryProcessor {
	
	public ReplacementRegistryProcessor() {
		super("Replaces Registry", ReplacementRegistryFile.PATH);
	}
}
