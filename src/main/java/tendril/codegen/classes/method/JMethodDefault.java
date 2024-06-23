package tendril.codegen.classes.method;

import tendril.codegen.VisibilityType;
import tendril.metadata.MethodData;

public class JMethodDefault<METADATA> extends JMethod<METADATA> {

    public JMethodDefault(VisibilityType visibility, MethodData<METADATA> methodData, String[] implementation) {
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
