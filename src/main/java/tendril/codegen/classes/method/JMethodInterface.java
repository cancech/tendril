package tendril.codegen.classes.method;

import tendril.codegen.VisibilityType;
import tendril.dom.method.MethodElement;
import tendril.dom.type.Type;

public class JMethodInterface<METADATA extends Type> extends JMethod<METADATA> {

    public JMethodInterface(VisibilityType visibility, MethodElement<METADATA> methodData, String[] implementation) {
        super(visibility, methodData, implementation);
    }

    @Override
    protected String generateSignatureStart(boolean hasImplementation) {
        if (VisibilityType.PUBLIC == visibility)
            return hasImplementation ? "default " : "";
        
        return visibility.toString() + " ";
    }

}
