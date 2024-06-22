package tendril.metadata;

import java.util.Set;

import tendril.codegen.PoDType;

public class TypeData {

    private final ClassData classData;
    private final String name;
    
    public TypeData() {
        this.classData = null;
        this.name = "void";
    }
    
    public TypeData(Class<?> type) {
        this(new ClassData(type));
    }
    
    public TypeData(ClassData type) {
        this.classData = type;
        this.name = type.getClassName();
    }
    
    public TypeData(PoDType type) {
        this.classData = null;
        this.name = type.toString();
    }
    
    public String getName() {
        return name;
    }
    
    public void registerImport(Set<ClassData> classImports) {
        if (classData != null)
            classImports.add(classData);
    }
}
