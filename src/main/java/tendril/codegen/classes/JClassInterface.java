package tendril.codegen.classes;

import tendril.codegen.VisibilityType;
import tendril.codegen.classes.method.JMethod;
import tendril.codegen.classes.method.JMethodInterface;
import tendril.metadata.ClassData;
import tendril.metadata.TypeData;

public class JClassInterface extends JClass {

    protected JClassInterface(VisibilityType visibility, ClassData data) {
        super(visibility, data);
    }

    @Override
    protected String classType() {
        return "interface";
    }

    @Override
    protected JMethod validateAndCreateMethod(VisibilityType visibility, TypeData returnType, String name, String[] implementation) {
        if (VisibilityType.PUBLIC == visibility || (VisibilityType.PRIVATE == visibility && implementation != null))
            return new JMethodInterface(visibility, returnType, name, implementation);
        
        return null;
    }

}
