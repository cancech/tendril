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
package tendril.test.processor;

import javax.annotation.processing.Processor;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;

import com.google.auto.service.AutoService;

import tendril.processor.AbstractBeanProcessor;

/**
 * Processor for the {@code TendrilTest} annotation, which will generate a recipe for the test class which is not registered. A test annotated in this manner will have a recipe created for it, however
 * said recipe will only be loaded when the test in question is executed.
 */
@SupportedAnnotationTypes("tendril.test.TendrilTest")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
@AutoService(Processor.class)
public class TestProcessor extends AbstractBeanProcessor {

	/**
	 * CTOR
	 */
	public TestProcessor() {
		super(null);
	}

}
