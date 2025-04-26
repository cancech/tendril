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

import tendril.codegen.CodeGenerationException;
import tendril.codegen.field.type.ClassType;

/**
 * Representation of a generic where a "no-name" generic extends from one (or more) parent classes/interfaces.
 */
class CompoundExtendsWildcardGeneric extends CompoundExtendsGeneric {

    /**
     * CTOR
     * 
     * @param parents {@link List} of {@link ClassType}s from which the generic extends
     */
    CompoundExtendsWildcardGeneric(List<ClassType> parents) {
        super(SimpleWildcardGeneric.WILD_CARD, parents);
    }
    
    /**
     * @see tendril.codegen.generics.CompoundGeneric#generateDefinition()
     */
    @Override
    public String generateDefinition() {
        throw new CodeGenerationException("A wildcard generic cannot be used in a definition");
    }
    
    /**
     * @see tendril.codegen.generics.GenericType#generateApplication()
     */
    @Override
    public String generateApplication() {
        return super.generateDefinition();
    }
    
    /**
     * @see tendril.codegen.generics.CompoundGeneric#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CompoundExtendsWildcardGeneric))
            return false;
        
        return super.equals(obj);
    }
}
