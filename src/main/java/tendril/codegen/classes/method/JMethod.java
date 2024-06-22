package tendril.codegen.classes.method;

import java.util.Set;

import tendril.codegen.BaseElement;
import tendril.codegen.CodeBuilder;
import tendril.codegen.VisibilityType;
import tendril.metadata.ClassData;
import tendril.metadata.TypeData;

public abstract class JMethod extends BaseElement {
    protected final VisibilityType visibility;
    protected final TypeData returnType;
    protected final String[] implementation;

    protected JMethod(VisibilityType visibility, TypeData returnType, String name, String[] implementation) {
        super(name);
        this.visibility = visibility;
        this.returnType = returnType;
        this.implementation = implementation;
    }

    @Override
    protected void generateSelf(CodeBuilder builder, Set<ClassData> classImports) {
        returnType.registerImport(classImports);
        
        boolean hasImplementation =  implementation != null;
        builder.append(generateSignature(hasImplementation));
        if (!hasImplementation)
            return;
        
        builder.indent();
        for(String s: implementation)
            builder.append(s);
        builder.deIndent();
        builder.append("}");
    }
    
    protected abstract String generateSignature(boolean hasImplementation);
}
