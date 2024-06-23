package tendril.metadata.field.type;

import java.util.Set;

import tendril.metadata.classes.ClassData;
import tendril.metadata.field.ValueData;

class TypeDataDeclared extends TypeData<ClassData> {

    TypeDataDeclared(Class<?> type) {
        this(new ClassData(type));
    }

    protected TypeDataDeclared(ClassData type) {
        super(type, type.getClassName());
    }

    @Override
    public void registerImport(Set<ClassData> classImports) {
        classImports.add(getMetaData());
    }

    @Override
    public ValueData<ClassData, Object> asValue(Object value) {
        return new ValueData<>(type, value);
    }
}
