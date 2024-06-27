package tendril.codegen.field;

import java.util.Set;

import tendril.dom.type.core.ClassType;

public class JValueString extends JValue<String> {
    
    JValueString(String value) {
        super(value);
    }

    @Override
    public String generate(Set<ClassType> classImports) {
        return "\"" + value + "\"";
    }
}