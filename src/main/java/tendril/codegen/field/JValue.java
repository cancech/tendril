package tendril.codegen.field;

import java.util.Set;

import tendril.metadata.classes.ClassData;

public abstract class JValue<TYPE> {

    protected final TYPE value;
    
    protected JValue(TYPE value) {
        this.value = value;
    }
    
    public abstract String generate(Set<ClassData> classImports);
}
