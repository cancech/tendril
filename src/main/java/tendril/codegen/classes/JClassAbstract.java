package tendril.codegen.classes;

import tendril.codegen.VisibilityType;
import tendril.codegen.classes.method.JMethod;
import tendril.codegen.classes.method.JMethodDefault;
import tendril.dom.method.MethodElement;
import tendril.dom.type.Type;
import tendril.dom.type.core.ClassType;

public class JClassAbstract extends JClassDefault {

    protected JClassAbstract(VisibilityType visibility, ClassType data) {
        super(visibility, data);
    }

    @Override
    protected String classType() {
        return "abstract " + super.classType();
    }

    @Override
    protected <METADATA extends Type> JMethod<METADATA> validateAndCreateMethod(VisibilityType visibility, MethodElement<METADATA> methodData, String[] implementation) {
        if(VisibilityType.PRIVATE == visibility && implementation == null)
            return null;
        
        return new JMethodDefault<METADATA>(visibility, methodData, implementation);
    }
}
