package tendril.codegen.classes;

import tendril.codegen.VisibilityType;
import tendril.codegen.classes.method.JMethod;
import tendril.codegen.classes.method.JMethodInterface;
import tendril.metadata.MethodData;
import tendril.metadata.classes.ClassData;

public class JClassInterface extends JClass {

    protected JClassInterface(VisibilityType visibility, ClassData data) {
        super(visibility, data);
    }

    @Override
    protected String classType() {
        return "interface";
    }

    @Override
    protected <METADATA> JMethod<METADATA> validateAndCreateMethod(VisibilityType visibility, MethodData<METADATA> methodData, String[] implementation) {
        if (VisibilityType.PUBLIC == visibility || (VisibilityType.PRIVATE == visibility && implementation != null))
            return new JMethodInterface<METADATA>(visibility, methodData, implementation);

        return null;
    }

}
