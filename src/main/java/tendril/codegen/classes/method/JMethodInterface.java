package tendril.codegen.classes.method;

import tendril.codegen.VisibilityType;
import tendril.metadata.MethodData;

public class JMethodInterface<METADATA> extends JMethod<METADATA> {

    public JMethodInterface(VisibilityType visibility, MethodData<METADATA> methodData, String[] implementation) {
        super(visibility, methodData, implementation);
    }

    @Override
    protected String generateSignatureStart(boolean hasImplementation) {
        if (VisibilityType.PUBLIC == visibility)
            return hasImplementation ? "default " : "";
        
        return visibility.toString() + " ";
    }

}
