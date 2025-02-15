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
package tendril.bean.recipe;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import tendril.context.ApplicationContext;
import tendril.context.Engine;

/**
 * Annotation that is to be applied to generated Recipes. This is used to create a full list of all recipes that are to be registered, allowing for them to be easily
 * and efficiently found and loaded by the {@link ApplicationContext} {@link Engine}.
 * 
 * This is not intended to be used by any client code, unless manually creating the bean infrastructure which is heavily discouraged.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Registry {

}
