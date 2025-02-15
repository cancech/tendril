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
package tendril.bean;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation which is used to mark a class or method as a bean provider. When applied to a class it is used wholesale as the source of a bean (populating the constructor as necessary to create the
 * instance), whereas when applied to a method, the encompassing class is first initialized and the result returned from the method added as a bean. The encompassing class will not be provided as a
 * bean, unless it is itself appropriately annotated as such. In this manner a bean can be the source of beans, or merely created and its nested beans extracted before discarding the encompassing 
 * class.
 * <p>Note that a Provider method must return something, and can be considered analogous to a factory method.</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface Bean {

}
