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
 * 
 * By default the replacement bean will registered as the "type" of what it is applied to (namely the class it is applied to or to return type of the method), though this can be overridden through the
 * attribute. {@code @Replaces (MyOverride.class)} will override the default type with {@code MyOverride.class}. As is the case for @{@link Bean}, the override must be a parent class/interface of the
 * real instance, with the added requirement that the override must be in effect a "common ancestor" of the both the original bean being replaced (or more accurately what it was advertised as) and the
 * advertised replacement bean type.
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface Replaces {

	/**
	 * Optionally specify a class/type that the replacement bean should be "advertised" under. Meaning that regardless of what the actual type of the replacement bean is, it will be made available
	 * purely under the override type. If not specified, it will default to the type as defined in the code (i.e.: the class it is applied to or to return type of the method). Note that the override
	 * must be a parent of the "default" type as well as the (advertised) type of the original bean it is replacing.
	 * 
	 * @return {@link Class} to use for the override
	 */
	Class<?> value() default NoBeanOverrideClass.class;
}
