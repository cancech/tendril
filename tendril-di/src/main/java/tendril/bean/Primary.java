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
package tendril.bean;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation which is used to mark a bean as a {@code primary} bean. A primary bean is one which is to be used when there is more than one bean matching the search
 * criteria. This only applies when searching for one specific bean. For example, if five beans match a given search and one is marked as {@code primary}, then the 
 * {@code primary} one will be returned and no exception will be generated. If multiple {@code primary} beans are found when searching for a single bean, an exception
 * will still be thrown. Conversely when searching for multiple beans the primacy of a bean will be ignored.
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface Primary {

}
