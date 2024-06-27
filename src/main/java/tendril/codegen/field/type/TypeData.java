package tendril.codegen.field.type;

import java.util.Set;

import tendril.dom.classes.Importable;
import tendril.dom.type.Type;
import tendril.dom.type.TypedElement;
import tendril.dom.type.core.ClassType;
import tendril.dom.type.value.ValueElement;

public abstract class TypeData<DATA_TYPE extends Type> implements Importable, TypedElement<DATA_TYPE> {

    protected final DATA_TYPE type;
    private final String name;
    
    protected TypeData(DATA_TYPE type, String name) {
        this.type = type;
        this.name = name;
    }
    
    public boolean isVoid() {
        return false;
    }
    
    public DATA_TYPE getDataType() {
        return type;
    }
    
    public String getSimpleName() {
        return name;
    }
    
    @Override
    public void registerImport(Set<ClassType> classImports) {
    }
    
    public abstract ValueElement<DATA_TYPE, ?> asValue(Object value);
}
