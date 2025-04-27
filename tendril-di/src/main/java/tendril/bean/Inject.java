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
 * Annotation which is used to mark a method or field into which a bean is to be injected as a bean consumer, provided that the encompassing class is a bean {@link Bean} in its own right. As 
 * part of the initialization of the bean these will be automatically populated with the required bean(s), such that fields will be guaranteed to have the appropriate value (bean) applied, and 
 * methods will be called with the appropriate parameters (beans). When this is employed, then every @Inject field/parameter must resolve to exactly one bean, with an exception being thrown if
 * either no suitable bean was found, or more than one.
 *  
 * <p>Note that an injected (consumer) method is expected to be void as any return will be "lost", and can be considered analogous to a method annotated with PostConstruct.</p>
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.FIELD })
public @interface Inject {
}
