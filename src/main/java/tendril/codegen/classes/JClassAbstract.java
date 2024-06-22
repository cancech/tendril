package tendril.codegen.classes;

import tendril.codegen.VisibilityType;
import tendril.codegen.classes.method.JMethod;
import tendril.codegen.classes.method.JMethodDefault;
import tendril.metadata.ClassData;
import tendril.metadata.TypeData;

public class JClassAbstract extends JClassDefault {

    protected JClassAbstract(VisibilityType visibility, ClassData data) {
        super(visibility, data);
    }

    @Override
    protected String classType() {
        return "abstract " + super.classType();
    }

    @Override
    protected JMethod validateAndCreateMethod(VisibilityType visibility, TypeData returnType, String name, String[] implementation) {
        if(VisibilityType.PRIVATE == visibility && implementation == null)
            return null;
        
        return new JMethodDefault(visibility, returnType, name, implementation);
    }
}
