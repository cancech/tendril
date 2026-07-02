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
package tendril.test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.extension.ExtendWith;

import tendril.bean.Bean;
import tendril.context.ApplicationContext;
import tendril.junit5.TendrilTestExtension;

/**
 * Annotation which is used to mark a class or method as a unit test. It will allow the test to be treated as a {@link Bean}, as in generate a recipe for it through which the various injections can be
 * performed, however it will not be registered and thus cannot be injected as a dependency in another bean. This is only intended to be used to annotated a unit test class, so that the test can be
 * injected.
 * 
 * To be applied to a unit test class which is to execute within an {@link ApplicationContext}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@ExtendWith(TendrilTestExtension.class)
public @interface TendrilTest {

	/**
	 * Optionally can be used to define the environments that are to be applied to the {@link ApplicationContext} for the test
	 * 
	 * @return {@link String}[] with the environments (empty by default)
	 */
	String[] environments() default {};

	/**
	 * Optionally can be used to define the properties that are to be applied to the {@link ApplicationContext} for the test
	 * 
	 * @return {@link String}[] with the properties (empty by default)
	 */
	String[] properties() default {};
}
