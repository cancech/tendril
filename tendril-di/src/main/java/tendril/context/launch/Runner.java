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
package tendril.context.launch;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import tendril.context.ApplicationContext;

/**
 * Annotation to be applied to a single class in the client code, to indicate the specific entry point into the {@link ApplicationContext}. The annotated class
 * must implement the {@link TendrilRunner} interface, and otherwise it can be treated as any other Consumer. No additional annotation should be applied to the class,
 * however for the purpose of assembly the regular bean consumption rules will be applied (i.e.: constructor, field, method, etc). The class cannot however provide any
 * beans as it is purely intended as an entry mechanism.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Runner {

}
