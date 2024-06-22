package tendril.codegen.classes.method;

import tendril.codegen.VisibilityType;
import tendril.metadata.TypeData;

public class JMethodDefault extends JMethod {

    public JMethodDefault(VisibilityType visibility, TypeData returnType, String name, String[] implementation) {
        super(visibility, returnType, name, implementation);
    }

    @Override
    protected String generateSignature(boolean hasImplementation) {
        String signature = VisibilityType.PACKAGE_PRIVATE == visibility ? "" : visibility.toString() + " ";
        if (!hasImplementation)
            signature += "abstract ";
        return signature + returnType.getName() + " " + name + "()" + (hasImplementation ? " {" : ";");
    }

}
