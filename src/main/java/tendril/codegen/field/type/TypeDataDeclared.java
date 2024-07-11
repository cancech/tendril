package tendril.codegen.field.type;

import java.util.Set;

import tendril.dom.type.core.ClassType;
import tendril.dom.type.value.ValueElement;

/**
 * {@link TypeData} for a declared element (i.e.: class)
 */
class TypeDataDeclared extends TypeData<ClassType> {

    /**
     * CTOR
     * 
     * @param type {@link Class} which the type represents
     */
    TypeDataDeclared(Class<?> type) {
        this(new ClassType(type));
    }

    /**
     * CTOR
     * 
     * @param type {@link ClassType} which the type represents
     */
    protected TypeDataDeclared(ClassType type) {
        super(type, type.getClassName());
    }

    /**
     * @see tendril.codegen.field.type.TypeData#registerImport(java.util.Set)
     */
    @Override
    public void registerImport(Set<ClassType> classImports) {
        classImports.add(getDataType());
    }

    /**
     * @see tendril.codegen.field.type.TypeData#asValue(java.lang.Object)
     */
    @Override
    public ValueElement<ClassType, Object> asValue(Object value) {
        return new ValueElement<>(type, value);
    }
}
