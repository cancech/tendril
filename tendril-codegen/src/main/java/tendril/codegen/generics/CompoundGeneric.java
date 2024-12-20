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

import tendril.codegen.field.type.ClassType;
import tendril.util.TendrilStringUtil;

/**
 * 
 */
abstract class CompoundGeneric<CLASS_TYPE> extends SimpleGeneric {
    
    protected final List<CLASS_TYPE> parents;
    
    CompoundGeneric(String name, List<CLASS_TYPE> parents) {
        super(name);
        this.parents = parents;
    }

    protected abstract String getKeyword();
    
    protected abstract ClassType asClassType(CLASS_TYPE type);
    
    /**
     * @see tendril.codegen.generics.GenericType#generateDefinition()
     */
    @Override
    public String generateDefinition() {
        String parentsCode = TendrilStringUtil.join(parents, " & ", (p) -> asCode(p));
        return super.generateDefinition() + " " + getKeyword() + parentsCode;
    }
    
    protected abstract String asCode(CLASS_TYPE type);
}
