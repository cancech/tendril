package tendril.codegen.classes;

import tendril.codegen.VisibilityType;
import tendril.codegen.classes.method.JMethod;
import tendril.codegen.classes.method.JMethodInterface;
import tendril.dom.method.MethodElement;
import tendril.dom.type.Type;
import tendril.dom.type.core.ClassType;

public class JClassInterface extends JClass {

    protected JClassInterface(VisibilityType visibility, ClassType data) {
        super(visibility, data);
    }

    @Override
    protected String classType() {
        return "interface";
    }

    @Override
    protected <METADATA extends Type> JMethod<METADATA> validateAndCreateMethod(VisibilityType visibility, MethodElement<METADATA> methodData, String[] implementation) {
        if (VisibilityType.PUBLIC == visibility || (VisibilityType.PRIVATE == visibility && implementation != null))
            return new JMethodInterface<METADATA>(visibility, methodData, implementation);

        return null;
    }

}
