package tendril.codegen.classes;

import tendril.codegen.VisibilityType;
import tendril.codegen.classes.method.JMethod;
import tendril.codegen.classes.method.JMethodDefault;
import tendril.metadata.ClassData;
import tendril.metadata.TypeData;

public class JClassDefault extends JClass {

    protected JClassDefault(VisibilityType visibility, ClassData data) {
        super(visibility, data);
    }

    @Override
    protected String classType() {
        return "class";
    }

    @Override
    protected JMethod validateAndCreateMethod(VisibilityType visibility, TypeData returnType, String name, String[] implementation) {
        if(implementation == null)
            return null;
        
        return new JMethodDefault(visibility, returnType, name, implementation);
    }

}
