package tendril.metadata.field.type;

import java.util.Set;

import tendril.metadata.MetaData;
import tendril.metadata.classes.ClassData;
import tendril.metadata.classes.Importable;
import tendril.metadata.field.ValueData;

public abstract class TypeData<METADATA> implements Importable, MetaData<METADATA> {

    protected final METADATA type;
    private final String name;
    
    protected TypeData(METADATA type, String name) {
        this.type = type;
        this.name = name;
    }
    
    public boolean isVoid() {
        return false;
    }
    
    public METADATA getMetaData() {
        return type;
    }
    
    public String getSimpleName() {
        return name;
    }
    
    @Override
    public void registerImport(Set<ClassData> classImports) {
    }
    
    public abstract ValueData<METADATA, ?> asValue(Object value);
}
