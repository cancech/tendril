package tendril.metadata.field.type;

import java.util.Set;

import tendril.metadata.TypedElement;
import tendril.metadata.classes.ClassData;
import tendril.metadata.classes.Importable;
import tendril.metadata.field.ValueData;

public abstract class TypeData<DATA_TYPE> implements Importable, TypedElement<DATA_TYPE> {

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
    public void registerImport(Set<ClassData> classImports) {
    }
    
    public abstract ValueData<DATA_TYPE, ?> asValue(Object value);
}
