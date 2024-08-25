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
package tendril.bean.qualifier;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks an {@link Enum} as a source for Bean IDs. The annotation processing will generate the appropriate ID annotation for the annotated {@link Enum} which can then be employed to qualify bean an
 * mark them as having entries from the annotated {@link Enum} as an ID.
 * <p>Ex: {@code MyEnum} when annotated will produce a {@code MyEnumId} annotation which will take a {@code MyEnum} entry as a value, and can be used to apply {@code MyEnum} as an ID to beans</p> 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface BeanIdEnum {

}
