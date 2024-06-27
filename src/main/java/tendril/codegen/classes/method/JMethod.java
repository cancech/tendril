package tendril.codegen.classes.method;

import java.util.Set;

import tendril.codegen.BaseElement;
import tendril.codegen.CodeBuilder;
import tendril.codegen.VisibilityType;
import tendril.codegen.field.type.TypeData;
import tendril.dom.method.MethodElement;
import tendril.dom.type.Type;
import tendril.dom.type.core.ClassType;
import tendril.util.TendrilStringUtil;

public abstract class JMethod<METADATA extends Type> extends BaseElement {

    protected final VisibilityType visibility;
    protected final MethodElement<METADATA> methodData;
    protected final String[] implementation;

    protected JMethod(VisibilityType visibility, MethodElement<METADATA> methodData, String[] implementation) {
        super(methodData.getName());
        this.visibility = visibility;
        this.methodData = methodData;
        this.implementation = implementation;
    }

    @Override
    protected void generateSelf(CodeBuilder builder, Set<ClassType> classImports) {
        methodData.getType().registerImport(classImports);

        boolean hasImplementation = implementation != null;
        builder.append(generateSignature(hasImplementation));

        if (hasImplementation) {
            builder.indent();
            for (String s : implementation)
                builder.append(s);
            builder.deIndent();
            builder.append("}");
        }
    }

    protected TypeData<METADATA> getReturnType() {
        return methodData.getType();
    }
    
    private String generateSignature(boolean hasImplementation) {
        StringBuilder signature = new StringBuilder(generateSignatureStart(hasImplementation));
        signature.append(getReturnType().getSimpleName() + " " + name);
        signature.append("(" + generateParameters() + ")");
        signature.append(hasImplementation ? " {" : ";");
        return signature.toString();
    }

    private String generateParameters() {
        return TendrilStringUtil.join(methodData.getParameters(), param -> {
            return param.getType().getSimpleName() + " " + param.getName();
        });
    }

    protected abstract String generateSignatureStart(boolean hasImplementation);
}
