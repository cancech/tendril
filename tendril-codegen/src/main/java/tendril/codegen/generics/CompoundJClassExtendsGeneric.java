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

import tendril.codegen.classes.JClass;

/**
 * 
 */
public abstract class CompoundJClassExtendsGeneric extends CompoundJClassGeneric {

    /**
     * @param name
     * @param parents
     */
    CompoundJClassExtendsGeneric(String name, List<JClass> parents) {
        super(name, parents);
    }

    /**
     * @see tendril.codegen.generics.CompoundGeneric#getKeyword()
     */
    @Override
    protected String getKeyword() {
        return "extends ";
    }

}
