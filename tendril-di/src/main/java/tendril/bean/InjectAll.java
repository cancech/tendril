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
import java.util.List;

/**
 * Annotation which is used to mark a field into which a {@link List} of beans is to be injected as a bean consumer, provided that the encompassing class is a bean {@link Bean} in its own right. As
 * part of the initialization of the bean these will be automatically populated with the required beans, such that fields will be guaranteed to have the appropriate value (bean) applied. The
 * difference between @InjectAll and @Inject is that where @Inject requires exactly one match, @InjectAll has no such stipulation. All suitable matches will be returned in a {@link List}, and it may
 * be empty if no such match exist. It is up to the client code to make appropriate use and determinations as necessary.
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ ElementType.FIELD })
public @interface InjectAll {
    // TODO allow for parameters to constructors or method to perform InjectAll injection
}
