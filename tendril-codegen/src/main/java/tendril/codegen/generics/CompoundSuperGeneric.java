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

import java.util.Collections;

import tendril.codegen.CodeGenerationException;
import tendril.codegen.field.type.ClassType;

/**
 * Representation of a generic which employs the super keyword
 */
class CompoundSuperGeneric extends CompoundGeneric {

    /**
     * CTOR
     * 
     * @param parent {@link ClassType} that is the parent (i.e.: "super") class
     */
    CompoundSuperGeneric(ClassType parent) {
        super(SimpleWildcardGeneric.WILD_CARD, Collections.singletonList(parent));
    }

    /**
     * @see tendril.codegen.generics.CompoundGeneric#getKeyword()
     */
    @Override
    protected String getKeyword() {
        return "super ";
    }
    
    /**
     * @see tendril.codegen.generics.CompoundGeneric#generateDefinition()
     */
    @Override
    public String generateDefinition() {
        throw new CodeGenerationException("Generic definition cannot use super");
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
        if (!(obj instanceof CompoundSuperGeneric))
            return false;
        
        return super.equals(obj);
    }
}
