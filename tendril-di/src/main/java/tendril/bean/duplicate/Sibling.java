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
package tendril.bean.duplicate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks an injection as one which is to be a sibling, meaning that it belongs to the same blueprint instance. This means that the same blueprint instance was employed
 * to create the desired injection copy. This only has meaning when employed with a duplicate, outside of duplication this is ignored.
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
public @interface Sibling {
	// TODO Make sure this is used for field as well as parameter injection
	// TODO Allow for sibling injection
	// TODO Allow for config duplicates
	// TODO warning when placed on a @Bean
}
