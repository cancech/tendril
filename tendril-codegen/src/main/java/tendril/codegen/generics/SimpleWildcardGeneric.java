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

import tendril.codegen.CodeGenerationException;

/**
 * {@link GenericType} which represents a situation where a single wildcard is present (i.e: <?>).
 */
class SimpleWildcardGeneric extends SimpleGeneric {

    static final String WILD_CARD = "?";
    
    /**
     * CTOR
     */
    public SimpleWildcardGeneric() {
        super(WILD_CARD);
    }
    
    /**
     * @see tendril.codegen.generics.GenericType#generateDefinition()
     */
    @Override
    public String generateDefinition() {
        throw new CodeGenerationException("A wildcard generic cannot be used in a definition");
    }
    
    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof SimpleWildcardGeneric;
    }
}
