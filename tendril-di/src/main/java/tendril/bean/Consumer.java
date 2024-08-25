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
 * Annotation which is used to mark a method or field as a bean consumer, provided that the encompassing class is a bean {@link Provider} in its own right. As part of the initialization of the bean
 * these will be automatically populated with the required bean(s), such that fields will be guaranteed to have the appropriate value (bean) applied, and methods will be called with the appropriate
 * parameters (beans). 
 * <p>Note that a Consumer method is expected to be void as any return will be "lost", and can be considered analogous to a method annotated with PostConstruct.</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.FIELD })
public @interface Consumer {

}
