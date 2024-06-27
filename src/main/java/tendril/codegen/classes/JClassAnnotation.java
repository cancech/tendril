package tendril.codegen.classes;

import tendril.codegen.VisibilityType;
import tendril.dom.type.core.ClassType;

public class JClassAnnotation extends JClassInterface {

    protected JClassAnnotation(VisibilityType visibility, ClassType data) {
        super(visibility, data);
    }

    @Override
    protected String classType() {
        return "@" + super.classType();
    }
}
