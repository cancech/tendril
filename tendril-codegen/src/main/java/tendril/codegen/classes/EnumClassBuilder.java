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
package tendril.codegen.classes;

import java.util.ArrayList;
import java.util.List;

import tendril.codegen.DefinitionException;
import tendril.codegen.VisibilityType;
import tendril.codegen.classes.method.JConstructor;
import tendril.codegen.field.type.ClassType;

/**
 * {@link ClassBuilder} which allows for the creation of {@link Enum} classes
 */
public class EnumClassBuilder extends ConcreteClassBuilder {
    
    /** List of enumerations that are to be contained within the enum */
    private final List<EnumerationEntry> entries = new ArrayList<>();

    /**
     * CTOR
     * 
     * @param type {@link ClassType} indicating the type of class to build
     */
    EnumClassBuilder(ClassType type) {
        super(type);
        setFinal(true);
    }

    /**
     * @see tendril.codegen.classes.ConcreteClassBuilder#validate()
     */
    @Override
    protected void validate() {
        super.validate();

        // Verify the Enum characteristics
        if (isStatic)
            throwException("Enum cannot be static");
        if (!isFinal)
            throwException("Enum must be final");
        if (parent != null)
            throwException("Enum cannot have any explicit parent class");
        
        // Verify the constructors have the proper visibility
        for (JConstructor c: ctors) {
            VisibilityType ctorVis = c.getVisibility();
            if (ctorVis != VisibilityType.PACKAGE_PRIVATE && ctorVis != VisibilityType.PRIVATE)
                throwException("Enum Constructors must be either private or package private");
        }
        
        // Ensure that each enumeration has a unique name
        for (int i = 0; i < entries.size() - 1; i++) {
            String lhs = entries.get(i).getName();
            for (int j = i+1; j < entries.size(); j++) {
                if (lhs.equals(entries.get(j).getName()))
                    throwException("Enum entries must have unique names, multiple " + lhs + " are present");
            }
        }
    }
    
    /**
     * Helper to streamline the throwing of validation exceptions
     * 
     * @param reason {@link String} why the exception is being thrown
     */
    private void throwException(String reason) {
        throw new DefinitionException(type, reason);
    }
    
    /**
     * @see tendril.codegen.classes.ConcreteClassBuilder#create()
     */
    @Override
    protected JClass create() {
        JClassEnum cls = new JClassEnum(type);
        entries.forEach(e -> cls.add(e));
        return cls;
    }
    
    /**
     * @see tendril.codegen.classes.ClassBuilder#buildEnumeration(java.lang.String)
     */
    @Override
    public EnumerationBuilder buildEnumeration(String name) {
        return new EnumerationBuilder(this, name);
    }
    
    /**
     * @see tendril.codegen.classes.ClassBuilder#add(tendril.codegen.classes.EnumerationEntry)
     */
    @Override
    void add(EnumerationEntry enumEntry) {
        entries.add(enumEntry);
    }
}
