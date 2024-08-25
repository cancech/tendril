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

/**
 * To be applied to {@link Enum}s which are to be used as Bean IDs (i.e.: annotated with {@link BeanIdEnum}), to ensure consistent interaction and usage across different Bean ID {@link Enum}s.
 */
public interface BeanId {
    
    /**
     * Get the ID to be applied to the Bean. The default implementation generates the ID from the {@link Enum} characteristics, such that the ID becomes: {@code <fully qualified class name>.<enum name>}.
     * <p>Ex: The ID for {@code MyEnum.ABC123} becomes {@code package.containing.MyEnum.ABC123}</p>
     * <p>This can be overridden by the concrete implementation is something different is desired.</p>
     * 
     * @return {@link String} the ID for the bean
     */
    default String getId() {
        return getClass().getName() + "." + name();
    }

    /**
     * Get the name of the element which is representing the ID. As this is intended to be used with {@link Enum}s, the {@link Enum} provide {@code name()} should provide the necessary implementation.
     * 
     * @return {@link String} the name of the ID representation
     */
    String name();
}
