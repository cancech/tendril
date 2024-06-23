package tendril.codegen.classes;

import tendril.codegen.VisibilityType;
import tendril.metadata.classes.ClassData;

public class JClassFactory {

    public static JClass createClass(VisibilityType visibility, ClassData data) {
        return new JClassDefault(visibility, data);
    }

    public static JClass createAbstractClass(VisibilityType visibility, ClassData data) {
        return new JClassAbstract(visibility, data);
    }

    public static JClass createInterface(VisibilityType visibility, ClassData data) {
        return new JClassInterface(visibility, data);
    }

    public static JClass createAnnotation(VisibilityType visibility, ClassData data) {
        return new JClassAnnotation(visibility, data);
    }
}
