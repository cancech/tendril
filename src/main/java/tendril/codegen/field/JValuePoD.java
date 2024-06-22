package tendril.codegen.field;

import java.util.Set;

import tendril.metadata.ClassData;

public class JValuePoD<N> extends JValue<N> {
    JValuePoD(N value) {
        super(value);
    }

    @Override
    public String generate(Set<ClassData> classImports) {
        return value.toString();
    }
}