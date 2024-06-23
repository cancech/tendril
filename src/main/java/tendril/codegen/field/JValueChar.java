package tendril.codegen.field;

import java.util.Set;

import tendril.metadata.classes.ClassData;

public class JValueChar extends JValue<Character> {

    JValueChar(Character value) {
        super(value);
    }

    @Override
    public String generate(Set<ClassData> classImports) {
        return "\'" + value + "\'";
    }
}
