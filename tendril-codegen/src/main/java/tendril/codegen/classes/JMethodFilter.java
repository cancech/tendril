/*
 * Copyright 2025 Jaroslav Bosak
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
package tendril.codegen.classes;

import tendril.codegen.classes.method.JMethod;

/**
 * Interface through which to perform method filtration on any arbitrary criteria as implemented in the {@code matches} override
 */
public interface JMethodFilter {

	/**
	 * Perform the necessary check to determine whether the specific {@link JMethod} matches the desired filter
	 * 
	 * @param methodToCheck {@link JMethod} to check
	 * @return boolean true if the specified method matches the desired filter
	 */
	boolean matches(JMethod<?> methodToCheck);
}
