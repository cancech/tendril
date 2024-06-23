package tendril.codegen.field;

import java.util.Set;

import tendril.metadata.classes.ClassData;

public class JValueString extends JValue<String> {
    
    JValueString(String value) {
        super(value);
    }

    @Override
    public String generate(Set<ClassData> classImports) {
        return "\"" + value + "\"";
    }
}