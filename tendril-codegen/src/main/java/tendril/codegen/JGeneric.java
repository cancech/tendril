/*
 * Copyright 2025 Jaroslav Bosak
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
package tendril.codegen;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import tendril.codegen.field.type.ClassType;
import tendril.codegen.generics.GenericType;
import tendril.util.TendrilStringUtil;

/**
 * Representation of generics which have been applied to an element, and provides the appropriate means of generating their code. Separate means are provided if
 * the element is used in a definition (i.e.: defining a class) or application manner (i.e.: using a class as a parameter/field). 
 */
public class JGeneric {
    /** List of the generic types are employed with the element */
    private List<GenericType> generics = new ArrayList<>();

    /**
     * CTOR
     */
    public JGeneric() {
        
    }
    
    /**
     * Add a generic to the item
     * 
     * @param generic {@link GenericType} to add
     */
    public void addGeneric(GenericType generic) {
        generics.add(generic);
    }

    /**
     * Get all applied generics
     * 
     * @return {@link List} of {@link GenericType} that have been applied to the item
     */
    public List<GenericType> getGenerics() {
        return generics;
    }

    /**
     * Get the keyword (code) for the applied generics when used to define the element.
     * 
     * @param blankSpace boolean true if a space is to be returned when empty (always included if text available)
     * 
     * @return {@link String} containing the generics definition
     */
    public String getGenericsDefinitionKeyword(boolean blankSpace) {
        if (generics.isEmpty())
            return blankSpace ? " " : "";

        return "<" + TendrilStringUtil.join(generics, g -> g.generateDefinition()) + "> ";
    }

    /**
     * Get the keyword (code) for the applied generics when used applied the element.
     * 
     * @param appendSpace boolean true if a space is to be appended after the keyword is generated
     * 
     * @return {@link String} containing the generics application
     */
    public String getGenericsApplicationKeyword(boolean appendSpace) {
        String end = appendSpace ? " " : "";
        if (generics.isEmpty())
            return end;

        return "<" + TendrilStringUtil.join(generics, g -> g.generateApplication()) + ">" + end;
    }

    /**
     * Registers the necessary imports for the generics
     * 
     * @param classImports {@link Set} of {@link ClassType}s representing the imports for the code
     */
    protected void registerGenerics(Set<ClassType> classImports) {
        for (GenericType gen : generics)
            gen.registerImport(classImports);
    }
}
