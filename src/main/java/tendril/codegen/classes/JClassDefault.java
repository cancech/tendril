package tendril.codegen.classes;

import tendril.codegen.VisibilityType;
import tendril.codegen.classes.method.JMethod;
import tendril.codegen.classes.method.JMethodDefault;
import tendril.dom.method.MethodElement;
import tendril.dom.type.Type;
import tendril.dom.type.core.ClassType;

public class JClassDefault extends JClass {

    protected JClassDefault(VisibilityType visibility, ClassType data) {
        super(visibility, data);
    }

    @Override
    protected String classType() {
        return "class";
    }

    @Override
    protected <METADATA extends Type> JMethod<METADATA> validateAndCreateMethod(VisibilityType visibility, MethodElement<METADATA> data, String[] implementation) {
        if(implementation == null)
            return null;
        
        return new JMethodDefault<METADATA>(visibility, data, implementation);
    }

}
