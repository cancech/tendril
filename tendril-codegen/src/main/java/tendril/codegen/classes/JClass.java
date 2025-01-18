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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import tendril.codegen.CodeBuilder;
import tendril.codegen.JBase;
import tendril.codegen.classes.method.JConstructor;
import tendril.codegen.classes.method.JMethod;
import tendril.codegen.field.JField;
import tendril.codegen.field.JVisibleType;
import tendril.codegen.field.type.ClassType;
import tendril.codegen.field.type.Importable;
import tendril.codegen.generics.GenericType;

/**
 * Representation of a class, the core construct of any generated code
 */
public abstract class JClass extends JVisibleType<ClassType> implements Importable {
    /** The fields that appear in this class */
    private final List<JField<?>> fields = new ArrayList<>();
    /** The CTORs that are available for creating instances of the class */
    private final List<JConstructor> ctors = new ArrayList<>();
    /** The methods that this class is composed of */
    private final List<JMethod<?>> methods = new ArrayList<>();
    /** The name of the package in which this class appears */
    private final String pkg;
    /** The parent of the JClass. Null to indicate no explicit parent */
    private JClass extendedClass = null;
    /** The interfaces that the JClass implements */
    private List<JClass> implementedInterfaces = Collections.emptyList();

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
     * @see tendril.codegen.field.JVisibleType#setStatic(boolean)
     */
    @Override
    public void setStatic(boolean isStatic) {
        if (isStatic)
            throw new IllegalArgumentException("Classes cannot be static.");
            
        super.setStatic(isStatic);
    }

    /**
     * Set the (explicit) parent class for this JClass.
     * 
     * @param parent {@link JClass} that this JClass is to extend
     */
    public void setParentClass(JClass parent) {
        extendedClass = parent;
    }

    /**
     * Set the interfaces that this JClass is to implement
     * 
     * @param ifaces {@link List} of {@link JClass}s indicating which interfaces to implement
     */
    public void setParentInterfaces(List<JClass> ifaces) {
        implementedInterfaces = ifaces;
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
     * Add a constructor to the class
     * 
     * @param ctor {@link JConstructor} to add
     */
    public void addConstructor(JConstructor ctor) {
        ctors.add(ctor);
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
        Set<ClassType> imports = new HashSet<>();
        
        CodeBuilder body = new CodeBuilder();
        generate(body, imports);

        // Generate the package and import statements
        CodeBuilder preamble = new CodeBuilder();
        preamble.append("package " + pkg + ";");
        preamble.blankLine();
        addImports(preamble, imports);
        preamble.blankLine();

        // Combine it to build the whole class
        return preamble.get() + body.get();
    }

    /**
     * Process the registered imports to generate the import code
     * 
     * @param builder {@link CodeBuilder} where to populate the import statements
     */
    private void addImports(CodeBuilder builder, Set<ClassType> imports) {
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
     * @see tendril.codegen.field.type.Importable#registerImport(java.util.Set)
     */
    @Override
    public void registerImport(Set<ClassType> classImports) {
        classImports.add(type);
        if (extendedClass != null)
            extendedClass.registerImport(classImports);
        implementedInterfaces.forEach(i -> i.registerImport(classImports));
        for(GenericType gen: getGenerics())
            gen.registerImport(classImports);
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
        registerImport(classImports);
        
        CodeBuilder builder = new CodeBuilder();
        builder.append(visibility.getKeyword() + getFinalKeyword() + getClassKeyword() + name + getGenericsDefinitionKeyword(true) + parentHierarchy() + "{");
        builder.blankLine();
        builder.indent();

        // Process elements within the class
        processElements(builder, classImports, fields);
        processElements(builder, classImports, ctors);
        processElements(builder, classImports, methods);

        builder.deIndent();
        builder.append("}");
        builder.blankLine();
        return builder.get();
    }
    
    /**
     * Process the elements, generate their code, and add them to the builder
     * 
     * @param builder {@link CodeBuilder} where the code for the class is assembled
     * @param classImports {@link Set} of {@link ClassType} where the imports for the class are collected
     * @param elements {@link List} of {@link JBase} extending elements which are to be processed
     */
    private void processElements(CodeBuilder builder, Set<ClassType> classImports, List<? extends JBase> elements) {
        for (JBase el: elements) {
            el.generate(builder, classImports);
            builder.blankLine();
        }
    }

    /**
     * Produce the {@link String} representing the type of class that is being defined class
     * 
     * @return {@link String} declaration construct for the class
     */
    protected abstract String getClassKeyword();

    /**
     * Generate the code for representing the parent hierarchy of this class.
     * 
     * @return {@link String} with the parent hierarchy
     */
    protected String parentHierarchy() {
        String result = generateParentClass();
        
        String implementsHierarchy = generateImplementInterfaces();
        if (!implementsHierarchy.isEmpty())
            result += implementsHierarchy + " ";

        return result;
    }

    /**
     * Generate the code for indicating the parent class (extends)
     * 
     * @return {@link String} with the appropriate extends code
     */
    protected String generateParentClass() {
        if (extendedClass == null)
            return "";

        return "extends " + extendedClass.getAppliedCode(true);
    }

    /**
     * Generate the code for indicating which interfaces are implemented by this class.
     * 
     * @return {@link String} with the appropriate implements code
     */
    protected String generateImplementInterfaces() {
        String code = "";

        for (JClass iface : implementedInterfaces) {
            if (code.isEmpty())
                code = interfaceExtensionKeyword();
            else
                code += ", ";
            
            code +=  iface.getAppliedCode(false);
        }

        return code;
    }
    
    /**
     * Get the code that is to be used when the class is "applied" (i.e.: when it is used in a variable or in
     * the class definition hierarchy).
     * 
     * @param appendSpace boolean true if a space is to be applied to the end of the generated code.
     * 
     * @return {@link String} the code for applying the class
     */
    public String getAppliedCode(boolean appendSpace) {
        return getName() + getGenericsApplicationKeyword(appendSpace);
    }

    /**
     * Get the appropriate keyword to indicate how the interface fits into the hierarchy. By default "implements", override to change as necessary.
     * 
     * @return {@link String} the interface extension keyword
     */
    protected String interfaceExtensionKeyword() {
        return "implements ";
    }
}
