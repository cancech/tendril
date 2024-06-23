package tendril.codegen.classes;

import tendril.codegen.VisibilityType;
import tendril.codegen.classes.method.JMethod;
import tendril.codegen.classes.method.JMethodDefault;
import tendril.metadata.MethodData;
import tendril.metadata.classes.ClassData;

public class JClassAbstract extends JClassDefault {

    protected JClassAbstract(VisibilityType visibility, ClassData data) {
        super(visibility, data);
    }

    @Override
    protected String classType() {
        return "abstract " + super.classType();
    }

    @Override
    protected <METADATA> JMethod<METADATA> validateAndCreateMethod(VisibilityType visibility, MethodData<METADATA> methodData, String[] implementation) {
        if(VisibilityType.PRIVATE == visibility && implementation == null)
            return null;
        
        return new JMethodDefault<METADATA>(visibility, methodData, implementation);
    }
}
