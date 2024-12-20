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

import tendril.codegen.classes.JClass;
import tendril.codegen.field.type.ClassType;

/**
 * 
 */
public abstract class CompoundJClassGeneric extends CompoundGeneric<JClass> {

    /**
     * @param name
     * @param parents
     */
    CompoundJClassGeneric(String name, List<JClass> parents) {
        super(name, parents);
    }
    
    /**
     * @see tendril.codegen.generics.GenericType#registerImport(java.util.Set)
     */
    @Override
    public void registerImport(Set<ClassType> classImports) {
        for (JClass c: parents)
            classImports.add(c.getType());
    }

    /**
     * @see tendril.codegen.generics.CompoundGeneric#asClassType(java.lang.Object)
     */
    @Override
    protected ClassType asClassType(JClass type) {
        return type.getType();
    }

    /**
     * @see tendril.codegen.generics.CompoundGeneric#asCode(java.lang.Object)
     */
    @Override
    protected String asCode(JClass type) {
        return type.generateCode();
    }

    
}
