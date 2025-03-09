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
import java.util.Set;

import tendril.codegen.CodeBuilder;
import tendril.codegen.field.type.ClassType;

/**
 * {@link Enum} class representation. Unlike other classes, this contains/tracks the various enumeration entries for the enum.
 */
public class JClassEnum extends JClass {
    /** Entries of the enum */
    private final List<EnumerationEntry> entries = new ArrayList<>();

    /**
     * CTOR
     * 
     * @param data {@link ClassType} the information about the class
     */
    protected JClassEnum(ClassType data) {
        super(data);
    }

    /**
     * @see tendril.codegen.classes.JClass#getClassKeyword()
     */
    @Override
    protected String getClassKeyword() {
        return "enum ";
    }
    
    /**
     * Add an enumeration entry
     * 
     * @param entry {@link EnumerationEntry}
     */
    void add(EnumerationEntry entry) {
        entries.add(entry);
    }
    
    /**
     * Get all enumeration which are present
     *
     * @return {@link List} of {@link EnumerationEntry}
     */
    public List<EnumerationEntry> getEnumerations() {
        return entries;
    }
    
    /**
     * Prior to appending the fields, the various enumeration entries are incorporated
     * 
     * @see tendril.codegen.classes.JClass#processFields(tendril.codegen.CodeBuilder, java.util.Set)
     */
    @Override
    protected void processFields(CodeBuilder builder, Set<ClassType> classImports) {
        boolean addEmptyLine = false;
        
        for (int i = 0; i < entries.size(); i++) {
            addEmptyLine = true;
            String terminator = (i < entries.size() - 1) ? "," : ";";
            entries.get(i).generateSelf(builder, classImports, terminator);
        }
        
        if (addEmptyLine)
            builder.blankLine();
        
        super.processFields(builder, classImports);
    }

}
