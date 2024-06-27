package tendril.codegen.field;

import java.util.Set;

import tendril.dom.type.core.ClassType;

public class JValuePoD<N> extends JValue<N> {
    JValuePoD(N value) {
        super(value);
    }

    @Override
    public String generate(Set<ClassType> classImports) {
        return value.toString();
    }
}