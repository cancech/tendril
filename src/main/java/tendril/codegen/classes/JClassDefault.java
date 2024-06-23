package tendril.codegen.classes;

import tendril.codegen.VisibilityType;
import tendril.codegen.classes.method.JMethod;
import tendril.codegen.classes.method.JMethodDefault;
import tendril.metadata.MethodData;
import tendril.metadata.classes.ClassData;

public class JClassDefault extends JClass {

    protected JClassDefault(VisibilityType visibility, ClassData data) {
        super(visibility, data);
    }

    @Override
    protected String classType() {
        return "class";
    }

    @Override
    protected <METADATA> JMethod<METADATA> validateAndCreateMethod(VisibilityType visibility, MethodData<METADATA> data, String[] implementation) {
        if(implementation == null)
            return null;
        
        return new JMethodDefault<METADATA>(visibility, data, implementation);
    }

}
