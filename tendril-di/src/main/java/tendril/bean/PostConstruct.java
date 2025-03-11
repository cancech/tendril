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

import tendril.processor.BeanProcessor;

/**
 * Annotation to be applied to a bean method (with no arguments) to indicate that it should be called after the instance has been created, but before providing it onward.
 * Any methods annotated in this manner will not be called until after the all injection has been completed. In order for a method to be successfully called during PostConstruct
 * it must follow a few rules:
 * <ol>
 *      <li>The method must not take any parameters</li>
 *      <li>The method must be void</li>
 *      <li>The method must not be private</li>
 * </ol>
 * 
 * If any of the above rules are not met, then the {@link BeanProcessor} will throw an exception and fail annotation processing
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface PostConstruct {

}
