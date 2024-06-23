package tendril.codegen.classes;

import tendril.codegen.VisibilityType;
import tendril.metadata.classes.ClassData;

public class JClassAnnotation extends JClassInterface {

    protected JClassAnnotation(VisibilityType visibility, ClassData data) {
        super(visibility, data);
    }

    @Override
    protected String classType() {
        return "@" + super.classType();
    }
}
