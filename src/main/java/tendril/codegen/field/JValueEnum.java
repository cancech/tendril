package tendril.codegen.field;

import java.util.Set;

import tendril.dom.type.core.ClassType;

/**
 * Value that contains a specific enum entry
 * 
 * @param <E> {@link Enum} whose entry is to be stored
 */
public class JValueEnum<E extends Enum<E>> extends JValue<E> {

    /**
     * CTOR
     * 
     * @param value E specific enum entry to store
     */
    JValueEnum(E value) {
        super(value);
    }

    /**
     * @see tendril.codegen.field.JValue#generate(java.util.Set)
     */
    @Override
    public String generate(Set<ClassType> classImports) {
        classImports.add(new ClassType(value.getClass()));
        return value.getClass().getSimpleName() + "." + value.name();
    }
}