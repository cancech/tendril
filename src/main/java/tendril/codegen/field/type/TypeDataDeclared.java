package tendril.codegen.field.type;

import java.util.Set;

import tendril.dom.type.core.ClassType;
import tendril.dom.type.value.ValueElement;

class TypeDataDeclared extends TypeData<ClassType> {

    TypeDataDeclared(Class<?> type) {
        this(new ClassType(type));
    }

    protected TypeDataDeclared(ClassType type) {
        super(type, type.getClassName());
    }

    @Override
    public void registerImport(Set<ClassType> classImports) {
        classImports.add(getDataType());
    }

    @Override
    public ValueElement<ClassType, Object> asValue(Object value) {
        return new ValueElement<>(type, value);
    }
}
