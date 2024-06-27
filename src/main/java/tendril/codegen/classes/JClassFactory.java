package tendril.codegen.classes;

import tendril.codegen.VisibilityType;
import tendril.dom.type.core.ClassType;

public class JClassFactory {

    public static JClass createClass(VisibilityType visibility, ClassType data) {
        return new JClassDefault(visibility, data);
    }

    public static JClass createAbstractClass(VisibilityType visibility, ClassType data) {
        return new JClassAbstract(visibility, data);
    }

    public static JClass createInterface(VisibilityType visibility, ClassType data) {
        return new JClassInterface(visibility, data);
    }

    public static JClass createAnnotation(VisibilityType visibility, ClassType data) {
        return new JClassAnnotation(visibility, data);
    }
}
