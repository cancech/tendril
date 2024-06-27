package tendril.codegen.field;

import java.util.Set;

import tendril.dom.type.core.ClassType;

public class JValueChar extends JValue<Character> {

    JValueChar(Character value) {
        super(value);
    }

    @Override
    public String generate(Set<ClassType> classImports) {
        return "\'" + value + "\'";
    }
}
