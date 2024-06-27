package tendril.codegen.classes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Generated;

import tendril.codegen.BaseElement;
import tendril.codegen.CodeBuilder;
import tendril.codegen.Utilities;
import tendril.codegen.VisibilityType;
import tendril.codegen.classes.method.JMethod;
import tendril.codegen.field.JValueFactory;
import tendril.codegen.field.type.TypeData;
import tendril.codegen.field.type.TypeDataFactory;
import tendril.dom.method.MethodElement;
import tendril.dom.type.Type;
import tendril.dom.type.core.ClassType;
import tendril.dom.type.core.PoDType;

public abstract class JClass extends BaseElement {

    private final Set<ClassType> imports = new HashSet<>();
    private final List<JMethod<?>> methods = new ArrayList<>();

    private final VisibilityType visibility;
    private final String pkg;

    protected JClass(VisibilityType visibility, ClassType data) {
        super(data.getClassName());
        this.visibility = visibility;
        this.pkg = data.getPackageName();

        annotate(Generated.class, Map.of("value", JValueFactory.from("tendril"), "date", JValueFactory.from(Utilities.iso8061TimeStamp())));
    }

    public void addMethod(VisibilityType visibility, String name) {
        addMethod(visibility, TypeDataFactory.create(), name, null);
    }

    public void addMethod(VisibilityType visibility, String name, String... implementation) {
        addMethod(visibility, TypeDataFactory.create(), name, implementation);
    }

    public void addMethod(VisibilityType visibility, PoDType returnType, String name) {
        addMethod(visibility, TypeDataFactory.create(returnType), name, null);
    }

    public void addMethod(VisibilityType visibility, PoDType returnType, String name, String... implementation) {
        addMethod(visibility, TypeDataFactory.create(returnType), name, implementation);
    }

    public void addMethod(VisibilityType visibility, Class<?> returnType, String name) {
        addMethod(visibility, TypeDataFactory.create(returnType), name, null);
    }

    public void addMethod(VisibilityType visibility, Class<?> returnType, String name, String... implementation) {
        addMethod(visibility, TypeDataFactory.create(returnType), name, implementation);
    }

    public void addMethod(VisibilityType visibility, ClassType classData, String name) {
        addMethod(visibility, TypeDataFactory.create(classData), name, null);
    }

    public void addMethod(VisibilityType visibility, ClassType classData, String name, String... implementation) {
        addMethod(visibility, TypeDataFactory.create(classData), name, implementation);
    }

    private <METADATA extends Type> void addMethod(VisibilityType visibility, TypeData<METADATA> returnType, String name, String[] implementation) {
        JMethod<METADATA> method = validateAndCreateMethod(visibility, new MethodElement<METADATA>(returnType, name), implementation);
        if (method == null) {
            String returnTypeStr = returnType == null ? "void" : returnType.toString();
            throw new IllegalArgumentException("Unable to add method [" + visibility + " " + returnTypeStr + " " + name + "()] to class " + pkg + "." + name);
        }

        methods.add(method);
    }

    protected abstract <METADATA extends Type> JMethod<METADATA> validateAndCreateMethod(VisibilityType visibility, MethodElement<METADATA> methodData, String[] implementation);

    public String generateCode() {
        CodeBuilder body = new CodeBuilder();
        generate(body, imports);

        CodeBuilder preamble = new CodeBuilder();
        preamble.append("package " + pkg + ";");
        preamble.blankLine();
        addImports(preamble);
        preamble.blankLine();

        return preamble.get() + body.get();
    }

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

    @Override
    protected void generateSelf(CodeBuilder builder, Set<ClassType> imports) {
        builder.append(visibility + " " + classType() + " " + name + " {");
        builder.blankLine();
        builder.indent();

        // Process methods
        for (JMethod<?> m : methods) {
            m.generate(builder, imports);
            builder.blankLine();
        }

        builder.deIndent();
        builder.append("}");
        builder.blankLine();
    }

    protected abstract String classType();
}
