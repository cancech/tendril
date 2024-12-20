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

import java.util.Set;

import tendril.codegen.field.type.ClassType;

/**
 * 
 */
class GenericExtendedType extends GenericType {
    
    private final ClassType parent;

    GenericExtendedType(ClassType parent) {
        super("?");
        this.parent = parent;
    }

    GenericExtendedType(String name, ClassType parent) {
        super(name);
        this.parent = parent;
    }
    
    protected String getKeyword() {
        return "extends ";
    }

    /**
     * @see tendril.codegen.generics.GenericType#generateDefinition()
     */
    @Override
    public String generateDefinition() {
        if (ExtensionType.SUPER == type)
            throw new IllegalArgumentException("Generic definition cannot use super");
        return super.generateDefinition() + " " + type.getKeyword() + " " + parent.getSimpleName();
    }
    
    /**
     * @see tendril.codegen.generics.GenericType#generateApplication()
     */
    @Override
    public String generateApplication() {
        if (!isWildcard())
            return super.generateApplication();
        
        return getSimpleName() + " " + type.getKeyword() + " " + parent.getSimpleName();
    }
    
    /**
     * @see tendril.codegen.generics.GenericType#registerImport(java.util.Set)
     */
    @Override
    public void registerImport(Set<ClassType> classImports) {
        super.registerImport(classImports);
        classImports.add(parent);
    }
}
