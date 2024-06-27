package tendril.codegen.field;

import java.util.Set;

import tendril.dom.type.core.ClassType;

public abstract class JValue<TYPE> {

    protected final TYPE value;
    
    protected JValue(TYPE value) {
        this.value = value;
    }
    
    public abstract String generate(Set<ClassType> classImports);
}
