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

import java.util.List;
import java.util.Set;

import tendril.codegen.field.type.ClassType;

/**
 * 
 */
public abstract class CompoundClassTypeGeneric extends CompoundGeneric<ClassType> {

    /**
     * @param name
     * @param parents
     */
    CompoundClassTypeGeneric(String name, List<ClassType> parents) {
        super(name, parents);
    }
    
    /**
     * @see tendril.codegen.generics.GenericType#registerImport(java.util.Set)
     */
    @Override
    public void registerImport(Set<ClassType> classImports) {
        classImports.addAll(parents);
    }

    /**
     * @see tendril.codegen.generics.CompoundGeneric#asClassType(java.lang.Object)
     */
    @Override
    protected ClassType asClassType(ClassType type) {
        return type;
    }

    /**
     * @see tendril.codegen.generics.CompoundGeneric#asCode(java.lang.Object)
     */
    @Override
    protected String asCode(ClassType type) {
        return type.getSimpleName();
    }

    
}
