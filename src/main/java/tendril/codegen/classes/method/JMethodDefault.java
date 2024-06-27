package tendril.codegen.classes.method;

import tendril.codegen.VisibilityType;
import tendril.dom.method.MethodElement;
import tendril.dom.type.Type;

public class JMethodDefault<METADATA extends Type> extends JMethod<METADATA> {

    public JMethodDefault(VisibilityType visibility, MethodElement<METADATA> methodData, String[] implementation) {
        super(visibility, methodData, implementation);
    }

    @Override
    protected String generateSignatureStart(boolean hasImplementation) {
        String start = VisibilityType.PACKAGE_PRIVATE == visibility ? "" : visibility.toString() + " ";
        if (!hasImplementation)
            return start + "abstract ";
        return start;
    }

}
