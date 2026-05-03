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
 * Annotation which is used to mark a class or method as a bean provider, specifically one which is to replace another existing bean. Which bean specifically is to be replaced is determined by the
 * other annotations (namely qualifiers) that are applied alongside {@code @Replaces}. Much like is the case with {@link Inject}, the various qualifying annotations must resolve to exactly one bean.
 * An exception will be thrown if either no "original" bean can be resolved, or if it resolves to more than one.
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface Replaces {
}
