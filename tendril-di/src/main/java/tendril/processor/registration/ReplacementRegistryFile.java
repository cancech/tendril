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

import java.io.IOException;
import java.util.Set;

import tendril.context.ApplicationContext;

/**
 * Class for loading and processing the tendril replacement registry file on {@link ApplicationContext} start
 */
public class ReplacementRegistryFile {

	/** The path where to find the registry file */
	public static String PATH = "META-INF/tendril/replacementregistry";

	/**
	 * Reads the replacement registry file and returns a list of all recipes that have been registered as replacements to other pre-existing recipes
	 * 
	 * @return {@link Set} of {@link String}s containing the registered replacement recipes
	 * @throws IOException if there is an issue opening the file
	 */
	public static Set<String> read() throws IOException {
		return RegistryFileHelper.read(PATH);
	}

	/**
	 * CTOR - should only ever be used as a static class
	 */
	private ReplacementRegistryFile() {
	}
}
