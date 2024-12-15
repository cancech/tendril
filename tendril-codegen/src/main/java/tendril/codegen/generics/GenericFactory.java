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
package tendril.codegen.generics;

import tendril.codegen.field.type.ClassType;

/**
 * 
 */
public class GenericFactory {
    
    public static GenericType createFixedWildcard() {
        return new GenericType();
    }
    
    public static GenericType createFixed(String name) {
        return new GenericType(name);
    }
    
    public static GenericType createFixed(ClassType type) {
        return new GenericType(type);
    }
}
