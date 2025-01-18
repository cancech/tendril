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
import tendril.util.TendrilStringUtil;

/**
 * Representation of a generic which is built up from a number of elements (i.e.: not a single "word").
 */
abstract class CompoundGeneric extends SimpleGeneric {
    
    /** {@link List} of {@link ClassType}s which are parents for the generic type */
    protected final List<ClassType> parents;
    
    /**
     * CTOR
     * 
     * @param name {@link String} to apply to the generic
     * @param parents {@link List} of {@link ClassType}s which are parents for the type
     */
    CompoundGeneric(String name, List<ClassType> parents) {
        super(name);
        this.parents = parents;
    }
    
    /**
     * @see tendril.codegen.generics.GenericType#registerImport(java.util.Set)
     */
    @Override
    public void registerImport(Set<ClassType> classImports) {
        classImports.addAll(parents);
    }

    /**
     * Get the keyword that is to be used to describe the relationship between the generic type and the parent(s)
     * 
     * @return {@link String} keyword
     */
    protected abstract String getKeyword();
    
    /**
     * @see tendril.codegen.generics.GenericType#generateDefinition()
     */
    @Override
    public String generateDefinition() {
        String parentsCode = TendrilStringUtil.join(parents, " & ", (p) -> p.getSimpleName());
        return super.generateDefinition() + " " + getKeyword() + parentsCode;
    }
}
