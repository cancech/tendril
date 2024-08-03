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
package tendril.codegen.classes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import tendril.codegen.CodeBuilder;
import tendril.codegen.classes.method.JMethod;
import tendril.codegen.field.JField;
import tendril.codegen.field.JVisibleType;
import tendril.codegen.field.type.ClassType;

/**
 * Representation of a class, the core construct of any generated code
 */
public abstract class JClass extends JVisibleType<ClassType> {
    /** The imports that this class requires, assembled/collected as part of code generation */
    private final Set<ClassType> imports = new HashSet<>();
    /** The fields that appear in this class */
    private final List<JField<?>> fields = new ArrayList<>();
    /** The methods that this class is composed of */
    private final List<JMethod<?>> methods = new ArrayList<>();
    /** The name of the package in which this class appears */
    private final String pkg;

    /**
     * CTOR
     * 
     * @param data {@link ClassType} the information about the class
     */
    protected JClass(ClassType data) {
        super(data, data.getClassName());
        this.pkg = data.getPackageName();
    }

    /**
     * Add a field to the class
     * 
     * @param field {@link JField} to add
     */
    public void addField(JField<?> field) {
        fields.add(field);
    }

    /**
     * Add a new method to the class. It is intended for this to be used by the {@link MethodBuilder}.
     * 
     * @param method {@link JMethod} to add to the class
     */
    public void addMethod(JMethod<?> method) {
        methods.add(method);
    }

    /**
     * Generate the code which represents this class.
     * 
     * @return {@link String} the code for the class
     */
    public String generateCode() {
        // Generate the class body
        CodeBuilder body = new CodeBuilder();
        generate(body, imports);

        // Generate the package and import statements
        CodeBuilder preamble = new CodeBuilder();
        preamble.append("package " + pkg + ";");
        preamble.blankLine();
        addImports(preamble);
        preamble.blankLine();

        // Combine it to build the whole class
        return preamble.get() + body.get();
    }

    /**
     * Process the registered imports to generate the import code
     * 
     * @param builder {@link CodeBuilder} where to populate the import statements
     */
    private void addImports(CodeBuilder builder) {
        List<ClassType> sortedImports = new ArrayList<>(imports);
        sortedImports.sort((l, r) -> l.getFullyQualifiedName().compareTo(r.getFullyQualifiedName()));
        for (ClassType toImport : sortedImports) {
            String importPkg = toImport.getPackageName();
            if (importPkg == null || importPkg.isBlank() || importPkg.equals(pkg) || importPkg.equals("java.lang"))
                continue;
            builder.append("import " + toImport.getFullyQualifiedName() + ";");
        }
    }

    /**
     * @see tendril.codegen.JBase#appendSelf(tendril.codegen.CodeBuilder, java.util.Set)
     */
    @Override
    protected void appendSelf(CodeBuilder builder, Set<ClassType> classImports) {
        builder.appendMultiLine(generateSelf(classImports));
    }

    /**
     * @see tendril.codegen.JBase#generateSelf(java.util.Set)
     */
    @Override
    public String generateSelf(Set<ClassType> classImports) {
        CodeBuilder builder = new CodeBuilder();
        String visStr = visibility.toString();
        if (!visStr.isEmpty())
            visStr += " ";
        builder.append(visStr + classType() + " " + name + " {");
        builder.blankLine();
        builder.indent();

        // Process fields
        for (JField<?> f : fields) {
            f.generate(builder, classImports);
            builder.blankLine();
        }

        // Process methods
        for (JMethod<?> m : methods) {
            m.generate(builder, imports);
            builder.blankLine();
        }

        builder.deIndent();
        builder.append("}");
        builder.blankLine();
        return builder.get();
    }

    /**
     * Produce the {@link String} name/representation for this class
     * 
     * @return {@link String} declaration construct for the class
     */
    protected abstract String classType();
}
