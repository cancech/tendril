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
package tendril.bean.requirement;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to indicate what type of (system) property is required to be set/applied to a context for the annotated bean to be allowed to be created. This can be used to limit under what circumstances a
 * bean can be made available. Unlike {@link RequiresProp}, where all of the listed (system) environments must be present, here at least one of the listed environments must be present (ergo
 * {@link RequiresProp} is an {@code and} whereas {@link RequiresOneOfProp} is an {@code or} condition).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface RequiresOneOfProp {

	/**
	 * Array of different properties, at least one of which must be met in order for the bean to be deemed valid in the execution environment.
	 * 
	 * @return {@link String} array of required properties
	 */
	String[] value();
}
