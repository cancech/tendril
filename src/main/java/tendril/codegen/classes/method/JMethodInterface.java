package tendril.codegen.classes.method;

import tendril.codegen.VisibilityType;
import tendril.metadata.TypeData;

public class JMethodInterface extends JMethod {

    public JMethodInterface(VisibilityType visibility, TypeData returnType, String name, String[] implementation) {
        super(visibility, returnType, name, implementation);
    }

    @Override
    protected String generateSignature(boolean hasImplementation) {
        String signature = "";
        
        if (VisibilityType.PUBLIC == visibility)
            signature = hasImplementation ? "default " : "";
        else
            signature = visibility.toString() + " ";
        
        return signature + returnType.getName() + " " + name + "()" + (hasImplementation ? " {" : ";");
    }

}
