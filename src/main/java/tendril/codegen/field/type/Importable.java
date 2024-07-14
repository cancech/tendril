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
package tendril.codegen.field.type;

import java.util.Set;

/**
 * Indicates that a construct contains a reference to an element which needs to be imported when used in a class/code.
 */
public interface Importable {

    /**
     * Add the {@link ClassType} of what needs to be imported to the {@link Set} of all imports for the enclosing class
     * 
     * @param classImports {@link Set} of {@link ClassType} where all imports for the enclosing class are stored
     */
    void registerImport(Set<ClassType> classImports);
}
