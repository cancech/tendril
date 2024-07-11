package tendril.codegen.field.type;

import tendril.dom.type.core.ClassType;
import tendril.dom.type.core.PoDType;
import tendril.dom.type.core.VoidType;

/**
 * Factory to facilitate the creation of {@link TypeData} instances
 */
public class TypeDataFactory {

    /**
     * Creates a void {@link TypeData}
     * 
     * @return {@link TypeData}
     */
    public static TypeData<VoidType> create() {
        return new TypeDataVoid();
    }

    /**
     * Creates a {@link TypeData} for a class or other declared type
     * 
     * @param type {@link Class} representing the declared type
     * @return {@link TypeData}
     */
    public static TypeData<ClassType> create(Class<?> type) {
        return new TypeDataDeclared(type);
    }

    /**
     * Creates a {@link TypeData} for a class or other declared type
     * 
     * @param type {@link ClassType} representing the declared type
     * @return {@link TypeData}
     */
    public static TypeData<ClassType> create(ClassType type) {
        return new TypeDataDeclared(type);
    }

    /**
     * Creates a {@link TypeData} for a plain ol' data type
     * 
     * @param type {@link PoDType} representing the POD type
     * @return {@link TypeData}
     */
    public static TypeData<PoDType> create(PoDType type) {
        return new TypeDataPoD(type);
    }
}
