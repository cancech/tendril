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

import tendril.codegen.classes.JClass;

/**
 * {@link GenericType} representing a generic that is given an explicit Class for a type (i.e.: <MyClass>)
 */
class SimpleJClassGeneric extends SimpleClassTypeGeneric {

    /** The class that is applied to the generic */
    private final JClass klass;

    /**
     * CTOR
     * 
     * @param klass {@link JClass} applied to the generic
     */
    SimpleJClassGeneric(JClass klass) {
        super(klass.getType());
        this.klass = klass;
    }

    /**
     * @see tendril.codegen.generics.GenericType#generateApplication()
     */
    @Override
    public String generateApplication() {
        return klass.getAppliedCode(false);
    }
}
